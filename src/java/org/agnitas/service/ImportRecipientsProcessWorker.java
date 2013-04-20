/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.service;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportRecipientsToolongValueException;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;

import java.util.concurrent.Callable;
import java.io.Serializable;

/**
 * @author viktor 12-May-2010 4:57:43 PM
 */
public class ImportRecipientsProcessWorker implements Callable, Serializable {
    private static final transient Logger logger = Logger.getLogger(ImportRecipientsProcessWorker.class);

    private NewImportWizardService service;
    private ActionMessage message;

    public ImportRecipientsProcessWorker(NewImportWizardService service) {
        this.service = service;
    }

    public Object call() throws Exception {
        try {
            service.doParse();
        } catch (Exception e) {
            logger.error("Error during import: " + e + "\n" + AgnUtils.getStackTrace(e));
            if (e.getCause() instanceof ImportRecipientsToolongValueException) {
                ImportRecipientsToolongValueException exception = (ImportRecipientsToolongValueException) e.getCause();
                message = new ActionMessage("error.import.toolongvalue", substringValueIfTooLong(exception.getFieldValue()));
            } else {
                message = new ActionMessage("error.exception");
            }
        }
        return null;
    }

    private String substringValueIfTooLong(String fieldValue) {
        String returnValue = fieldValue;
        if (fieldValue.length() > 50) {
            returnValue = fieldValue.substring(0, 50);
        }
        return returnValue;
    }

    public ActionMessage getMessage() {
        return message;
    }

    public void setMessage(ActionMessage message) {
        this.message = message;
    }
}
