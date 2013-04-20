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

package org.agnitas.beans.impl;

import org.agnitas.beans.ExportPredef;

import java.util.Date;

public class ExportPredefImpl implements ExportPredef {
    protected int id;

    protected int company;

    protected String charset="ISO-8859-1";
    
    protected String columns="";

    protected String shortname="";

    protected String description="";

    protected String mailinglists="";

    protected int mailinglistID;

    protected String delimiter="";

    protected String separator=";";

    protected int targetID;

    protected String userType="E";

    protected int userStatus;

    protected int deleted;

    private Date timestampStart;
    private Date timestampEnd;

    private Date creationDateStart;
    private Date creationDateEnd;

    private Date mailinglistBindStart;
    private Date mailinglistBindEnd;

    ExportPredefImpl() {
    }

    // * * * * *
    //  SETTER:
    // * * * * *
    public void setId(int id) {
        this.id=id;
    }

    public void setCompanyID(int company) {
        this.company=company;
    }

    public void setCharset(String charset) {
        this.charset=charset;
    }
    
    public void setColumns(String columns) {
        this.columns=columns;
    }
    
    public void setShortname(String shortname) {
        this.shortname=shortname;
    }
    
    public void setDescription(String description) {
        this.description=description;
    }
    
    public void setMailinglists(String mailinglists) {
        this.mailinglists=mailinglists;
    }
    
    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID=mailinglistID;
    }

    public void setDelimiterValue(int delimiter) {
        switch(delimiter) {
            case 1: this.delimiter="'"; break;
            default: this.delimiter="\"";
        }
    }

    public void setDelimiter(String delimiter) {
        this.delimiter=delimiter;
    }

    public void setSeparatorValue(int separator) {
        switch(separator) {
            case 1: this.separator=","; break;
            case 2: this.separator="|"; break;
            case 3: this.separator="\t"; break;
            default: this.separator=";";
        }
    }

    public void setSeparator(String separator) {
        this.separator=separator;
    }

    public void setTargetID(int targetID) {
        this.targetID=targetID;
    }

    public void setUserType(String userType) {
        this.userType=userType;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus=userStatus;
    }

    public void setDeleted(int deleted) {
        this.deleted=deleted;
    }


    // * * * * *
    //  GETTER:
    // * * * * *
    public int getId() {
        return id;
    }

    public int getCompanyID() {
        return company;
    }
    
    public String getCharset() {
        return charset;
    }

    public String getColumns() {
        return columns;
    }

    public String getShortname() {
        return shortname;
    }

    public String getDescription() {
        return description;
    }

    public String getMailinglists() {
        return mailinglists;
    }

    public int getMailinglistID() {
        return mailinglistID;
    }
    
    public int getDelimiterValue() {
        if(delimiter.equals("'")) {
            return 1;
        }
        return 0;
    }

    public String getDelimiter() {
        return delimiter;
    }
    
    public int getSeparatorValue() {
        if(separator.equals(",")) {
            return 1;
        } else if(separator.equals("|")) {
            return 2;
        } else if(separator.equals("\t")) {
            return 3;
        }
        return 0;
    }

    public String getSeparator() {
        return separator;
    }
    
    public int getTargetID() {
        return targetID;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public int getUserStatus() {
        return userStatus;
    }
    
    public int getDeleted() {
        return deleted;
    }

    public Date getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(Date timestampStart) {
        this.timestampStart = timestampStart;
    }

    public Date getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(Date timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public Date getCreationDateStart() {
        return creationDateStart;
    }

    public void setCreationDateStart(Date creationDateStart) {
        this.creationDateStart = creationDateStart;
    }

    public Date getCreationDateEnd() {
        return creationDateEnd;
    }

    public void setCreationDateEnd(Date creationDateEnd) {
        this.creationDateEnd = creationDateEnd;
    }

    public Date getMailinglistBindStart() {
        return mailinglistBindStart;
    }

    public void setMailinglistBindStart(Date mailinglistBindStart) {
        this.mailinglistBindStart = mailinglistBindStart;
    }

    public Date getMailinglistBindEnd() {
        return mailinglistBindEnd;
    }

    public void setMailinglistBindEnd(Date mailinglistBindEnd) {
        this.mailinglistBindEnd = mailinglistBindEnd;
    }
}
