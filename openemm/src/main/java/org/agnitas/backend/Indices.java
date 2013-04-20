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
package org.agnitas.backend;

/** Collection of indices in database records
 */
public class Indices {
    /** index for emai */
    public int  email = -1;
    /** index for gender */
    public int  gender = -1;
    /** index for firstname */
    public int  firstname = -1;
    /** index for lastname */
    public int  lastname = -1;
    /** index for title */
    public int title = -1;

    /** Checks if column index should be remebered
     * @param colname name of column
     * @param index in database record
     */
    public void checkIndex (String colname, int index) {
        if ((email == -1) && colname.equals ("email")) {
            email = index;
        } else if ((gender == -1) && colname.equals ("gender")) {
            gender = index;
        } else if ((firstname == -1) && colname.equals ("firstname")) {
            firstname = index;
        } else if ((lastname == -1) && colname.equals ("lastname")) {
            lastname = index;
        } else if ((title == -1) && colname.equals ("title")) {
            title = index;
        }
    }
}
