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

import org.agnitas.beans.Company;

public class CompanyImpl implements Company {

    private static final long serialVersionUID = -3486669974391290220L;
	protected int companyID;
    protected int creatorID;
    protected String shortname;
    protected String description;
    protected String status;
    protected int mailtracking;
    protected int maxLoginFails;
    protected int loginBlockTime;
    protected Number minimumSupportedUIDVersion;
    protected int maxRecipients;


	// CONSTRUCTOR:
    public CompanyImpl() {
        companyID=0;
        creatorID=0;
    }



    // * * * * *
    //  SETTER:
    // * * * * *
    public void setId(int id) {
        companyID=id;
    }

    public void setShortname(String name) {
        shortname=name;
    }

    public void setDescription(String sql) {
        description=sql;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMaxRecipients(int maxRecipients) {
        this.maxRecipients = maxRecipients;
    }

    // * * * * *
    //  GETTER:
    // * * * * *
    public int getCreatorID() {
        return this.creatorID;
    }

    public String getStatus() {
        return this.status;
    }

    public int getId() {
        return companyID;
    }

    public String getShortname() {
        return shortname;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Holds value of property rdirDomain.
     */
    protected String rdirDomain="http://rdir.de";

    /**
     * Getter for property rdirDomain.
     * @return Value of property rdirDomain.
     */
    public String getRdirDomain() {

        return this.rdirDomain;
    }

    /**
     * Setter for property rdirDomain.
     * @param rdirDomain New value of property rdirDomain.
     */
    public void setRdirDomain(String rdirDomain) {

        this.rdirDomain = rdirDomain;
    }

    /**
     * Holds value of property secret.
     */
    protected String secret;

    /**
     * Getter for property secret.
     * @return Value of property secret.
     */
    public String getSecret() {
    	if(this.secret != null)
    		return this.secret;
    	else
    		return "";
    }

    /**
     * Setter for property secret.
     * @param secret New value of property secret.
     */
    public void setSecret(String secret) {

        this.secret = secret;
    }

    /**
     * Holds value of property mailloopDomain.
     */
    private String mailloopDomain="filter.agnitas.de";

    /**
     * Getter for property mailloopDomain.
     * @return Value of property mailloopDomain.
     */
    public String getMailloopDomain() {
        return this.mailloopDomain;
    }

    /**
     * Setter for property mailloopDomain.
     * @param mailloopDomain New value of property mailloopDomain.
     */
    public void setMailloopDomain(String mailloopDomain) {
        this.mailloopDomain = mailloopDomain;
    }

    public int getMailtracking() {
    	return this.mailtracking;
    }

    public void setMailtracking (int tracking) {
    	this.mailtracking = tracking;
    }

    private int useUTF=0;

    public void setUseUTF(int useUTF) {
        this.useUTF = useUTF;
    }

    public int getUseUTF() {
        return this.useUTF;
    }

    public int getMaxLoginFails() {
		return maxLoginFails;
	}

	public void setMaxLoginFails(int maxLoginFails) {
		this.maxLoginFails = maxLoginFails;
	}

	public int getLoginBlockTime() {
		return loginBlockTime;
	}

	public void setLoginBlockTime(int loginBlockTime) {
		this.loginBlockTime = loginBlockTime;
	}

    public int getMaxRecipients() {
        return maxRecipients;
    }

    @Override
	public Number getMinimumSupportedUIDVersion() {
		return this.minimumSupportedUIDVersion;
	}
	
	@Override
	public void setMinimumSupportedUIDVersion( Number minimumSupportedUIDVersion) {
		this.minimumSupportedUIDVersion = minimumSupportedUIDVersion;
	}
}
