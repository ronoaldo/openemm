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

import  java.io.StringWriter;
import  java.util.Hashtable;
import  java.util.HashSet;
import  java.util.regex.Pattern;
import  java.util.regex.Matcher;
import  org.apache.velocity.app.VelocityEngine;
import  org.apache.velocity.VelocityContext;
//
// One should not access runtime directly, I know :-(
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.RuntimeServices;

public class CustomTitle implements LogChute {
    /*
     * Colect velocity error output
     */
    private StringBuffer    logTo = null;
    public void init (RuntimeServices rs) throws Exception {
    }
    public void log (int logLevel, String logMessage) {
        if (logTo != null) {
            String  label;
            
            if (logLevel == LogChute.WARN_ID) {
                label = "WARNING";
            } else if (logLevel == LogChute.ERROR_ID) {
                label = "ERROR";
            } else {
                label = null;
            }
            if (label != null) {
                logTo.append ("[" + label + "]: " + logMessage + "\n");
            }
        }
    }
    public void log (int logLevel, String logMessage, Throwable t) {
        log (logLevel, logMessage);
    }
    public boolean isLevelEnabled (int level) {
        return (level == LogChute.WARN_ID) || (level == LogChute.ERROR_ID);
    }

    public static String    SCRIPT_ID = "#<>#";
    private VelocityEngine  ve;
    private VelocityContext vc;
    private String      titleText;

    public CustomTitle (String nTitleText) {
        try {
            ve = new VelocityEngine ();
            ve.setProperty (VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
            ve.setProperty (VelocityEngine.VM_LIBRARY, "");
            ve.init ();
            vc = new VelocityContext ();
        } catch (Exception e) {
            ve = null;
            vc = null;
        }
        titleText = cleanup (nTitleText.startsWith (SCRIPT_ID) ? nTitleText.substring (SCRIPT_ID.length ()) : nTitleText);
    }

    private static Pattern  cleaner = Pattern.compile ("#(include|parse)(\\([^)]*\\))?");
    private String cleanup (String template) {
        Matcher m = cleaner.matcher (template);

        return m.replaceAll ("");
    }

    private static Pattern  shrinker = Pattern.compile ("[ \t\n\r]+");
    private String shrink (String output) {
        Matcher m = shrinker.matcher (output);

        return m.replaceAll (" ").trim ();
    }

    private static Pattern  searcher = Pattern.compile ("cust\\.([a-z_][a-z0-9_]*)");
    public void requestFields (HashSet <String> predef) {
        Matcher m = searcher.matcher (titleText);
        int pos = 0;

        while (m.find (pos)) {
            predef.add (titleText.substring (m.start (1), m.end (1)));
            pos = m.end ();
        }
    }

    public String makeTitle (int titleType, int gender, String title, String firstname, String lastname, Hashtable <String, String> columns, StringBuffer error) {
        String      rc = null;

        if ((ve != null) && (vc != null)) {
            logTo = error;
            try {
                StringWriter    out = new StringWriter ();

                vc.put ("type", new Integer (titleType));
                vc.put ("gender", new Integer (gender));
                vc.put ("title", (title == null || titleType == Title.TITLE_FIRST ? "" : title));
                vc.put ("firstname", (firstname == null || titleType == Title.TITLE_DEFAULT ? "" : firstname));
                vc.put ("lastname", (lastname == null || titleType == Title.TITLE_FIRST ? "" : lastname));
                vc.put ("cust", columns);
                ve.evaluate (vc, out, "title", titleText);
                rc = shrink (out.toString ());
            } catch (Exception e) {
                if (error != null) {
                    error.append (e.toString ());
                    error.append ("\n");
                }
            }
            logTo = null;
        }
        return rc;
    }
}
