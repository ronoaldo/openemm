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

package org.agnitas.util.importvalues;


/**
 * Values for checkForDuplicates property of import profile
 *
 * @author Vyacheslav Stepanov
 */
public enum CheckForDuplicates {

    NEW_DATA("import.doublechecking.csv"), // 0

    COMPLETE("import.doublechecking.full"), // 1

    NO_CHECK("import.doublechecking.none"); // 2

    // message key in resource bundle to display value on pages
    private String publicValue;

    public String getPublicValue() {
        return publicValue;
    }

    public int getIntValue() {
        return ordinal();
    }

    CheckForDuplicates(String publicValue) {
        this.publicValue = publicValue;
    }

    public static String getPublicValue(int intValue) {
        if (intValue < CheckForDuplicates.values().length) {
            return CheckForDuplicates.values()[intValue].getPublicValue();
        } else {
            return null;
        }
    }
}