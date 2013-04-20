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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import org.agnitas.util.Log;
import org.agnitas.util.Sub;
import org.agnitas.util.Title;

/** Class EMMTAG
 * - stores information about a single agnitas-tag
 * - constructor rectrieves selectvalues associated with tag-name from dbase
 * - after db query for a record set (user), EmmTag.mTagValue holds the value
 *   for this tag
 */
public class EMMTag implements Sub.CB {
    class PrivateData {
        protected Object    datap;
        protected Object    cinfop;

        protected PrivateData () {
            datap = null;
            cinfop = null;
        }
    }

    /** This tag is taken from the database */
    public final static int    TAG_DBASE = 0;
    /** This tag leads into an coded URL */
    public final static int    TAG_URL = 1;
    /** This tag is handled internally */
    public final static int    TAG_INTERNAL = 2;
    /** This is a forced custom tag */
    public final static int    TAG_CUSTOM = 3;
    /** Internal tag, virtual Database column */
    public final static int    TI_DBV = 0;
    /** Internal tag, database column */
    public final static int    TI_DB = 1;
    /** Internal tag, email address */
    public final static int    TI_EMAIL = 2;
    /** Internal tag, message ID */
    public final static int    TI_MESSAGEID = 3;
    /** Internal tag, UID */
    public final static int    TI_UID = 4;
    /** Internal tag, number of subscriber for this mailing */
    public final static int    TI_SUBSCRIBERCOUNT = 5;
    /** Internal tag, current date */
    public final static int    TI_DATE = 6;
    /** Internal tag, system information created during final mail creation */
    public final static int    TI_SYSINFO = 7;
    /** Internal tag, dynamic condition */
    public final static int    TI_DYN = 8;
    /** Internal tag, dynamic content */
    public final static int    TI_DYNVALUE = 9;
    /** Handle title tags */
    public final static int    TI_TITLE = 10;
    /** Handle full title tags */
    public final static int    TI_TITLEFULL = 11;
    /** Handle title tags for first name only */
    public final static int    TI_TITLEFIRST = 12;
    /** Create image link tags */
    public final static int    TI_IMGLINK = 13;
    /** The base for further internal extensions */
    public final static int    TI_EXT = 14;
    /** Names of all internal tags */
    final static String[]   TAG_INTERNALS = {
        "agnDBV",
        "agnDB",
        "agnEMAIL",
        "agnMESSAGEID",
        "agnUID",
        "agnSUBSCRIBERCOUNT",
        "agnDATE",
        "agnSYSINFO",
        "agnDYN",
        "agnDVALUE",
        "agnTITLE",
        "agnTITLEFULL",
        "agnTITLEFIRST",
        "agnIMGLINK"
    };
    /** Database tag, no special handling */
    final static int    TDB_UNSPEC = 0;
    /** Database tag, image stored in database */
    final static int    TDB_IMAGE = 1;
    /** Names of all database tags */
    final static String[]   TAG_DB = {
        null,
        "agnIMAGE"
    };
    /** The full name of this tag including all parameters */
    public String       mTagFullname;
    /** The name of the tag */
    protected String    mTagName;
    /** All parameters parsed into a hash */
    public Hashtable <String, String>
                mTagParameters;
    /** Number of available parameter */
    private int     mNoOfParameters;
    /** invalid parameter */
    private StringBuffer    invalidParameters;
    /** Is this a complex, e.g. dynamic changable tag */
    private boolean     isComplex;
    /** Howto select this tag from the database */
    public String       mSelectString;
    /** Howto interpret this string */
    public String       mSelectType;
    /** Result of this tag, is set for each customer, if not fixed or global */
    protected String    mTagValue;
    /** The tag type */
    public int       tagType;
    /** The tag type specification */
    public int       tagSpec;
    /** If this tag is fixed, e.g. can be inserted already here */
    public boolean      fixedValue;
    /** If this tag is global, but will be inserted during final mail creation */
    public boolean      globalValue;
    /** If this tag is not retreived from database but build during runtime */
    public boolean      mutableValue;
    private Sub     mutable;
    private PrivateData mutablePD;

