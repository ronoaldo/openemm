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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.agnitas.util.Log;

/**
 * Collection of all dynamic content
 */
public class DynCollection {
    /** Reference to configuration */
    protected Data      data;
    /** all dynamic names (including content) */
    protected Hashtable <Long, DynName> names;
    /** number of all available names */
    protected int       ncount;

    /** Constructor
     * @param nData the configuration
     */
    protected DynCollection (Data nData) {
        data = nData;
        names = new Hashtable <Long, DynName> ();
        ncount = 0;
    }

    /** Creates a new dynamic content element
     * @param dyncontID the unique ID
     * @param targetID optional ID for target expression
     * @param order priority of this content
     * @param content the content itself
     * @return a new dynamic content instance
     */
    public DynCont mkDynCont (long dyncontID, long targetID, long order, String content) {
        return new DynCont (dyncontID, targetID, order, content);
    }

    public DynName mkDynName (String name, long nameID) {
        return new DynName (name, nameID);
    }

    protected String queryDynNameColumns () {
        return "dyn_name_id, dyn_name";
    }

    protected void setDynNameColumns (Object dno, Map <String, Object> row) {
    }

    /** Collect all available dynamic parts from the database
     */
    protected void collectParts () throws Exception {
        List <Map <String, Object>> rq;
        String              query;
            
        query = "SELECT " + queryDynNameColumns () + " FROM dyn_name_tbl " +
            "WHERE mailing_id = :mailingID AND company_id = :companyID";
        rq = data.dbase.query (query, "mailingID", data.mailing_id, "companyID", data.company_id);
        for (int n = 0; n < rq.size (); ++n) {
            Map <String, Object>    row = rq.get (n);
            long            nameID = data.dbase.asLong (row.get ("dyn_name_id"));
            String          name = data.dbase.asString (row.get ("dyn_name"));

            if (! names.containsKey (new Long (nameID))) {
            	DynName  dno = mkDynName (name, nameID);

                setDynNameColumns (dno, row);
                names.put (new Long (nameID), dno);
                ncount++;
                data.logging (Log.DEBUG, "dyn", "Added dynamic name " + name);
            } else
                data.logging (Log.DEBUG, "dyn", "Skip already recorded name " + name);
        }
            
        query = "SELECT dyn_content_id, dyn_name_id, target_id, dyn_order, dyn_content FROM dyn_content_tbl " +
            "WHERE dyn_name_id IN (SELECT dyn_name_id FROM dyn_name_tbl WHERE mailing_id = :mailingID AND company_id = :companyID)";
        rq = data.dbase.query (query, "mailingID", data.mailing_id, "companyID", data.company_id);
        for (int n = 0; n < rq.size (); ++n) {
            Map <String, Object>    row = rq.get (n);
            long            dyncontID = data.dbase.asLong (row.get ("dyn_content_id"));
            long            nameID = data.dbase.asLong (row.get ("dyn_name_id"));
            long            targetID = data.dbase.asLong (row.get ("target_id"));
            long            order = data.dbase.asLong (row.get ("dyn_order"));
            String          content = data.dbase.asClob (row.get ("dyn_content"));
            DynName         name;

            if ((name = names.get (new Long (nameID))) != null) {
                name.add (mkDynCont (dyncontID, targetID, order, content != null ? StringOps.convertOld2New (content) : null), data);
            } else {
                data.logging (Log.WARNING, "dyn", "Found content for name-ID " + nameID + " without an entry in dyn_name_tbl"); // Use "nameID", "name" is always null here
            }
        }

        for (Object o : names.values ()) {
            DynName tmp = (DynName) o;

            for (int n = 0; n < tmp.clen; ++n) {
                DynCont cont = tmp.content.elementAt (n);

                if ((cont.targetID != DynCont.MATCH_ALWAYS) &&
                    (cont.targetID != DynCont.MATCH_NEVER)) {
                    Target  tgt;

                    try {
                        tgt = data.getTarget (cont.targetID);
                    } catch (Exception ex) {
                        data.logging (Log.ERROR, "dyn", cont.id + " has invalid targetID " + cont.targetID + ": " + ex.toString ());
                        tgt = null;
                    }
                    if (tgt != null) {
                        cont.condition = tgt.sql;
                        data.logging (Log.DEBUG, "dyn", cont.id + " has condition '" + cont.condition + "'");
                    } else {
                        data.logging (Log.ERROR, "dyn", cont.id + " has invalid condition ID, disable block");
                        cont.targetID = DynCont.MATCH_NEVER;
                    }
                }
            }
        }
    }
}
