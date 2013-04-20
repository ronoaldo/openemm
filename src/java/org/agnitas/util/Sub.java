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
package org.agnitas.util;

import  java.util.Hashtable;
import  java.util.regex.Pattern;
import  java.util.regex.Matcher;

public class Sub {
    public interface CB {
        public void cb_sub_setup (String id, Hashtable <String, String> param);
        public void cb_sub_done (String id, Hashtable <String, String>  param);
        public String   cb_sub_exec (String id, Hashtable <String, String> param, Object privData);
    }

    class Element implements CB {
        Element     next;
        String      pure;
        String      cbID;
        String      cbFull;
        Hashtable <String, String>
                    cbParm;
        CB          cb;
        
        public void cb_sub_setup (String id, Hashtable <String, String> param) {
        }
        public void cb_sub_done (String id, Hashtable <String, String> param) {
        }
        public String cb_sub_exec (String id, Hashtable <String, String> unused, Object privData) {
            return pure;
        }

        protected Element () {
            next = null;
            pure = null;
            cbID = null;
            cbFull = null;
            cbParm = null;
            cb = null;
        }
        
        protected Element (String s) {
            this ();
            pure = s;
            cb = this;
        }
        
        protected Element (String nCbID, String nCbFull) {
            this ();
            cbID = nCbID;
            cbFull = nCbFull;
            cbParm = new Hashtable <String, String> ();
        }
        
        protected void next (Element nNext) {
            next = nNext;
        }
        
        protected Element next () {
            return next;
        }
        
        protected void add (String vname, String value) {
            cbParm.put (vname, value);
        }
        
        protected void setCB (CB nCb) {
            cb = nCb;
        }
        
        protected boolean isCB (String cCbID) {
            if (cbID != null)
                return cbID.equals (cCbID);
            return (cCbID == null) && (cb == null);
        }
        
        protected void setup () {
            if (cb != null)
                cb.cb_sub_setup (cbID, cbParm);
        }
        
        protected void done () {
            if (cb != null)
                cb.cb_sub_done (cbID, cbParm);
        }
        
        protected String get (Object passThrough) {
            if (cb != null)
                return cb.cb_sub_exec (cbID, cbParm, passThrough);
            return pure != null ? pure : (cbFull != null ? cbFull : "");
        }
    }
    
    Element     elem, prev;
    
    public Sub () {
        elem = null;
        prev = null;
    }
    
    private void addElement (Element e) {
        if (prev != null)
            prev.next (e);
        else
            elem = e;
        prev = e;
    }
    
    /** Parses a string and search for variables as definied in
     * pattern as a regular expression
     * @param src the source string to parse
     * @param pattern the regular expression for scanning the src
     * @param idPattern regexp to extract the ID for this variable
     * @param parmPattern regexp to extract optional parameter
     * @param quotePattern regexp to remove quotes from paramter values
     * @param flags regex compile flags
     * @return True if pattern is found, False otherwise
     */
    public boolean parse (String src, String pattern, String idPattern, String parmPattern, String quotePattern, int flags) {
        boolean rc = false;
        Pattern r = Pattern.compile (pattern, flags);
        Matcher m = r.matcher (src);
        Pattern rid = Pattern.compile (idPattern);
        Matcher mid = null;
        Pattern rparm = Pattern.compile (parmPattern);
        Matcher mparm = null;
        Pattern rquot = (quotePattern != null ? Pattern.compile (quotePattern) : null);
        Matcher mquot = null;
        int gnumber;
        int slen, pos;
        
        elem = null;
        prev = null;
        gnumber = m.groupCount () > 0 ? 1 : 0;
        slen = src.length ();
        for (pos = 0; pos < slen; ) {
            if (m.find (pos)) {
                int start = m.start ();
                
                if (start > pos)
                    addElement (new Element (src.substring (pos, start)));
                pos = m.end ();
                
                String  cbData = m.group (gnumber);
                String  cbID;
                if (mid == null)
                    mid = rid.matcher (cbData);
                else
                    mid.reset (cbData);
                if (mid.find ()) {
                    cbID = mid.group (mid.groupCount () > 0 ? 1 : 0);
                    start = mid.end ();
                } else {
                    cbID = cbData;
                    start = 0;
                }

                String  cbParm;

                if (start > 0)
                    cbParm = cbData.substring (start);
                else
                    cbParm = cbData;

                Element tmp = new Element (cbID, cbData);
                
                if (mparm == null)
                    mparm = rparm.matcher (cbParm);
                else
                    mparm.reset (cbParm);
                if (mparm.groupCount () >= 2) {
                    int cbpos = 0;
                    int cblen = cbParm.length ();

                    while (cbpos < cblen) 
                        if (mparm.find (cbpos)) {
                            String  value = mparm.group (2);
                            
                            if (rquot != null) {
                                if (mquot == null)
                                    mquot = rquot.matcher (value);
                                else
                                    mquot.reset (value);
                                if (mquot.find ())
                                    value = mquot.group (mquot.groupCount () > 0 ? 1 : 0);
                            }
                            tmp.add (mparm.group (1), value);
                            cbpos = mparm.end ();
                        } else
                            break;
                }
                addElement (tmp);
                rc = true;
            } else {
                addElement (new Element (src.substring (pos)));
                pos = slen;
            }
        }
        return rc;
    }
    public boolean parse (String src, String pattern, String idPattern, String parmPattern, String quotePattern) {
        return parse (src, pattern, idPattern, parmPattern, quotePattern, 0);
    }
    
    /** Register a callback for each found element
     * @param id the callback id
     * @param cb the class to be called
     */
    public void reg (String id, CB cb) {
        for (Element tmp = elem; tmp != null; tmp = tmp.next ())
            if (tmp.isCB (id))
                tmp.setCB (cb);
    }
    
    /** Deinitialize all callbacks
     */
    public void done () {
        for (Element tmp = elem; tmp != null; tmp = tmp.next ())
            tmp.done ();
    }

    /** Main routine to substitute an expression
     * @param passThrough some object to pass through to the callback
     * @return the newly generated string
     */
    public String sub (Object passThrough) {
        StringBuffer    buffer = new StringBuffer (512);
        
        for (Element tmp = elem; tmp != null; tmp = tmp.next ()) {
            String  stmp = tmp.get (passThrough);
            
            if (stmp != null)
                buffer.append (stmp);
        }
        return buffer.toString ();
    }
}