    /** Internal used to format agnDB values */
    protected Column    dbColumn;
    protected Format    dbFormat;
    protected String    dbOverwrite;
    /** Internal used value on how to code an email */
    private int     emailCode;
    /** Internal used format, if this is a date tag */
    private SimpleDateFormat
                dateFormat;
    /** Internal used offset in seconds for date tag */
    private long        dateOffset;
    /** Internal used title type */
    private Long        titleType;
    /** Internal used pre-/postfix for title generation */
    private String      titlePrefix, titlePostfix;
    /** Internal used title mode */
    private int     titleMode;
    /** Internal used reference to image component */
    private String      ilPrefix, ilPostfix;
    protected String    ilURL;

    /** Is this character a whitespace?
     * @param ch the character to inspect
     * @return true, if character is whitespace
     */
    private boolean isspace (char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\r') || (ch == '\f'));
    }

    protected String clearify (String tag) {
        return tag;
    }

    /** Split a tag into its elements
     * @return Vector of all elements
     */
    private Vector <String> splitTag () throws Exception {
        int tlen;
        String  tag;

        tlen = mTagFullname.length ();
        if ((tlen > 0) && (mTagFullname.charAt (0) == '[')) {
            tag = mTagFullname.substring (1);
            --tlen;
        } else
            tag = mTagFullname;
        if ((tlen > 0) && (tag.charAt (tlen - 1) == ']'))
            if ((tlen > 1) && (tag.charAt (tlen - 2) == '/'))
                tag = tag.substring (0, tlen - 2);
            else
                tag = tag.substring (0, tlen - 1);
        tag = clearify (tag);
        tlen = tag.length ();

        Vector <String> rc = new Vector <String> ();
        int     rccnt = 0;
        int     state = 0;
        char        quote = '\0';
        StringBuffer    scratch = new StringBuffer (tlen);

        for (int n = 0; n <= tlen; ) {
            char    ch;

            if (n < tlen)
                ch = tag.charAt (n);
            else {
                ch = '\0';
                state = 99;
                ++n;
            }
            switch (state) {
            default:
                throw new Exception ("Invalid state " + state + " for " + mTagFullname);
            case 0:
                if (! isspace (ch)) {
                    scratch.setLength (0);
                    state = 1;
                } else {
                    ++n;
                }
                break;
            case 1:
                if (isspace (ch)) {
                    state = 99;
                } else {
                    scratch.append (ch);
                    if (ch == '=')
                        state = 2;
                }
                ++n;
                break;
            case 2:
                if (isspace (ch)) {
                    state = 99;
                } else if (ch == '\\') {
                    state = 3;
                } else {
                    if ((ch == '"') || (ch == '\'')) {
                        quote = ch;
                        state = 10;
                    } else {
                        scratch.append (ch);
                        state = 20;
                    }
                }
                ++n;
                break;
            case 3:
                if ((ch == '"') || (ch == '\'')) {
                    quote = ch;
                    state = 30;
                } else {
                    scratch.append (ch);
                    state = 20;
                }
                ++n;
                break;
            case 10:
                if (ch == '\\') {
                    state = 11;
                } else {
                    if (ch == quote) {
                        state = 99;
                    } else {
                        scratch.append (ch);
                    }
                }
                ++n;
                break;
            case 11:
                scratch.append (ch);
                state = 10;
                ++n;
                break;
            case 20:
                if (isspace (ch)) {
                    state = 99;
                } else if (ch == '\\') {
                    state = 21;
                } else {
                    scratch.append (ch);
                }
                ++n;
                break;
            case 21:
                scratch.append (ch);
                state = 20;
                ++n;
                break;
            case 30:
                if (ch == '\\') {
                    state = 31;
                } else {
                    scratch.append (ch);
                }
                ++n;
                break;
            case 31:
                if (ch == quote) {
                    state = 99;
                } else {
                    scratch.append (ch);
                }
                ++n;
                break;
            case 99:
                if (scratch.length () > 0) {
                    rc.addElement (scratch.toString ());
                    rccnt++;
                }
                state = 0;
                break;
            }
        }
        return rccnt > 0 ? rc : null;
    }

    /** Checks a select value if its just a pure
     * (string or numeric) data for marking it
     * as fixed value to avoid including it in
     * the global select call
     * @param str the string to check
     * @return true, if its pure data, false otherwise
     */
    private boolean isPureData (String str) {
        int slen = str.length ();

        if (slen > 0) {
            if ((slen >= 2) && (str.charAt (0) == '\'') && (str.charAt (slen - 1) == '\''))
                return true;
            if (slen > 0) {
                int n;
                char    ch;

                n = 0;
                while (n < slen) {
                    ch = str.charAt (n);
                    if (((n == 0) && (ch == '-')) || Character.isDigit (ch))
                        ++n;
                    else
                        break;
                }
                if (n == slen)
                    return true;
            }
        }
        return false;
    }

