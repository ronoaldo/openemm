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

import  javax.mail.internet.InternetAddress;
import  javax.mail.internet.AddressException;

/** This class handles emails and there different
 * representations
 */
public class EMail {
    /** The full email with optional comment */
    public String   full;
    /** The email alone */
    public String   pure;
    /** The plain domain part of email */
    public String   domain;
    /** The full email, coded using punycode */
    public String   full_puny;
    /** The email alone coded using punycode */
    public String   pure_puny;
    /** The domain part coded using punycode */
    public String   domain_puny;

    /** Extracts the email address by stripping any comment
     * @param str the string to extract the email address from
     * @return the pure email
     */
    private String extractPureAddress (String str) {
        int n, m;

        if (((n = str.indexOf ('(')) != -1) &&
            ((m = str.indexOf (')', n + 1)) != -1))
            str = (n > 0 ? str.substring (0, n) : "") + (m + 1 < str.length () ? str.substring (m + 1) : "");
        if (((n = str.indexOf ('<')) != -1) &&
            ((m = str.indexOf ('>', n + 1)) != -1))
            str = str.substring (n + 1, m);
        n = str.length ();
        for (m = 0; (m < n) && (str.charAt (m) == ' '); ++m)
            ;
        if (m > 0) {
            str = str.substring (m);
            n -= m;
        }
        for (m = n - 1; (m >= 0) && (str.charAt (m) == ' '); --m)
            ;
        if (m < n - 1)
            str = str.substring (0, m + 1);
        return str;
    }

    /** Create the punycode version of the available email address
     */
    private void makePunyCoded () {
        pure = null;
        full_puny = null;
        pure_puny = null;
        if (full != null) {
            pure = extractPureAddress (full);
            pure_puny = StringOps.punycoded (pure);

            int     cur = 0;
            int     len = full.length ();
            int     plen = pure.length ();
            StringBuffer    temp = new StringBuffer (len + 32);

            if (plen > 0) {
                while (cur < len) {
                    int n;

                    if ((n = full.indexOf (pure, cur)) != -1) {
                        if (n > cur)
                            temp.append (full.substring (cur, n));
                        temp.append (pure_puny);
                        cur = n + plen;
                    } else {
                        temp.append (full.substring (cur, len));
                        cur = len;
                    }
                }
                full_puny = temp.toString ();
            } else
                full_puny = full;
        }
    }

    /** Extract domain part of emails
     */
    private void getDomains () {
        domain = null;
        domain_puny = null;

        int n;

        if ((pure != null) && ((n = pure.indexOf ('@')) != -1)) {
            domain = pure.substring (n + 1).toLowerCase ();
        }
        if ((pure_puny != null) && ((n = pure_puny.indexOf ('@')) != -1)) {
            domain_puny = pure_puny.substring (n + 1);
        }
    }

    /** Constructor
     * @param str the email address with optional comment
     */
    public EMail (String str) {
        setEMail (str);
    }

    /** Set and parse the email address
     */
    public void setEMail (String str) {
        InternetAddress[]   inet;

        try {
            if (str != null) {
                inet = InternetAddress.parse (str);
            } else {
                inet = null;
            }
        } catch (AddressException e) {
            inet = null;
        }
        if ((inet == null) || (inet.length == 0)) {
            full = str;
        } else {
            String  personal = null;
            String  email = null;

            if (inet.length == 1) {
                personal = inet[0].getPersonal ();
                email = inet[0].getAddress ();
            } else {
                for (int n = 0; n < inet.length; ++n) {
                    String  p = inet[n].getPersonal ();
                    String  e = inet[n].getAddress ();

                    if ((e != null) && (e.indexOf ('@') == -1)) {
                        if (p == null) {
                            p = e;
                        } else {
                            p += " " + e;
                        }
                        e = null;
                    }
                    if ((p != null) && (p.length () > 0)) {
                        if (personal == null) {
                            personal = p;
                        } else {
                            personal += ", " + p;
                        }
                    }
                    if ((email == null) && (e != null) && (e.length () > 0)) {
                        email = e;
                    }
                }
            }
            if ((personal != null) && (personal.length () > 0)) {
                if ((! personal.startsWith ("\"")) && (! personal.endsWith ("\""))) {
                    personal = "\"" + personal + "\"";
                }
                full = personal + " <" + email + ">";
            } else
                full = email;
        }
        makePunyCoded ();
        getDomains ();
    }

    /** Checks if the email address could be fully parse
     * @return true, if address is valid
     */
    public boolean valid (boolean relaxed) {
        return (full != null) && (full_puny != null) && (pure != null) && (pure_puny != null) && (relaxed || (pure.indexOf ('@') != -1));
    }

    /** String representation of ourself
     * @return our representation
     */
    public String toString () {
        return   "(" + (full == null ? "" : full) +
            ", " + (full_puny == null ? "" : full_puny) +
            ", " + (pure == null ? "" : pure) +
            ", " + (pure_puny == null ? "" : pure_puny) +
            ")";
    }
}
