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
package org.agnitas.preview;

import  java.util.Hashtable;
import  java.util.HashSet;
import  java.util.Vector;

import  org.agnitas.backend.Data;
import  org.agnitas.backend.EMMTag;
import  org.agnitas.backend.BlockData;

public class TAGCheckImpl implements TAGCheck {
    class Seen {
        boolean status;
        String  report;

        public Seen () {
            status = false;
            report = null;
        }
    }

    private Data    data;
    private Hashtable <String, Seen>
            seen;

    protected Object mkData (String statusID) throws Exception {
        return new Data ("tagcheck", statusID, "silent");
    }

    protected Object mkEMMTag (String tag) throws Exception {
        return new EMMTag (data, tag, false);
    }
    
    protected Object mkBlockData (String content) {
        return new BlockData (content, null, null, -1, -1, 0, null, true, true);
    }
    
    public TAGCheckImpl (long mailingID) throws Exception {
        data = (Data) mkData ("preview:" + mailingID);
        seen = new Hashtable <String, Seen> ();
    }

    public TAGCheckImpl () throws Exception {
        this (0);
    }

    public void done () {
        if (data != null) {
            try {
                data.done ();
            } catch (Exception e) {
                ;
            }
            data = null;
        }
    }

    public boolean check (String tag, StringBuffer report) {
        Seen    s = seen.get (tag);

        if (s == null) {
            s = new Seen ();
            try {
                EMMTag  t = (EMMTag) mkEMMTag (tag);
                HashSet <String>
                    dummy = new HashSet <String> ();
                
                t.initialize (data, true);
                t.requestFields (data, dummy);
                s.status = true;
            } catch (Exception e) {
                s.report = tag + ": " + e.toString ();
            }
        }
        if ((! s.status) && (s.report != null) && (report != null)) {
            report.append (s.report + "#");
        }
        return s.status;
    }

    public boolean check (String tag) {
        return check (tag, null);
    }
    
    public boolean checkContent (String content, StringBuffer report, Vector <String> failures) {
        BlockData   bd = (BlockData) mkBlockData (content);
        boolean     rc = true;
        String      tag;
        
        do {
            try {
                tag = bd.get_next_tag ();
            } catch (Exception e) {
                tag = null;
                rc = false;
                if (report != null) {
                    report.append ("Failed to parse: " + e.toString () + "   #");
                }
            }
            if ((tag != null) && (! check (tag, report))) {
                rc = false;
                if (failures != null) {
                    failures.add (tag);
                }
            }
        }   while (tag != null);
        return rc;
    }
}
