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

import  java.util.HashSet;

public class Target {
    public long id;
    public String   sql;
    
    public Target (long nId, String nSql) {
        id = nId;
        sql = nSql;
    }
    
    public boolean valid () {
        return sql != null && sql.length () > 0;
    }
    
    public void requestFields (Object datap, HashSet <String> use) {
        if (sql != null) {
            String  c = sql.toLowerCase ();
            int l = c.length ();
            int pos = 0;
            int start;

            while ((pos = c.indexOf ("cust.", pos)) != -1) {
                pos += 5;
                start = pos;
                while (pos < l) {
                    char    chk = c.charAt (pos);

                    if ((! Character.isLetterOrDigit (chk)) && (chk != '_')) {
                        break;
                    }
                    ++pos;
                }
                if (start < pos) {
                    String  cname = c.substring (start, pos);

                    use.add (cname);
                }
            }
        }
    }
}
