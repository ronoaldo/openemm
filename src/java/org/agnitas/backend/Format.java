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

import  java.util.Date;
import  java.util.Locale;
import  java.util.Hashtable;
import  java.util.TimeZone;
import  java.text.NumberFormat;
import  java.text.DecimalFormat;
import  java.text.SimpleDateFormat;

public class Format {
    private String              format;
    private String              lang;
    private String              country;
    private Hashtable <String, String>  opts;
    private Locale              locale;
    private NumberFormat            numberFormater;
    private SimpleDateFormat        dateFormater;
    protected String            error;

    public Format (String nFormat, String nLang, String nCountry, Hashtable <String, String> nOpts) {
        format = nFormat;
        lang = nLang;
        country = nCountry;
        opts = nOpts;
        if (format != null) {
            if ((country == null) || (lang == null)) {
                if (lang == null) {
                    locale = null;
                } else {
                    locale = new Locale (lang);
                }
            } else {
                locale = new Locale (lang, country);
            }
        } else {
            locale = null;
        }
        numberFormater = null;
        dateFormater = null;
        error = null;
    }

    private boolean validNumberFormat () {
        if (numberFormater == null) {
            if (locale != null) {
                numberFormater = NumberFormat.getInstance (locale);
            } else {
                numberFormater = NumberFormat.getInstance ();
            }
            if (numberFormater instanceof DecimalFormat) {
                try {
                    ((DecimalFormat) numberFormater).applyPattern (format);
                } catch (IllegalArgumentException e) {
                    error = "Invalid format (" + e.toString () + ") found: " + format;
                    numberFormater = null;
                }
            } else {
                error = "Format leads into an unexpected class \"" + numberFormater.getClass ().getName () + "\": " + format;
                numberFormater = null;
            }
            if (numberFormater == null) {
                format = null;
            }
        }
        return numberFormater != null;
    }
    public String formatFloat (double val) {
        String  rc = null;

        if ((format != null) && validNumberFormat ()) {
            rc = numberFormater.format (val);
        }
        return rc;
    }

    public String formatInteger (long val) {
        String  rc = null;

        if ((format != null) && validNumberFormat ()) {
            rc = numberFormater.format (val);
        }
        return rc;
    }

    public String formatString (String val) {
        return val;
    }

    private boolean validDateFormater () {
        if (dateFormater == null) {
            String  tz = opts != null ? opts.get ("timezone") : null;

            try {
                if (locale != null) {
                    dateFormater = new SimpleDateFormat (format, locale);
                } else {
                    dateFormater = new SimpleDateFormat (format);
                }
            } catch (IllegalArgumentException e) {
                error = "Invalid format (" + e.toString () + ") found: " + format;
                dateFormater = null;
            }
            if ((dateFormater != null) && (tz != null)) {
                dateFormater.setTimeZone (TimeZone.getTimeZone (tz));
            }
            if (dateFormater == null) {
                format = null;
            }
        }
        return dateFormater != null;
    }

    public String formatDate (Date val) {
        String  rc = null;

        if ((format != null) && validDateFormater ()) {
            rc = dateFormater.format (val);
        }
        return rc;
    }
}