    /** Callbacks for mutable tag substituion
     */
    public void cb_sub_setup (String id, Hashtable <String, String> param) {
    }
    public void cb_sub_done (String id, Hashtable <String, String> param) {
    }
    public String cb_sub_exec (String id, Hashtable <String, String> param, Object privData) {
        PrivateData pd = (PrivateData) privData;

        return replaceMutable (id, param, pd.datap, pd.cinfop);
    }

    public void registerMutable (Sub mutable) {
        mutable.reg ("agnUID", this);
    }
    public String replaceMutable (String id, Hashtable <String, String> param, Object datap, Object cinfop) {
        String      rc;

        if (id.equals ("agnUID")) {
            Custinfo    cinfo = (Custinfo) cinfop;
            long        urlID;

            if (param.containsKey ("id"))
                urlID = Long.parseLong (param.get ("id"));
            else
                urlID = 0;
            try {
                rc = cinfo.makeUID (urlID);
            } catch (Exception e) {
                rc = null;
            }
        } else
            rc = null;
        return rc;
    }


    /** Constructor
     * @param data Reference to configuration
     * @param tag the tag itself
     * @param isCustom if this is handled elsewhere
     */
    public EMMTag(Data data, String tag, boolean isCustom) throws Exception {
        mTagFullname = tag;
        mTagParameters = new Hashtable <String, String> ();
        invalidParameters = null;

        // parse the tag
        Vector <String> parsed = splitTag ();
        int     pcnt;

        if ((parsed == null) || ((pcnt = parsed.size ()) == 0))
            throw new Exception ("Failed in parsing (empty?) tag " + mTagFullname);

        mTagName = parsed.elementAt (0);
        for (int n = 1; n < pcnt; ++n) {
            String  parm = parsed.elementAt (n);
            int pos = parm.indexOf ('=');

            if (pos != -1) {
                String  variable = parm.substring (0, pos);
                String  value = parm.substring (pos + 1);

                mTagParameters.put (variable, value);
            } else {
                if (invalidParameters == null) {
                    invalidParameters = new StringBuffer ();
                } else {
                    invalidParameters.append (", ");
                }
                invalidParameters.append (parm);
            }
        }
        mNoOfParameters = mTagParameters.size ();

        // check for special URL Tags

        // return if tag is a url tag, otherwise get tag info from database
        if (isCustom) {
            mTagValue = null;
            tagType = TAG_CUSTOM;
            tagSpec = 0;
            fixedValue = false;
            globalValue = false;
            mutableValue = false;
        } else if(check_tags(data) == TAG_DBASE){
            String[]    rc = data.getTag (mTagName);

            if (rc == null) {
                throw new EMMTagException (data,
                    "ERROR-Code AGN-2002: failure while querying tag = '" + this.mTagName + "'");
            }
            mSelectString = rc[0];
            mSelectType = rc[1];

            if (mSelectString == null) {
                if (! dynamicTag (data)) {
                    throw new EMMTagException(data,
                            "ERROR-Code AGN-2004: no valid entry found for tagname = '" + this.mTagName + "'");
                }
            }
            interpretTagType (data);

            if (tagType == TAG_DBASE) {
                int pos, end;
                boolean hasMutableID;

                pos = 0;
                hasMutableID = false;
                while ((pos = mSelectString.indexOf ("[", pos)) != -1)
                    if ((end = mSelectString.indexOf ("]", pos + 1)) != -1) {
                        String  id = mSelectString.substring (pos + 1, end);
                        String  rplc = null;

                        if (id.equals ("company-id"))
                            rplc = Long.toString (data.company_id);
                        else if (id.equals ("mailinglist-id"))
                            rplc = Long.toString (data.mailinglist_id);
                        else if (id.equals ("mailing-id"))
                            rplc = Long.toString (data.mailing_id);
                        else if (id.equals ("rdir-domain"))
                            rplc = data.rdirDomain;
                        else {
                            hasMutableID = true;
                            rplc = "[" + id + "]";
                        }
                        mSelectString = (pos > 0 ? mSelectString.substring (0, pos) : "") +
                                (rplc == null ? "" : rplc) +
                                (end < mSelectString.length () - 1 ? mSelectString.substring (end + 1) : "");
                        pos += rplc.length () - (id.length () + 2) + 1;
                    } else
                        break;

                // replace arguments of complex tags (in curly braces)
                //
                if (isComplex) {
                    for ( Enumeration <String> e = mTagParameters.keys(); e.hasMoreElements(); ) {
                        String alias = "{" + e.nextElement() + "}";
                        if (mSelectString.indexOf(alias) == -1)
                            throw new EMMTagException(data,
                                "ERROR-Code AGN-2005: parameter '" + alias +
                                "' not found in tag entry");
                        mSelectString = StringOps.replace( mSelectString,
                            alias, mTagParameters.get (alias.substring(1, alias.length() - 1)) );
                    }

                    if (mSelectString.indexOf("{") != -1)
                        throw new EMMTagException(data,
                                      "ERROR-Code AGN-2006: missing required parameter '" + this.mSelectString.substring(mSelectString.indexOf("{") + 1, this.mSelectString.indexOf("}")) + "' in tag = '" + this.mTagName + "'");
                } else if (mNoOfParameters > 0)
                    throw new EMMTagException(data,
                        "ERROR-Code AGN-2007: a simple tag cannot have additional parameters!");


                if ((tagSpec == TDB_IMAGE) || isPureData (mSelectString)) {
                    mTagValue = StringOps.unSqlString (mSelectString);
                    if (hasMutableID) {
                        mutableValue = true;
                        mutable = new Sub ();
                        mutable.parse (mTagValue, "\\[([^]]+)\\]", "[ \t]*([^ \t]+)", "([A-Za-z0-9_-]+)=(\"[^\"]*\"|[^ \t]*)", "^\"(.*)\"$");
                        registerMutable (mutable);
                        mutablePD = new PrivateData ();
                        mutablePD.datap = data;
                    } else
                        fixedValue = true;
                }
            }
        }
    }

