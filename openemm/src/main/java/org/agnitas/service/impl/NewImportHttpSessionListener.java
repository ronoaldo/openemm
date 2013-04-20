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
package org.agnitas.service.impl;

import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.NewImportWizardForm;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;

/**
 * @author Viktor Gema
 */
public class NewImportHttpSessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        final HttpSession session = httpSessionEvent.getSession();
        if (AgnUtils.isOracleDB()) {
            final String sessionId = session.getId();
            final WebApplicationContext applicatonContext = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
            final ImportRecipientsDao importRecipientsDao = (ImportRecipientsDao) applicatonContext.getBean("ImportRecipientsDao");
            List<String> tableNames = importRecipientsDao.getTemporaryTableNamesBySessionId(sessionId);
            for (String tableName : tableNames) {
                importRecipientsDao.removeTemporaryTable(tableName, sessionId);
            }
        }
        NewImportWizardForm importWizardForm = (NewImportWizardForm) session.getAttribute("newImportWizardForm");
        if (importWizardForm != null){
            NewImportWizardService importWizardHelper = importWizardForm.getImportWizardHelper();
            if (importWizardHelper != null){
                ImportRecipientsDao importRecipientsDao = importWizardHelper.getImportRecipientsDao();
                if (importRecipientsDao != null){
                    SingleConnectionDataSource temporaryConnection = importRecipientsDao.getTemporaryConnection();
                    if (temporaryConnection != null){
                        temporaryConnection.destroy();
                        importRecipientsDao.setTemporaryConnection(null);
                    }
                }
            }
        }
    }
}
