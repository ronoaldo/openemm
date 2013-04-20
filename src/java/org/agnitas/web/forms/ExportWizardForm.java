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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.web.forms;

import org.agnitas.beans.ExportPredef;
import org.agnitas.beans.Mailinglist;
import org.agnitas.target.Target;
import org.agnitas.web.ExportWizardAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author  mhe
 */
public class ExportWizardForm extends StrutsFormBase {
    
    private static final long serialVersionUID = -1678091898444495551L;

	/** 
     * Holds value of property charset. 
     */
    private String charset="UTF-8";
    
    /**
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property csvFile. 
     */
    private File csvFile;
    
    /**
     * Holds value of property linesOK. 
     */
    private int linesOK;
    
    /** 
     * Holds value of property dbExportStatus. 
     */
    private int dbExportStatus;
    
    /**
     * Holds value of property separator. 
     */
    private String separator;
    
    /**
     * Holds value of property delimiter. 
     */
    private String delimiter;
    
    /**
     * Holds value of property downloadName. 
     */
    private String downloadName;
    
    /**
     * Holds value of property dbExportStatusMessages. 
     */
    private LinkedList<String> dbExportStatusMessages;
    
    /**
     * Holds value of property mailinglists. 
     */
    private String[] mailinglists;
    
    /**
     * Holds value of property targetID. 
     */
    private int targetID;
    
    /**
     * Holds value of property columns. 
     */
    private String[] columns;
    
    /**
     * Holds value of property mailinglistID. 
     */
    private int mailinglistID;
    
    /**
     * Holds value of property userType. 
     */
    private String userType;
    
    /**
     * Holds value of property userStatus. 
     */
    private int userStatus;

    /**
     * Holds value of property collectingData. 
     */
    private boolean collectingData;

	private boolean recipientFilterVisible = true;

	private boolean columnsPanelVisible = true;

	private boolean mlistsPanelVisible = true;

	private boolean fileFormatPanelVisible = true;

	private boolean datesPanelVisible = true;

     /**
     * Holds collection of rows from ExportPredef table.
     */

    private List<ExportPredef> exportPredefList;

     /**
     * Holds number of rows of ExportPredef table.
     */

    private int exportPredefCount;

    private List<Target> targetGroups;

    private List<Mailinglist> mailinglistObjects;

    private String timestampStart;
    private String timestampEnd;

    private String creationDateStart;
    private String creationDateEnd;

    private String mailinglistBindStart;
    private String mailinglistBindEnd;


	/**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        return;
    }
    
    /**
     * Initialization
     */
    public void clearData() {
        this.columns = new String[] {};
        this.mailinglists = new String[] {};
        this.shortname = "";
        this.description = "";
        this.targetID = 0;
        this.mailinglistID = 0;
        this.userStatus = 0;
        this.userType = null;
        this.targetGroups = new ArrayList<Target>();
        this.mailinglistObjects = new ArrayList<Mailinglist>();
        this.timestampStart = "";
        this.timestampEnd = "";
        this.creationDateStart = "";
        this.creationDateEnd = "";
        this.mailinglistBindStart = "";
        this.mailinglistBindEnd = "";
    }
    
	/**
	 * Validate the properties that have been set from this HTTP request,
	 * and return an <code>ActionErrors</code> object that encapsulates any
	 * validation errors that have been found.  If no errors are found,
	 * return <code>null</code> or an <code>ActionErrors</code> object with
	 * no recorded error messages.
	 * 
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 * @return errors
	 */
    
	public ActionErrors formSpecificValidate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
       
		if(action == ExportWizardAction.ACTION_LIST) {
			/* Clear the data on startup */ 
			clearData();
		} else if(action==ExportWizardAction.ACTION_COLLECT_DATA) {
			/* Make sure there were columns selected */ 
			if(request.getParameter("columns")==null) {
				columns=new String[] {};
			}
			if(columns!=null && columns.length==0) {
				errors.add("global", new ActionMessage("error.export.no_columns_selected"));
			}
		}