    /** parse specific tag type found in database
     */
    public void interpretTagType (Object datap) {
        if ((tagType == TAG_DBASE) && (mSelectType != null)) {
            if (mSelectType.equals ("COMPLEX"))
                isComplex = true;
        }
    }

    /** Try to interpret an unknown tag in a dynamic way,
     * guessing what the user tries to use
     * @return True, if we interpreted it someway
     */
    public boolean dynamicTag (Object datap) {
        Data data = (Data) datap;
        boolean rc = false;

        if (mTagName.length () > 3) {
            String  column = mTagName.substring (3);

            if (data.columnByName (column) != null) {
                tagType = TAG_INTERNAL;
                tagSpec = TI_DB;
                mTagParameters.clear ();
                mTagParameters.put ("column", column);
                mNoOfParameters = mTagParameters.size ();
                rc = true;
            }
        }
        return rc;
    }

    /** Check for advanced tags
     * @param data Reference to configuration
     * @return true, if tag had been interpreted, false otherwise
     */
    public boolean checkAdvancedTags (Object datap) {
        return false;
    }

    /** Determinate the type of the tag
     * @param data Reference to configuration
     */
    private int check_tags(Data data) {
        fixedValue = false;
        globalValue = false;
        mutableValue = false;
        if(this.mTagName.equals("agnPROFILE_OLD") ){
            tagType = TAG_URL;
            tagSpec = 1;
        } else if(this.mTagName.equals("agnUNSUBSCRIBE_OLD") ){
            tagType = TAG_URL;
            tagSpec = 2;
        } else if(this.mTagName.equals("agnAUTOURL_OLD") ){
            tagType = TAG_URL;
            tagSpec = 3;
        } else if (mTagName.equals ("agnONEPIXEL")) {
            tagType = TAG_URL;
            tagSpec = 4;
        } else {
            int n;

            for (n = 0; n < TAG_INTERNALS.length; ++n)
                if (mTagName.equals (TAG_INTERNALS[n]))
                    break;
            if (n < TAG_INTERNALS.length) {
                tagType = TAG_INTERNAL;
                tagSpec = n;
            } else if (! checkAdvancedTags (data)) {
                tagType = TAG_DBASE;
                tagSpec = 0;
                for (n = 0; n < TAG_DB.length; ++n)
                    if ((TAG_DB[n] != null) && (mTagName.equals (TAG_DB[n]))) {
                        tagSpec = n;
                        break;
                    }
            }
        }
        return tagType;
    }

