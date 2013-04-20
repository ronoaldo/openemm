package org.agnitas.emm.core.validator;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.agnitas.emm.core.validator.annotation.Validate;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.xml.sax.SAXException;

@Aspect
public class ModelValidator {
	
	private final static Logger log = Logger.getLogger(ModelValidator.class);
	
    private ResourceBundle messagesBundle;
    private ValidatorResources resources;

    public ModelValidator(String validationFile, String propertiesFile) throws IOException, SAXException {
    	InputStream in = this.getClass().getClassLoader().getResourceAsStream(validationFile);
    	resources = new ValidatorResources(in);

    	messagesBundle = ResourceBundle.getBundle(propertiesFile);
    }
	
	@Before("@annotation(annotation) && args(model)")
	public void validate(Validate annotation, Object model) throws ValidatorException {
		log.debug("formName:"+annotation.value()+", model:"+model.getClass().getName());
		
        Validator validator = new Validator(resources, annotation.value());
        validator.setParameter(Validator.BEAN_PARAM, model);
        ValidatorResults results = validator.validate();

        checkResults(results);

	}

    @SuppressWarnings("unchecked")
    private void checkResults(ValidatorResults results) throws IllegalArgumentException {
        Iterator<String> fieldNames = results.getPropertyNames().iterator();
        while (fieldNames.hasNext()) {
            ValidatorResult result = results.getValidatorResult(fieldNames.next());
            List<String> actions = result.getField().getDependencyList();
            for (int i = 0; i < actions.size(); ++ i) {
                if (!result.isValid(actions.get(i))) {
                    ValidatorAction action = resources.getValidatorAction(actions.get(i));
                    Field field = result.getField();
                    throw new IllegalArgumentException(getErrorMessage(field, action));
                }
            }
        }
    }


    private String getErrorMessage(Field field, ValidatorAction action) {
        // TODO: add processing of an alternative message
        // that can be associated with a Field and configured with a <msg> xml element.
        // See Resources.getActionMessage(validator, request, va, field)) for references.
		String args[] = getArgs(action.getName(), messagesBundle, field);

		String msg = field.getMsg(action.getName()) != null ? field.getMsg(action.getName()) : action.getMsg();

		return MessageFormat.format(getMessage(messagesBundle, msg), (Object[]) args);
	}    
    
	public static String[] getArgs(String actionName, ResourceBundle messages, Field field) {

		String[] argMessages = new String[4];

		Arg[] args = new Arg[] { 
				field.getArg(actionName, 0), 
				field.getArg(actionName, 1), 
				field.getArg(actionName, 2),
				field.getArg(actionName, 3) };

		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) {
				continue;
			}

			if (args[i].isResource()) {
				argMessages[i] = getMessage(messages, args[i].getKey());
			} else {
				argMessages[i] = args[i].getKey();
			}

		}

		return argMessages;
	}
	
	public static String getMessage(ResourceBundle messages, String key) {
		String message = null;

		if (messages != null) {
			message = messages.getString(key);
		}

		return (message == null) ? "" : message;
	}
}
