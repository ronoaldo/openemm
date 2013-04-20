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

import java.util.List;

import org.agnitas.beans.AdminEntry;
import org.agnitas.util.UserActivityLogActions;

/**
 * @author Viktor Gema
 */
public class UserActivityLogForm extends StrutsFormBase {

    private static final long serialVersionUID = -543714511721911412L;
    private int action;
    private int userActivityLogAction;
    private String username;
    private int all;
    private String fromDate;
    private String toDate;

    // added for retrieving list of all users
    private List<AdminEntry> adminList;
     // added for retrieving list of company's users
    private List<AdminEntry> adminByCompanyList;

    public List<AdminEntry> getAdminByCompanyList() {
        return adminByCompanyList;
    }

    public void setAdminByCompanyList(List<AdminEntry> adminByCompanyList) {
        this.adminByCompanyList = adminByCompanyList;
    }

    public List<AdminEntry> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<AdminEntry> adminList) {
        this.adminList = adminList;
    }


    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getUserActivityLogAction() {
        return userActivityLogAction;
    }

    public void setUserActivityLogAction(int userActivityLogAction) {
        this.userActivityLogAction = userActivityLogAction;
    }

    public UserActivityLogActions[] getActions() {
        return UserActivityLogActions.values();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAll(int fullListSize) {
    this.all=fullListSize;
    }

    public int getAll() {
        return all;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