    /** Collect allowed parameter by this tag for
     * more strict tag checking
     * @param collect the vector to collect the allowed names
     */
    public void collectAllowedParameters (Vector <String> collect) {
        switch (tagType) {
        case TAG_DBASE:
            switch (tagSpec) {
            case TDB_UNSPEC:
                collect.add ("*");
                break;
            case TDB_IMAGE:
                collect.add ("name");
                break;
            }
            break;
        case TAG_URL:
            collect.add ("");
            break;
        case TAG_INTERNAL:
            switch (tagSpec) {
            case TI_DBV:
            case TI_DB:
                collect.add ("column");
                collect.add ("format");
                collect.add ("language");
                collect.add ("country");
                collect.add ("timezone");
                break;
            case TI_EMAIL:
                collect.add ("code");
                break;
            case TI_MESSAGEID:
            case TI_UID:
            case TI_SUBSCRIBERCOUNT:
                collect.add ("");
                break;
            case TI_DATE:
                collect.add ("type");
                collect.add ("language");
                collect.add ("country");
                collect.add ("offset");
                collect.add ("format");
                break;
            case TI_SYSINFO:
                collect.add ("default");
                break;
            case TI_DYN:
            case TI_DYNVALUE:
                collect.add ("name");
                break;
            case TI_TITLE:
            case TI_TITLEFULL:
            case TI_TITLEFIRST:
                collect.add ("type");
                collect.add ("prefix");
                collect.add ("postfix");
                break;
            case TI_IMGLINK:
                collect.add ("name");
                break;
            }
            break;
        case TAG_CUSTOM:
            break;
        }
    }

