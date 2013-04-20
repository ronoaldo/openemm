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

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

import org.agnitas.util.Log;

/** this class is used to remove pending mailings
 */
public class Destroyer {
    /** Class to filter filenames for deletion
     */
    private class DestroyFilter implements FilenameFilter {
        /** the mailing ID */
        private long    mailingID;

        /** Constructor
         * @param mailing_id the mailing ID to filter files for
         */
        public DestroyFilter (long mailing_id) {
            super ();
            mailingID = mailing_id;
        }

        /** If a file matches the filter
         * @param dir home directory of the file
         * @param name name of the file
         * @return true, if it should be deleted
         */
        public boolean accept (File dir, String name) {
            boolean     st;
            StringTokenizer tok;

            st = false;
            tok = new StringTokenizer (name, "=");
            if (tok.countTokens () == 6) {
                int n;
                long    mid;

                for (n = 0; n < 3; ++n) {
                    tok.nextToken ();
                }
                mid = Long.decode (tok.nextToken ()).longValue ();
                if (mid == mailingID) {
                    st = true;
                }
            }
            return st;
        }
    }

    /** The mailing ID */
    private long    mailingID;
    /** Reference to configuration */
    private Data    data;

    public void setData (Object nData) {
        data = (Data) nData;
    }

    public void mkData () throws Exception {
        setData (new Data ("destroyer"));
    }

    /** Constructor
     * @param mailing_id the mailing ID for the mailing to destroy
     */
    public Destroyer (long mailing_id) throws Exception {
        if (mailing_id <= 0) {
            throw new Exception ("Mailing_id is less or equal 0");
        }
        mailingID = mailing_id;
        mkData ();
    }

    /** Cleanup
     */
    public void done () throws Exception {
        data.done ();
    }

    /** Remove file(s) found in directory
     * @param path the directory to search for
     * @return number of files deleted
     */
    private int doDestroy (String path) throws Exception {
        File    file;
        File    files[];
        int n;

        file = new File (path);
        files = file.listFiles (new DestroyFilter (mailingID));
        for (n = 0; n < files.length; ++n) {
            if (! files[n].delete ()) {
                data.logging (Log.ERROR, "destroy", "File " + files[n] + " cannot be removed");
            }
        }
        return files.length;
    }

    /** Start destruction
     * @return message string
     */
    public String destroy () throws Exception {
        String  msg;
        String  path;

        msg = "Destroy:";
        path = data.metaDir ();
        msg += " [" + path;
        try {
            msg += " " + doDestroy (path);
            msg += " done";
        } catch (Exception e) {
            msg += " failed: " + e;
        }
        msg += "]";
        return msg;
    }

}
