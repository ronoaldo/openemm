package org.agnitas.emm.core.mailing.service.validator;

import org.agnitas.emm.core.mailing.service.MailingModel;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResults;
import org.apache.commons.validator.util.ValidatorUtils;

public class MailingModelChecks {

    public static boolean validateMailingType(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

       boolean valid = MailingModel.mailingTypeMap.containsKey(value.toLowerCase());
       results.add(field, action.getName(), valid, value);
       return valid;
    }

    public static boolean validateMailingFormat(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

       boolean valid = MailingModel.formatMap.containsKey(value.toLowerCase());
       results.add(field, action.getName(), valid, value);
       return valid;
    }

    public static boolean validateOnePixel(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

       boolean valid = MailingModel.onePixelMap.containsKey(value.toLowerCase());
       results.add(field, action.getName(), valid, value);
       return valid;
    }

    public static boolean validateTargetMode(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

       boolean valid = MailingModel.targetModeMap.containsKey(value.toLowerCase());
       results.add(field, action.getName(), valid, value);
       return valid;
    }

    public static boolean validateMaildropStatus(Object bean, Field field,
            ValidatorResults results, ValidatorAction action) {
       String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

       boolean valid = MailingModel.maildropStatusMap.containsKey(value.toLowerCase());
       results.add(field, action.getName(), valid, value);
       return valid;
    }
    
}