    /** Initialize the tag, if its an internal one
     * @param data Reference to configuration
     */
    public void initializeInternalTag (Object datap, boolean strict) throws Exception {
        Data data = (Data) datap;
        switch (tagSpec) {
        case TI_DBV:
        case TI_DB:
            mSelectString = mTagParameters.get ("column");
            dbColumn = null;
            dbFormat = new Format (mTagParameters.get ("format"), mTagParameters.get ("language"), mTagParameters.get ("country"), mTagParameters);
            dbOverwrite = null;
            if (tagSpec == TI_DBV) {
                if (mSelectString != null)
                    mSelectString = mSelectString.trim ().toUpperCase ();
                else {
                    data.logging (Log.WARNING, "emmtag", "Missing virtual column");
                    if (strict)
                        throw new Exception ("Missing parameter \"column\"");
                }
            } else {
                if (mSelectString != null) {
                    mSelectString = mSelectString.trim ();

                    Column  col = data.columnByName (mSelectString);

                    if (col == null) {
                        String  orig = mSelectString;
                        Column  alias = data.columnByAlias (mSelectString);
                        int len = mSelectString.length ();
                        int n;

                        for (n = 0; n < len; ++n) {
                            char    ch = mSelectString.charAt (n);

                            if (((n == 0) && (! Character.isLetter (ch)) && (ch != '_')) ||
                                ((n > 0) && (! Character.isLetterOrDigit (ch)) && (ch != '_')))
                                break;
                        }
                        if (n < len) {
                            mSelectString = mSelectString.substring (0, n);
                            col = data.columnByName (mSelectString);
                        }
                        if ((col == null) && (alias != null)) {
                            mSelectString = alias.name;
                            col = alias;
                        } else {
                            data.logging (Log.WARNING, "emmtag", "Unknown column referenced for " + TAG_INTERNALS[TI_DB] + ": " + orig);
                            if (strict)
                                throw new Exception ("Invalid value for paramter \"column\"");
                        }
                    }

                    if (col != null) {
                        String  err = col.validate (dbFormat);

                        if (err != null) {
                            data.logging (Log.WARNING, "emmtag", "Invalid format: " + err);
                            if (strict) {
                                throw new Exception ("Invalid formating using parameter \"format\"");
                            }
                        }
                    }
                    dbColumn = col;
                } else {
                    data.logging (Log.WARNING, "emmtag", "Missing column parameter for " + TAG_INTERNALS[TI_DB]);
                    if (strict)
                        throw new Exception ("Missing parameter \"column\"");
                }
            }
            break;
        case TI_EMAIL:
            emailCode = 0;
            {
                String  code = mTagParameters.get ("code");

                if (code != null)
                    if (code.equals ("punycode"))
                        emailCode = 1;
                    else {
                        data.logging (Log.WARNING, "emmtag", "Unknown coding for email found: " + code);
                        if (strict)
                            throw new Exception ("Invalid value for parameter \"code\"");
                    }
            }
            break;
        case TI_SUBSCRIBERCOUNT:
            globalValue = true;
            break;
        case TI_DATE:
            try {
                String  temp;
                int type;
                String  lang;
                String  country;
                String  typestr;

                if ((temp = mTagParameters.get ("type")) != null)
                    type = Integer.parseInt (temp);
                else
                    type = 0;
                lang = mTagParameters.get ("language");
                country = mTagParameters.get ("country");
                dateOffset = 0;
                if ((temp = mTagParameters.get ("offset")) != null) {
                    try {
                        dateOffset = (long) (Double.parseDouble (temp) * (24 * 60 * 60));
                    } catch (NumberFormatException e) {
                        data.logging (Log.WARNING, "emmtag", "Invalid offset " + temp + " for date found: " + e.toString ());
                        if (strict)
                            throw new Exception ("Invalid offset \"" + temp + "\"");
                    }
                }
                if ((temp = mTagParameters.get ("format")) != null) {
                    typestr = temp;
                } else {
                    SimpleJdbcTemplate  jdbc = null;
                    String          query = "SELECT format FROM date_tbl WHERE type = :type";

                    typestr = "d.M.yyyy";
                    try {
                        Map <String, Object>    row;

                        jdbc = data.dbase.request (query);
                        row = data.dbase.querys (jdbc, query, "type", type);
                        if (row != null) {
                            typestr = data.dbase.asString (row.get ("format"));
                        } else {
                            data.logging (Log.WARNING, "emmtag", "No format in date_tbl found for " + mTagFullname);
                            if (strict)
                                throw new Exception ("No format in database found for paramter \"type\" " + type);
                        }
                    } catch (Exception e) {
                        data.logging (Log.WARNING, "emmtag", "Query failed for data_tbl: " + e);
                        if (strict)
                            throw e;
                    } finally {
                        if (jdbc != null) {
                            data.dbase.release (jdbc, query);
                        }
                    }
                }
                Locale  l = data.getLocale (lang, country);

                if (l == null) {
                    dateFormat = new SimpleDateFormat (typestr);
                } else {
                    dateFormat = new SimpleDateFormat (typestr, l);
                }
            } catch (Exception e) {
                data.logging (Log.WARNING, "emmtag", "Failed parsing tag " + mTagFullname + " (" + e.toString () + ")");
                if (strict)
                    throw e;
            }
            globalValue = true;
            break;
        case TI_SYSINFO:
            {
                String  dflt = mTagParameters.get ("default");

                mTagValue = dflt == null ? "" : dflt;
            }
            globalValue = true;
            break;
        case TI_DYN:
        case TI_DYNVALUE:
            if (strict) {
                if (mTagParameters.get ("name") == null) {
                    throw new Exception ("Missing parameter \"name\"");
                }
            }
            break;
        case TI_TITLE:
        case TI_TITLEFULL:
        case TI_TITLEFIRST:
            {
                String  temp;

                if ((temp = mTagParameters.get ("type")) != null) {
                    try {
                        titleType = new Long (temp);
                    } catch (java.lang.NumberFormatException e) {
                        data.logging (Log.WARNING, "emmtag", "Invalid type string type=\"" + temp + "\", using default 0");
                        titleType = new Long (1);
                        if (strict)
                            throw new Exception ("Invalid value for parameter \"type\"");
                    }
                } else {
                    titleType = new Long (1);
                }
                titlePrefix = mTagParameters.get ("prefix");
                titlePostfix = mTagParameters.get ("postfix");
                if (tagSpec == TI_TITLE) {
                    titleMode = Title.TITLE_DEFAULT;
                } else if (tagSpec == TI_TITLEFULL) {
                    titleMode = Title.TITLE_FULL;
                } else if (tagSpec == TI_TITLEFIRST) {
                    titleMode = Title.TITLE_FIRST;
                }
                if (strict) {
                    Title   title = data.getTitle (titleType);
                    HashSet <String>
                        cols = new HashSet <String> ();

                    if (title == null)
                        throw new Exception ("No title for title type=\"" + temp + "\" found");
                    title.requestFields (cols, titleMode);
                    for (Iterator <String> i = cols.iterator (); i.hasNext (); ) {
                        String  col = i.next ();

                        if (data.columnByName (col) == null)
                            throw new Exception ("Column \"" + col + "\" required by title type=\"" + temp + "\" not found in customer table");
                    }
                }
            }
            break;
        case TI_IMGLINK:
            {
                String  name = mTagParameters.get ("name");

                ilURL = null;
                ilPrefix = null;
                ilPostfix = null;
                if (name != null) {
                    ilURL = data.defaultImageLink (name);
                    ilPrefix = "<a href=\"";
                    ilPostfix = "\"><img src=\"" + ilURL + "\" border=\"0\"></a>";
                } else {
                    data.logging (Log.WARNING, "emmtag", "Missing name");
                    if (strict)
                        throw new Exception ("Missing parameter \"name\"");
                }
            }
            break;
        }
    }