		return errors;
	}
    
    /**
     * Getter for property charset.
     *
     * @return Value of property charset.
     */
    public String getCharset() {
        return this.charset;
    }
    
    /**
     * Setter for property charset.
     *
     * @param charset New value of property charset.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    /**
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /**
     * Getter for property csvFile.
     *
     * @return Value of property csvFile.
     */
    public File getCsvFile() {
        return this.csvFile;
    }
    
    /** 
     * Setter for property csvFile.
     *
     * @param csvFile New value of property csvFile.
     */
    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }
    
    /**
     * Getter for property linesOK.
     *
     * @return Value of property linesOK.
     */
    public int getLinesOK() {
        return this.linesOK;
    }
    
    /**
     * Setter for property linesOK.
     *
     * @param linesOK New value of property linesOK.
     */
    public void setLinesOK(int linesOK) {
        this.linesOK = linesOK;
    }
    
    /**
     * Getter for property dbExportStatus.
     *
     * @return Value of property dbExportStatus.
     */
    public int getDbExportStatus() {
        return this.dbExportStatus;
    }
    
    /**
     * Setter for property dbExportStatus.
     * 
     * @param dbExportStatus 
     */
    public void setDbExportStatus(int dbExportStatus) {
        this.dbExportStatus = dbExportStatus;
    }
    
    /** 
     * Getter for property separator.
     *
     * @return Value of property separator.
     */
    public String getSeparator() {
        return this.separator;
    }
    
    /** 
     * Setter for property separator.
     *
     * @param separator New value of property separator.
     */
    public void setSeparator(String separator) {
    	if(separator.equals("t")) {
        	this.separator = "\t";
        } else {
        	this.separator=separator;
        }
    }
    
    /** 
     * Setter for property separator.
     *
     * @param separator New value of property separator.
     */
    public void setSeparatorInternal(String separator) {
       	this.separator=separator;
    }
    
    /**
     * Getter for property delimitor.
     *
     * @return Value of property delimitor.
     */
    public String getDelimiter() {
        return this.delimiter;
    }
    
    /**
     * Setter for property delimitor.
     * 
     * @param delimiter 
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    
    /**
     * Getter for property downloadName.
     *
     * @return Value of property downloadName.
     */
    public String getDownloadName() {
        return this.downloadName;
    }
    
    /**
     * Setter for property downloadName.
     *
     * @param downloadName New value of property downloadName.
     */
    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }
    
    /**
     * Getter for property dbInsertStatusMessages.
     *
     * @return Value of property dbInsertStatusMessages.
     */
    public LinkedList<String> getDbExportStatusMessages() {
        return this.dbExportStatusMessages;
    }
    
    /**
     * Setter for property dbInsertStatusMessages.
     * 
     * @param dbExportStatusMessages 
     */
    public void setDbExportStatusMessages(LinkedList<String> dbExportStatusMessages) {
        this.dbExportStatusMessages = dbExportStatusMessages;
    }
    
    /**
     * Adds a database export status message.
     */
    public void addDbExportStatusMessage(String message) {
        if(this.dbExportStatusMessages==null) {
            this.dbExportStatusMessages=new LinkedList<String>();
        }
        
        this.dbExportStatusMessages.add(message);
    }
    
    /**
     * Getter for property mailinglists.
     *
     * @return Value of property mailinglists.
     */
    public String[] getMailinglists() {
        return this.mailinglists;
    }
    
    /** 
     * Setter for property mailinglists.
     *
     * @param mailinglists New value of property mailinglists.
     */
    public void setMailinglists(String[] mailinglists) {
        this.mailinglists = mailinglists;
    }
    
    /** 
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /** 
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    /**
     * Getter for property columns.
     *
     * @return Value of property columns
     */
    public String[] getColumns() {
        return this.columns;
    }
    
    /**
     * Setter for property columns.
     *
     * @param columns New value of property columns.
     */
    public void setColumns(String[] columns) {
        this.columns = columns;
    }
    
    /**
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
    public int getMailinglistID() {
        return this.mailinglistID;
    }
    
    /**
     * Setter for property mailinglistID.
     *
     * @param mailinglistID New value of property mailinglistID.
     */
    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID = mailinglistID;
    }
    
    /**
     * Getter for property userType.
     *
     * @return Value of property userType.
     */
    public String getUserType() {
        return this.userType;
    }
    
    /**
     * Setter for property userType.
     *
     * @param userType New value of property userType.
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    /**
     * Getter for property userStatus.
     *
     * @return Value of property userStatus.
     */
    public int getUserStatus() {
        return this.userStatus;
    }
    
    /**
     * Setter for property userStatus.
     *
     * @param userStatus New value of property userStatus.
     */
    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * Checks if collectingData exists.
     */
    public synchronized boolean tryCollectingData() {
        if(this.collectingData) {
            return false;
        } else {
            this.collectingData=true;
            return true;
        }
    }
    
    /**
     * Resets collectingData
     */
    public synchronized void resetCollectingData() {
        this.collectingData=false;
    }

    /**
     * Holds value of property shortname.
     */
    private String shortname="";

    /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    public String getShortname() {

        return this.shortname;
    }

    /**
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {

        this.shortname = shortname;
    }

    /**
     * Holds value of property description.
     */
    private String description;

    /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Holds value of property exportPredefID.
     */
    private int exportPredefID;

    /**
     * Getter for property exportPredefID.
     *
     * @return Value of property exportPredefID.
     */
    public int getExportPredefID() {

        return this.exportPredefID;
    }

    /**
     * Setter for property exportPredefID.
     *
     * @param exportPredefID New value of property exportPredefID.
     */
    public void setExportPredefID(int exportPredefID) {

        this.exportPredefID = exportPredefID;
    }

	public boolean isRecipientFilterVisible() {
		return recipientFilterVisible;
	}

	public void setRecipientFilterVisible(boolean recipientFilterVisible) {
		this.recipientFilterVisible = recipientFilterVisible;
	}

	public boolean isColumnsPanelVisible() {
		return columnsPanelVisible;
	}

	public void setColumnsPanelVisible(boolean columnsPanelVisible) {
		this.columnsPanelVisible = columnsPanelVisible;
	}

	public boolean isMlistsPanelVisible() {
		return mlistsPanelVisible;
	}

	public void setMlistsPanelVisible(boolean mlistsPanelVisible) {
		this.mlistsPanelVisible = mlistsPanelVisible;
	}

    public boolean isDatesPanelVisible() {
        return datesPanelVisible;
    }

    public void setDatesPanelVisible(boolean datesPanelVisible) {
        this.datesPanelVisible = datesPanelVisible;
    }

    public boolean isFileFormatPanelVisible() {
		return fileFormatPanelVisible;
	}

	public void setFileFormatPanelVisible(boolean fileFormatPanelVisible) {
		this.fileFormatPanelVisible = fileFormatPanelVisible;
	}

    public List<ExportPredef> getExportPredefList() {
        return exportPredefList;
    }

    public void setExportPredefList(List<ExportPredef> exportPredefList) {
        this.exportPredefList = exportPredefList;
    }

    public int getExportPredefCount() {
        return exportPredefCount;
    }

    public void setExportPredefCount(int exportPredefCount) {
        this.exportPredefCount = exportPredefCount;
    }

    public List<Target> getTargetGroups() {
        return targetGroups;
    }

    public void setTargetGroups(List<Target> targetGroups) {
        this.targetGroups = targetGroups;
    }

    public List<Mailinglist> getMailinglistObjects() {
        return mailinglistObjects;
    }

    public void setMailinglistObjects(List<Mailinglist> mailinglistObjects) {
        this.mailinglistObjects = mailinglistObjects;
    }

    public String getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(String timestampStart) {
        this.timestampStart = timestampStart;
    }

    public String getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(String timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public String getCreationDateStart() {
        return creationDateStart;
    }

    public void setCreationDateStart(String creationDateStart) {
        this.creationDateStart = creationDateStart;
    }

    public String getCreationDateEnd() {
        return creationDateEnd;
    }

    public void setCreationDateEnd(String creationDateEnd) {
        this.creationDateEnd = creationDateEnd;
    }

    public String getMailinglistBindStart() {
        return mailinglistBindStart;
    }

    public void setMailinglistBindStart(String mailinglistBindStart) {
        this.mailinglistBindStart = mailinglistBindStart;
    }

    public String getMailinglistBindEnd() {
        return mailinglistBindEnd;
    }

    public void setMailinglistBindEnd(String mailinglistBindEnd) {
        this.mailinglistBindEnd = mailinglistBindEnd;
    }
}
