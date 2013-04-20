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

import  java.util.Vector;
import  org.agnitas.util.Log;

/**
 * Represents a name for a dynamic content
 */
public class DynName {
    /** the unqiue name for this entry */
    protected String    name;
    /** the unique ID */
    protected long      id;
    /** all content with the same name */
    protected Vector <DynCont>
                content;
    /** number of entries in content */
    protected int       clen;

    /**
     * The Constructor
     * @param nName the name
     * @param nId the ID
     */
    protected DynName (String nName, long nId) {
        name = nName;
        id = nId;
        content = new Vector <DynCont> ();
        clen = 0;
    }

    protected String getAssignedColumn () {
        return null;
    }
    /**
     * Add a dynamic block for this name
     * @param cont the content to add
     */
    protected void add (DynCont cont, Data data) {
        int n;
        DynCont tmp;

        tmp = null;
        for (n = 0; n < clen; ++n) {
            tmp = content.elementAt (n);

            if (tmp.order >= cont.order)
                break;
        }
        if ((n < clen) && (tmp != null) && (tmp.order == cont.order)) {
            data.logging (Log.ERROR, "dyn", "DB inconsistence: Textpart \"" + name + "\" has additional content with same order " + cont.order + ", use the one with higher ID (" + tmp.id + " vs. " + cont.id + ")");
            if (cont.id > tmp.id)
                content.set (n, cont);
        } else {
            content.add (n, cont);
            ++clen;
        }
    }
}