    public void initialize (Object datap, boolean strict) throws Exception {
        Data    data = (Data) datap;
        if (invalidParameters != null) {
            data.logging (Log.WARNING, "emmtag", "Invalid parameters: " + invalidParameters);
            if (strict)
                throw new Exception ("Invalid parameters: \"" + invalidParameters + "\"");
        }
        if (strict) {
            if (mTagParameters.size () > 0) {
                Vector <String> allowedParameters = new Vector <String> ();
                StringBuffer    notAllowed = null;

                collectAllowedParameters (allowedParameters);
                for (Enumeration <String> e = mTagParameters.keys (); e.hasMoreElements(); ) {
                    String  name = e.nextElement ();
                    boolean found = false;

                    for (int n = 0; n < allowedParameters.size (); ++n) {
                        String  parm = allowedParameters.get (n);

                        if (parm.equals (name) || parm.equals ("*")) {
                            found = true;
                            break;
                        } else if (parm.equals ("")) {
                            break;
                        }
                    }
                    if (! found) {
                        if (notAllowed == null) {
                            notAllowed = new StringBuffer ();
                        } else {
                            notAllowed.append (", ");
                        }
                        notAllowed.append (name);
                    }
                }
                if (notAllowed != null)
                    throw new Exception ("Tag does not support these parameter: " + notAllowed);
            }
        }
        switch (tagType) {
        case TAG_INTERNAL:
            initializeInternalTag (datap, strict);
            break;
        }
    }

    /** Set link reference for image link tag
     * @param data Reference to configuration
     * @param urlID id of referenced URL
     */
    protected void imageLinkReference (Object datap, long urlID) {
        Data    data = (Data) datap;
        String  destination = ilURL;

        for (int n = 0; n < data.urlcount; ++n) {
            URL url = data.URLlist.elementAt (n);

            if (url.id == urlID) {
                destination = url.url;
                break;
            }
        }
        mTagValue = ilPrefix + destination + ilPostfix;
        fixedValue = true;
    }

