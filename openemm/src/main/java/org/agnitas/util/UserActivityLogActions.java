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

package org.agnitas.util;

/**
 * @author Viktor Gema
 */
public enum UserActivityLogActions {

    ANY("UserActivitylog.Any_Action", "any"), //0

    CREATE("UserActivitylog.Action.Create", "create"), // 1

    EDIT("UserActivitylog.Action.Edit", "edit"), // 2

    DELETE("UserActivitylog.Action.Delete", "delete"), // 3

    DO("UserActivitylog.Action.Do", "do"), // 4

    SEND("UserActivitylog.Action.Send", "send"), // 5

    BLACKLIST("UserActivitylog.Action.Blacklist", "blacklist"), // 6

    LOGIN_LOGOUT("UserActivitylog.login.logout", "loginLogout"), // 7

    ANY_WITHOUT_LOGIN("UserActivitylog.Any_Action_without_login", "all"); // 8

    // message key in resource bundle to display value on pages
    private String publicValue;
    // action type for log file
    private String localValue;


    public String getPublicValue() {
        return publicValue;
    }

    // Position of constant in Enum
    public int getIntValue() {
        return ordinal();
    }
    // first parameter of constant is publicValue, second is localValue
    UserActivityLogActions(String publicValue, String localValue) {
        this.publicValue = publicValue;
        this.localValue=localValue;
    }

    public static String getPublicValue(int intValue) {
        if (intValue < UserActivityLogActions.values().length) {
            return UserActivityLogActions.values()[intValue].getPublicValue();
        } else {
            return null;
        }
    }

    public static String getLocalValue(int intValue) {
        if (intValue < UserActivityLogActions.values().length) {
            return UserActivityLogActions.values()[intValue].getLocalValue();
        } else {
            return null;
        }
    }

    public String getLocalValue() {
        return localValue;
    }
}
