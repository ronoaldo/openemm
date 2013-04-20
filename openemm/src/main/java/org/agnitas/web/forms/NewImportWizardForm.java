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
package org.agnitas.web.forms;

import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.Mailinglist;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.util.ImportReportEntry;
import org.agnitas.web.NewImportWizardAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Viktor Gema
 */
public class NewImportWizardForm extends ImportBaseFileForm {
	private static final long serialVersionUID = -1338333139454802699L;

	private int action;
    private Map<Integer, String> importProfiles;
    private int defaultProfileId;
    private int datasourceId;
    private List<Mailinglist> allMailingLists;
    private List<Integer> listsToAssign;
	private boolean resultPagePrepared;
    private List<Mailinglist> assignedMailingLists;
    private Map<Integer, Integer> mailinglistAssignStats;
    private String calendarDateFormat;
    private String mailinglistAddMessage;
    private NewImportWizardService importWizardHelper;
    private LinkedList<LinkedList<String>> previewParsedContent;
    private int all;
    private File invalidRecipientsFile;
    private File validRecipientsFile;
    private File fixedRecipientsFile;
    private File duplicateRecipientsFile;
    private int downloadFileType;
    private File resultFile;
	private ActionMessages errorsDuringImport;
	
    protected Collection<ImportReportEntry> reportEntries;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Map<Integer, String> getImportProfiles() {
        return importProfiles;
    }

    public void setImportProfiles(Map<Integer, String> importProfiles) {
        this.importProfiles = importProfiles;
    }

	public ActionMessages getErrorsDuringImport() {
		return errorsDuringImport;
	}

	public void setErrorsDuringImport(ActionMessages errorsDuringImport) {
		this.errorsDuringImport = errorsDuringImport;
	}

    public void setDefaultProfileId(int defaultProfileId) {
        this.defaultProfileId = defaultProfileId;
    }

    public int getDefaultProfileId() {
        return defaultProfileId;
    }

    public CustomerImportStatus getStatus() {
        return importWizardHelper.getStatus();
    }

    public void setStatus(CustomerImportStatus status) {
        importWizardHelper.setStatus(status);
    }

    public Collection<ImportReportEntry> getReportEntries() {
        return reportEntries;
    }

    public void setReportEntries(Collection<ImportReportEntry> reportEntries) {
        this.reportEntries = reportEntries;
    }

    public int getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId) {
        this.datasourceId = datasourceId;
    }

    public List<Mailinglist> getAllMailingLists() {
        return allMailingLists;
    }

    public void setAllMailingLists(List<Mailinglist> allMailingLists) {
        this.allMailingLists = allMailingLists;
    }

    public List<Mailinglist> getAssignedMailingLists() {
        return assignedMailingLists;
    }

    public void setAssignedMailingLists(List<Mailinglist> assignedMailingLists) {
        this.assignedMailingLists = assignedMailingLists;
    }

    public Map<Integer, Integer> getMailinglistAssignStats() {
        return mailinglistAssignStats;
    }

    public void setMailinglistAssignStats(Map<Integer, Integer> mailinglistAssignStats) {
        this.mailinglistAssignStats = mailinglistAssignStats;
    }

    public String getCalendarDateFormat() {
        return calendarDateFormat;
    }

    public void setCalendarDateFormat(String calendarDateFormat) {
        this.calendarDateFormat = calendarDateFormat;
    }

    public String getMailinglistAddMessage() {
        return mailinglistAddMessage;
    }

    public void setMailinglistAddMessage(String mailinglistAddMessage) {
        this.mailinglistAddMessage = mailinglistAddMessage;
    }

    public ActionErrors formSpecificValidate(ActionMapping actionMapping, HttpServletRequest request) {
        ActionErrors actionErrors = super.formSpecificValidate(actionMapping, request);
        if (actionErrors == null) {
            actionErrors = new ActionErrors();
        }

        if (action == NewImportWizardAction.ACTION_START) {
            if (importWizardHelper == null) {
                importWizardHelper = (NewImportWizardService) getWebApplicationContext().getBean("NewImportWizardService");
            }
        }
        if (request.getParameter("start_proceed") != null) {
            if (defaultProfileId == 0) {
                actionErrors.add("global", new ActionMessage("error.import.no_profile_exists"));
            }
        }
        return actionErrors;
    }

    public NewImportWizardService getImportWizardHelper() {
        return importWizardHelper;
    }

    public void setImportWizardHelper(NewImportWizardService importWizardHelper) {
        this.importWizardHelper = importWizardHelper;
    }

    public LinkedList<LinkedList<String>> getPreviewParsedContent() {
        return previewParsedContent;
    }

    public void setPreviewParsedContent(LinkedList<LinkedList<String>> previewParsedContent) {
        this.previewParsedContent = previewParsedContent;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public int getAll() {
        return all;
    }

    public File getInvalidRecipientsFile() {
        return invalidRecipientsFile;
    }

    public void setInvalidRecipientsFile(File invalidRecipientsFile) {
        this.invalidRecipientsFile = invalidRecipientsFile;
    }

    public File getValidRecipientsFile() {
        return validRecipientsFile;
    }

    public void setValidRecipientsFile(File validRecipientsFile) {
        this.validRecipientsFile = validRecipientsFile;
    }

    public File getFixedRecipientsFile() {
        return fixedRecipientsFile;
    }

    public void setFixedRecipientsFile(File fixedRecipientsFile) {
        this.fixedRecipientsFile = fixedRecipientsFile;
    }

    public int getDownloadFileType() {
        return downloadFileType;
    }

    public void setDownloadFileType(int downloadFileType) {
        this.downloadFileType = downloadFileType;
    }

    public File getDuplicateRecipientsFile() {
        return duplicateRecipientsFile;
    }

    public void setDuplicateRecipientsFile(File duplicateRecipientsFile) {
        this.duplicateRecipientsFile = duplicateRecipientsFile;
    }

	public List<Integer> getListsToAssign() {
		return listsToAssign;
	}

	public void setListsToAssign(List<Integer> listsToAssign) {
		this.listsToAssign = listsToAssign;
	}

	public boolean isResultPagePrepared() {
		return resultPagePrepared;
	}

	public void setResultPagePrepared(boolean resultPagePrepared) {
		this.resultPagePrepared = resultPagePrepared;
	}

    public File getResultFile() {
        return resultFile;
    }

    public void setResultFile(File resultFile) {
        this.resultFile = resultFile;
    }
}