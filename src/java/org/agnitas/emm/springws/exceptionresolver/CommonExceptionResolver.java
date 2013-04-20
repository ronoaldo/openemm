package org.agnitas.emm.springws.exceptionresolver;

import java.util.Locale;

import org.agnitas.emm.core.binding.service.BindingNotExistException;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentNotExistException;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentWithSameOrderAlreadyExist;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentWithSameTargetIdAlreadyExist;
import org.agnitas.emm.core.dynname.service.DynamicTagNameNotExistException;
import org.agnitas.emm.core.mailing.service.MailingNotExistException;
import org.agnitas.emm.core.mailing.service.SendDateNotInFutureException;
import org.agnitas.emm.core.mailing.service.TemplateNotExistException;
import org.agnitas.emm.core.mailing.service.WorldMailingAlreadySentException;
import org.agnitas.emm.core.mailinglist.service.MailinglistNotExistException;
import org.agnitas.emm.core.recipient.service.RecipientNotExistException;
import org.agnitas.emm.core.target.service.TargetNotExistException;
import org.apache.log4j.Logger;
import org.springframework.ws.soap.server.endpoint.AbstractSoapFaultDefinitionExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

public class CommonExceptionResolver extends
        AbstractSoapFaultDefinitionExceptionResolver {
	
	private final static Logger logger = Logger.getLogger(CommonExceptionResolver.class);

	protected SoapFaultDefinition getDefaultDefinition(Exception ex) {
		logger.error("Exception", ex);
        SoapFaultDefinition definition = new SoapFaultDefinition();
        definition.setLocale(Locale.getDefault());
        definition.setFaultCode(SoapFaultDefinition.SERVER);
        definition.setFaultStringOrReason(ex.getMessage());
        return definition;
	}
	
    @Override
    protected SoapFaultDefinition getFaultDefinition(Object endpoint, Exception ex) {
    	SoapFaultDefinition definition = getDefaultDefinition(ex);
        if (ex instanceof IllegalArgumentException) {
            definition.setFaultCode(SoapFaultDefinition.CLIENT);
        }
        else if (ex instanceof org.springframework.dao.IncorrectResultSizeDataAccessException) {
        }
        else if (ex instanceof org.springframework.dao.DataAccessException) {
            definition.setFaultStringOrReason("Data access error");
        } else if (ex instanceof MailingNotExistException) {
			definition.setFaultStringOrReason("Unknown mailing ID");
		} else if (ex instanceof DynamicTagContentNotExistException) {
			definition.setFaultStringOrReason("Unknown content ID");
		} else if (ex instanceof DynamicTagNameNotExistException) {
			definition.setFaultStringOrReason("Unknown block name");
		} else if (ex instanceof TargetNotExistException) {
			definition.setFaultStringOrReason("Unknown target id (" + ((TargetNotExistException)ex).getTargetID() + ")");
		} else if (ex instanceof MailinglistNotExistException) {
			definition.setFaultStringOrReason("Unknown mailinglist ID");
		} else if (ex instanceof TemplateNotExistException) {
			definition.setFaultStringOrReason("Unknown template ID");
		} else if (ex instanceof DynamicTagContentWithSameOrderAlreadyExist) {
			definition.setFaultStringOrReason("Content with the same order already exist");
		} else if (ex instanceof DynamicTagContentWithSameTargetIdAlreadyExist) {
			definition.setFaultStringOrReason("Content with the same target id already exist");
		} else if (ex instanceof RecipientNotExistException) {
            definition.setFaultStringOrReason("Unknown customer ID");
        } else if (ex instanceof BindingNotExistException) {
            definition.setFaultStringOrReason("Binding not exist");
        } else if (ex instanceof SendDateNotInFutureException) {
            definition.setFaultStringOrReason("Send date not in future");
        } else if (ex instanceof WorldMailingAlreadySentException) {
            definition.setFaultStringOrReason("World mailing already sent");
        } else {
            definition.setFaultStringOrReason("Unknown error");
        }
        return definition;
    }
}
