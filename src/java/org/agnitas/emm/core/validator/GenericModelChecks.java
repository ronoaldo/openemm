package org.agnitas.emm.core.validator;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResults;
import org.apache.commons.validator.util.ValidatorUtils;
import org.apache.log4j.Logger;

public class GenericModelChecks {
	
	private static Logger logger = Logger.getLogger(GenericModelChecks.class);
	
    /**
     * Checks if the field is required.
     *
     * @return boolean If the field isn't <code>null</code> and
     * has a length greater than zero, <code>true</code> is returned.  
     * Otherwise <code>false</code>.
     */
    public static boolean validateRequired(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

       boolean valid = !GenericValidator.isBlankOrNull(value);
       results.add(field, action.getName(), valid, value);
       return valid;
    }

	public static boolean validateMaxLength(Object bean, Field field, ValidatorResults results, ValidatorAction action) {
		String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
		if (value == null) {
			return true;
		}
		int max = Integer.parseInt(field.getVarValue("maxlength"));

		boolean valid = GenericValidator.maxLength(value, max);
		results.add(field, action.getName(), valid, value);
		return valid;
	}

	public static boolean validateIntRange(Object bean, Field field, ValidatorResults results, ValidatorAction action) {
		String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
		int intValue = Integer.parseInt(value);
		int min = Integer.parseInt(field.getVarValue("min"));
		int max = Integer.parseInt(field.getVarValue("max"));

		boolean valid = GenericValidator.isInRange(intValue, min, max);
		results.add(field, action.getName(), valid, value);
		return valid;
	}

    /**
     * Checks if field is positive assuming it is an integer
     * 
     * @param    value       The value validation is being performed on.
     * @param    field       Description of the field to be evaluated
     * @return   boolean     If the integer field is greater than zero, returns
     *                        true, otherwise returns false.
     */
    public static boolean validatePositive(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
    
       boolean valid = GenericTypeValidator.formatInt(value).intValue() > 0;
       results.add(field, action.getName(), valid, value);
       return valid;
    }
    
    /**
     * Checks if the field value is in email format.
     *
     * @return boolean If the field isn't <code>null</code> and
     * has a length greater than zero, <code>true</code> is returned.  
     * Otherwise <code>false</code>.
     */
    public static boolean validateEmail(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
    
       boolean valid = GenericValidator.isEmail(value);
       results.add(field, action.getName(), valid, value);
       return valid;
    }
    
    /**
     * Checks if field is not negative assuming it is an integer
     * 
     * @param    value       The value validation is being performed on.
     * @param    field       Description of the field to be evaluated
     * @return   boolean     If the integer field is greater than or equals zero, returns
     *                        true, otherwise returns false.
     */
    public static boolean validatePositiveOrZero(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
    
       boolean valid = GenericTypeValidator.formatInt(value).intValue() >= 0;
       results.add(field, action.getName(), valid, value);
       return valid;
    }
    
    public static boolean validatePositiveOrZeroCollection(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
    	Collection<?> collection = getValueAsCollection(bean, field.getProperty());
    	boolean valid = true;
    	if (collection != null) {
    		for (Object item : collection) {
    			boolean validItem = (Integer)item >= 0;
    			results.add(field, action.getName(), valid, item);
    			valid = valid && validItem;  
			}
    	}
        return valid;
    }
    
    private static Collection<?> getValueAsCollection(Object bean, String property) {
		Object value = null;
		try {
			value = PropertyUtils.getProperty(bean, property);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(), e);
		}
		if (value == null || !(value instanceof Collection<?>)) {
			return null;
		}
		return (Collection<?>) value;
    }

}