    /** Handle special cases on internal tags
     * @param data Reference to configuration
     */
    public String makeInternalValue (Object datap, Object cinfop) throws Exception {
        Data data = (Data) datap;
        Custinfo cinfo = (Custinfo) cinfop;

        if (tagType != TAG_INTERNAL) {
            throw new Exception ("Call makeInternalValue with tag type " + tagType);
        }
        switch (tagSpec) {
        case TI_DBV:         // is set before in Mailgun.realFire ()
            break;
        case TI_DB:
            if (dbOverwrite != null) {
                mTagValue = dbOverwrite;
            } else if (dbColumn != null) {
                mTagValue = dbColumn.get (dbFormat);
            }
            break;
        case TI_EMAIL:
            switch (emailCode) {
            case 1:
                if (mTagValue != null)
                    mTagValue = StringOps.punycoded (mTagValue.trim ());
                break;
            }
            break;
        case TI_MESSAGEID:      // is set before in MailWriter.writeMail()
        case TI_UID:            // is set before in MailWriter.writeMail()
            break;
        case TI_SUBSCRIBERCOUNT:
            {
                long    cnt = data.totalSubscribers;
                String  format = null;
                String  str;

                if (((format = mTagParameters.get ("format")) == null) &&
                    ((str = mTagParameters.get ("type")) != null)) {
                    str = str.toLowerCase ();
                    if (str.equals ("us"))
                        format = "#,###,###";
                    else if (str.equals ("de"))
                        format = "#.###.###";
                }
                if ((str = mTagParameters.get ("round")) != null) {
                    try {
                        int round = Integer.parseInt (str);

                        if (round > 0)
                            cnt = (cnt + round - 1) / round;
                    } catch (NumberFormatException e) {
                        ;
                    }
                }
                if (format != null) {
                    int len = format.length ();
                    boolean first = true;
                    int last = -1;
                    mTagValue = "";

                    for (int n = len - 1; n >= 0; --n)
                        if (format.charAt (n) == '#')
                            last = n;
                    for (int n = len - 1; n >= 0; --n)
                        if (format.charAt (n) == '#') {
                            if (first || (cnt != 0)) {
                                if (n == last) {
                                    str = Long.toString (cnt);
                                    cnt = 0;
                                } else {
                                    str = Long.toString (cnt % 10);
                                    cnt /= 10;
                                }
                                mTagValue = str + mTagValue;
                                first = false;
                            }
                        } else if ((n < last) || (cnt != 0))
                            mTagValue = format.substring (n, n + 1) + mTagValue;
                } else
                    mTagValue = Long.toString (cnt);
            }
            break;
        case TI_DATE:           // is prepared here in initializeInternalTag () from check_tags
            if (dateOffset == 0) {
                mTagValue = dateFormat.format (data.currentSendDate);
            } else {
                Date    offset = new Date (data.currentSendDate.getTime () + (dateOffset * 1000));

                mTagValue = dateFormat.format (offset);
            }
            break;
        case TI_SYSINFO:        // is set here in initializeInternalTag () from check_tags
            break;
        case TI_DYN:            // is handled in xml backend
        case TI_DYNVALUE:       // dito
            break;
        case TI_TITLE:
        case TI_TITLEFULL:
        case TI_TITLEFIRST:
            {
                Title   title = data.getTitle (titleType);

                if (title != null) {
                    mTagValue = title.makeTitle (titleMode, cinfo.gender, cinfo.title, cinfo.firstname, cinfo.lastname, cinfo.columns, null);
                    if ((mTagValue.length () > 0) && ((titlePrefix != null) || (titlePostfix != null))) {
                        mTagValue = (titlePrefix == null ? "" : titlePrefix) + mTagValue + (titlePostfix == null ? "" : titlePostfix);
                    }
                } else {
                    mTagValue = "";
                }
            }
            break;
        case TI_IMGLINK:        // is set in imageLinkReference
            break;
        }
        return mTagValue;
    }

    public void requestFields (Object datap, HashSet <String> predef) throws Exception {
        if (tagType == TAG_INTERNAL) {
            switch (tagSpec) {
            case TI_DB:
                if (dbColumn != null) {
                    predef.add (dbColumn.qname);
                }
                break;
            case TI_TITLE:
            case TI_TITLEFULL:
            case TI_TITLEFIRST:
                {
                    Data    data = (Data) datap;
                    Title   title = data.getTitle (titleType);

                    if (title != null) {
                        title.requestFields (predef, titleMode);
                    }
                }
                break;
            }
        }
    }

    public String getType () {
        return mSelectType;
    }

    public String makeMutableValue (Object datap, Object cinfop) {
        mutablePD.cinfop = cinfop;
        return mutable.sub (mutablePD);
    }

    /** String representation of outself
     * @return our representation
     */
    public String toString () {
        return mTagFullname +
            " (" + (isComplex ? "complex," : "") + tagType + "," + tagSpec + ")" +
            " = " +
            (mSelectString == null ? "" : "[" + mSelectString + "]") +
            (mTagValue == null ? "*unset*" : "\"" + mTagValue + "\"");
    }

    /** Create a string of an internal tag
     * @param ti the internal tag ID
     * @return its string representation
     */
    static public String internalTag (int ti) {
        if ((ti >= 0) && (ti < TAG_INTERNALS.length))
            return "[" + TAG_INTERNALS[ti] + "]";
        return null;
    }
}
