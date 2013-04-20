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
import java.io.FileInputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.ResourceBundle;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import org.agnitas.util.Const;
import org.agnitas.beans.BindingEntry;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.preview.Page;
import org.agnitas.util.Config;
import org.agnitas.util.Log;
import org.agnitas.util.Title;

/** Class holding most of central configuration and global database
 * information
 */
public class Data {
    final static long serialVersionUID = 0x055da1a;
    /** the file to read the configuration from */
    final static String INI_FILE = "Mailgun.ini";
    /** default value for domain entry */
    final static String DEF_DOMAIN = "openemm.org";
    /** default value for boundary entry */
    final static String DEF_BOUNDARY = "AGNITAS";
    /** default value for EOL coding */
    final static String DEF_EOL = "\r\n";
    /** default value for X-Mailer: header */
    final static String DEF_MAILER = "OpenEMM 2013/Agnitas AG";

    /** Constant for onepixellog: no automatic insertion */
    final public static int OPL_NONE = 0;
    /** Constant for onepixellog: insertion on top */
    final public static int OPL_TOP = 1;
    /** Constant for onepixellog: insertion at bottom */
    final public static int OPL_BOTTOM = 2;

    /** Configuration from properties file */
    protected Config    cfg = null;
    /** Loglevel */
    private int     logLevel = Log.ERROR;
    /** directory to write admin-/testmails to */
    public String       mailDir = null;
    /** default encoding for all blocks */
    public String       defaultEncoding = "quoted-printable";
    /** default character set for all blocks */
    public String       defaultCharset = "UTF-8";
    /** database driver name */
    protected String    dbDriver = null;
    /** database login */
    protected String    dbLogin = null;
    /** database password */
    protected String    dbPassword = null;
    /** database connect expression */
    protected String    dbConnect = null;
    /** database pool size */
    protected int       dbPoolsize = 12;
    /** database growable pool */
    protected boolean   dbPoolgrow = true;
    /** used block size */
    private int     blockSize = 1000;
    /** directory to store meta files for further processing */
    private String      metaDir = null;
    /** name of program to execute meta files */
    private String      xmlBack = "xmlback";
    /** validate each generated block */
    private boolean     xmlValidate = false;
    /** Send samples of worldmailing to dedicated address(es) */
    private String      sampleEmails = null;
    /** write a DB record after creating that number of receiver */
    private int     mailLogNumber = 0;
    /** path to accounting logfile */
    private String      accLogfile = "log/account.log";
    /** path to bounce logfile */
    private String      bncLogfile = "log/extbounce.log";

    /** the user_status for this query */
    public long     defaultUserStatus = BindingEntry.USER_STATUS_ACTIVE;
    /** in case of campaing mailing, send mail only to this customer */
    public long     campaignCustomerID = 0;
    /** in case of a transaction, use this transaction ID */
    public long     campaignTransactionID = 0;
    /** for campaign mailings, use this user status in the binding table */
    public long     campaignUserStatus = BindingEntry.USER_STATUS_ACTIVE;
    /** for sending to one fix address */
    public String       fixedEmail = null;
    /** for preview mailings use this for matching the customer ID */
    public long     previewCustomerID = 0;
    /** for preview mailings store output */
    public Page     previewOutput = null;
    /** for preview of external text input */
    public String       previewInput = null;
    /** for preview mailings if preview should be anon */
    public boolean      previewAnon = false;
    /** for preview mailings if only partial preview requested */
    public String       previewSelector = null;
    /** for preview mailings if preview is cachable */
    public boolean      previewCachable = true;
    /** for preview mailings to convert to entities */
    public boolean      previewConvertEntities = false;
    /** for preview mailings to use legacy UIDs */
    public boolean      previewLegacyUIDs = false;
    /** for preview mailings to create all possible parts */
    public boolean      previewCreateAll = false;
    /** alternative campaign mailing selection */
    public TargetRepresentation
                campaignSubselect = null;
    /** custom generated tags */
    public Vector <String>  customTags = null;
    /** custom generated tags with values */
    public Hashtable <String, String>
                customMap = null;
    /** overwtite existing database fields */
    public Hashtable <String, String>
                overwriteMap = null;
    /** overwtite existing database fields for more receivers */
    public Hashtable <Long, Hashtable <String, String>>
                overwriteMapMulti = null;
    /** virtual database fields */
    public Hashtable <String, String>
                virtualMap = null;
    /** virtual database fields for more receivers */
    public Hashtable <Long, Hashtable <String, String>>
                virtualMapMulti = null;
    /** optional infos for that company */
    public Hashtable <String, String>
                companyInfo = null;
    /** optional infos for this mailing */
    public Hashtable <String, String>
                mailingInfo = null;
    /** keeps track of already read EMMTags from database */
    private Hashtable <String, String[]>
                tagCache = null;
    /** instance to write logs to */
    private Log     log = null;
    /** the ID to write as marker in the logfile */
    private String      lid = null;
    /** the connection to the database */
    public DBase        dbase = null;
    /** status_id from maildrop_status_tbl */
    public long     maildrop_status_id = -1;
    /** assigned company to this mailing */
    public long     company_id = -1;
    /** mailinglist assigned to this mailing */
    public long     mailinglist_id = -1;
    /** for subselect, the target expression of the mailing */
    public String       targetExpression = null;
    /** mailign_id of this mailing */
    public long     mailing_id = -1;
    /** status_field from maildrop_status_tbl */
    public String       status_field = null;
    /** when to send the mailing, date */
    public Date     senddate = null;
    /** when to send the mailing, time */
    public Time     sendtime = null;
    /** when to send the mailing, date+time */
    public Timestamp    sendtimestamp = null;
    /** current send date, calculated from sendtimstamp and stepping */
    public java.util.Date   currentSendDate = null;
    /** the currentSendDate in epoch */
    public long     sendSeconds = 0;
    /** steps in seconds between two entities */
    public int      step = 0;
    /** number of blocks per entity */
    public int      blocksPerStep = 1;
    /** start steping at this block */
    public int      startBlockForStep = 1;
    /** the subselection for the receiver of this mailing */
    public String       subselect = null;
    /** the name of this mailing */
    public String       mailing_name = null;
    /** the subject for this mailing */
    public String       subject = null;
    /** the sender address for this mailing */
    public EMail        fromEmail = null;
    /** the optional reply-to address for this mailing */
    public EMail        replyTo = null;
    /** the envelope address */
    public EMail        envelopeFrom = null;
    /** the encoding for this mailing */
    public String       encoding = null;
    /** the charachter set for this mailing */
    public String       charset = null;
    /** domain used to build message-ids */
    public String       domain = DEF_DOMAIN;
    /** boundary part to build multipart messages */
    public String       boundary = DEF_BOUNDARY;
    /** EOL coding for spoolfiles */
    public String       eol = DEF_EOL;
    /** content of the X-Mailer: header */
    public String       mailer = DEF_MAILER;
    /** the base for the profile URL */
    public String       profileURL = null;
    public String       profileTag = "/p.html?";
    /** the base for the unsubscribe URL */
    public String       unsubscribeURL = null;
    public String       unsubscribeTag = "/uq.html?";
    /** the base for the auto URL */
    public String       autoURL = null;
    public String       autoTag = "/r.html?";
    /** the base for the onepixellog URL */
    public String       onePixelURL = null;
    public String       onePixelTag = "/g.html?";
    /** the largest mailtype to generate */
    public int      masterMailtype = Const.Mailtype.MASK;
    /** default line length in text part */
    public int      lineLength = 72;
    /** where to automatically place the onepixellog */
    public int      onepixlog = OPL_NONE;
    /** Password for signatures */
    public String       password = null;
    /** the base domain to build the base URLs */
    public String       rdirDomain = null;
    /** the mailloop domain */
    public String       mailloopDomain = null;
    /** Collection of media information */
    public Media        media = null;
    /** Bitfield of available media types in mailing */
    public long     availableMedias = 0;
    /** number of all subscriber of a mailing */
    public long     totalSubscribers = -1;
    public long     totalReceivers = -1;
    /** number of all subscriber of a mailing */
    private BC      bigClause = null;
    /** keep track of collected targets */
    private Hashtable <Long, Target>
                targets = null;
    /** all URLs from rdir_url_tbl */
    public Vector <URL>
                URLlist = null;
    /** number of entries in URLlist */
    public int      urlcount = 0;
    /** all title tags */
    private Hashtable <Long, Title>
                titles = null;
    /** layout of the customer table */
    public Vector <Column>  layout = null;
    /** number of entries in layout */
    public int      lcount = 0;
    /** number of entries in layout used */
    public int      lusecount = 0;
    /** name of the company (for logfile display only) */
    public String       company_name = null;
    /** name of mailtracking table */
    public String       mailtracking_table = null;
    /** for housekeeping of created files */
    private Vector <String> toRemove = null;

    /** check if database is available
     */
    private void checkDatabase () throws Exception {
        if (dbase == null)
            throw new Exception ("Database not available");
    }

    public Object mkDBase (Object me) throws Exception {
        return new DBase ((Data) me);
    }

    public Object mkBigClause () {
        return new BC ();
    }

    /**
     * setup database connection and retreive a list of all available
     * tables
     * @param conn an optional existing database connection
     */
    private void setupDatabase () throws Exception {
        try {
            dbase = (DBase) mkDBase (this);
            dbase.setup ();
        } catch (Exception e) {
            throw new Exception ("Database setup failed: " + e);
        }
    }

    /** close a database and free all assigned data
     */
    private void closeDatabase () throws Exception {
        if (dbase != null) {
            try {
                dbase.done ();
                dbase = null;
            } catch (Exception e) {
                throw new Exception ("Database close failed: " + e);
            }
        }
    }

    /**
     * find an entry from the media record for this mailing
     * @param m instance of media record
     * @param id the ID to look for
     * @param dflt a default value if no entry is found
     * @return the found entry or the default
     */
    public String findMediadata (Media m, String id, String dflt) {
        String  rc;

        Vector<String> v = m.findParameterValues (id);
        rc = null;
        if ((v != null) && (v.size () > 0))
            rc = v.elementAt (0);
        return rc == null ? dflt : rc;
    }

    /**
     * find a numeric entry from the media record for this mailing
     * @param m instance of media record
     * @param id the ID to look for
     * @param dflt a default value if no entry is found
     * @return the found entry or the default
     */
    public long ifindMediadata (Media m, String id, long dflt) {
        String  tmp = findMediadata (m, id, null);
        long rc;

        if (tmp != null)
            try {
                rc = Integer.parseInt (tmp);
            } catch (Exception e) {
                rc = dflt;
            }
        else
            rc = dflt;
        return rc;
    }
    public int ifindMediadata (Media m, String id, int dflt) {
        return (int) ifindMediadata (m, id, (long) dflt);
    }

    /**
     * find a boolean entry from the media record for this mailing
     * @param m instance of media record
     * @param id the ID to look for
     * @param dflt a default value if no entry is found
     * @return the found entry or the default
     */
    public boolean bfindMediadata (Media m, String id, boolean dflt) {
        String  tmp = findMediadata (m, id, null);
        boolean rc = dflt;

        if (tmp != null)
            if (tmp.length () == 0)
                rc = true;
            else {
                String tok = tmp.substring (0, 1).toLowerCase ();

                if (tok.equals ("t") || tok.equals ("y") ||
                    tok.equals ("+") || tok.equals ("1"))
                    rc = true;
            } else
                rc = false;
        return rc;
    }

    /**
     * Retreive basic mailing data
     */
    public void retreiveMailingInformation () throws Exception {
        Map <String, Object>    rc;

        rc = dbase.querys ("SELECT mailinglist_id, shortname, target_expression " +
                   "FROM mailing_tbl WHERE mailing_id = :mailingID", "mailingID", mailing_id);
        mailinglist_id = dbase.asInt (rc.get ("mailinglist_id"));
        mailing_name = dbase.asString (rc.get ("shortname"));
        targetExpression = dbase.asString (rc.get ("target_expression"));
        if (isPreviewMailing ()) {
            targetExpression = null;
        }
    }

    public Object mkMedia (int mediatype, int priority, int status, String parameter) {
        return new Media (mediatype, priority, status, parameter);
    }

    /**
     * Retreive the media data
     */
    public void retreiveMediaInformation () throws Exception {
        List <Map <String, Object>> rc;

        rc = dbase.query ("SELECT mediatype, param FROM mailing_mt_tbl " +
                  "WHERE mailing_id = :mailingID", "mailingID", mailing_id);
        if (rc.size () > 0) {
            Map <String, Object>    row = rc.get (0);

            media = (Media) mkMedia (dbase.asInt (row.get ("mediatype")), 0, Media.STAT_ACTIVE, dbase.asString (row.get ("param")));
            availableMedias = (1 << Media.TYPE_EMAIL);
            if (media.findParameterValues ("charset") == null)
                media.setParameter ("charset", defaultCharset);
            if (media.findParameterValues ("encoding") == null)
                media.setParameter ("encoding", defaultEncoding);
            fromEmail = new EMail (findMediadata (media, "from", null));
            replyTo = new EMail (findMediadata (media, "reply", null));
            subject = findMediadata (media, "subject", subject);
            charset = findMediadata (media, "charset", charset);
            masterMailtype = ifindMediadata (media, "mailformat", masterMailtype) & Const.Mailtype.MASK;
            if (masterMailtype == 2) {
                masterMailtype = Const.Mailtype.HTML | Const.Mailtype.HTML_OFFLINE;
            }
            encoding = findMediadata (media, "encoding", encoding);
            lineLength = ifindMediadata (media, "linefeed", lineLength);

            String  opl = findMediadata (media, "onepixlog", "none");
            if (opl.equals ("top"))
                onepixlog = OPL_TOP;
            else if (opl.equals ("bottom"))
                onepixlog = OPL_BOTTOM;
            else
                onepixlog = OPL_NONE;
        }
        envelopeFrom = fromEmail;
    }

    /**
     * query company specific details
     */
    public void retreiveCompanyInfo () throws Exception {
        Map <String, Object>    rc;

        mailtracking_table = "mailtrack_tbl";
        rc = dbase.querys ("SELECT shortname, xor_key, rdir_domain, mailloop_domain FROM company_tbl WHERE company_id = :companyID", "companyID", company_id);
        company_name = dbase.asString (rc.get ("shortname"));
        password = dbase.asString (rc.get ("xor_key"));
        rdirDomain = dbase.asString (rc.get ("rdir_domain"));
        mailloopDomain = dbase.asString (rc.get ("mailloop_domain"));
    }

    public Object mkTarget (long tid, String sql) {
        return new Target (tid, sql);
    }

    public Target getTarget (long tid) throws Exception {
        if (targets == null) {
            targets = new Hashtable <Long, Target> ();
        }

        Long    targetID = new Long (tid);
        Target  rc = targets.get (targetID);
        String  reason = "invalid";

        if (rc == null) {
            String      sql = null;
            int     deleted = 0;

            try {
                Map <String, Object>    row = dbase.querys ("SELECT target_sql, deleted FROM dyn_target_tbl WHERE target_id = :targetID", "targetID", tid);

                deleted = dbase.asInt (row.get ("deleted"));
                if (deleted != 0) {
                    logging (Log.ERROR, "targets", "TargetID " + tid + " is marked as deleted");
                    sql = null;
                    reason = "deleted";
                } else {
                    sql = dbase.asString (row.get ("target_sql"), 3);
                    if (sql == null) {
                        reason = "empty";
                    }
                }
            } catch (Exception e) {
                logging (Log.ERROR, "targets", "No target with ID " + tid + " found in dyn_target_tbl: " + e.toString ());
                sql = null;
                reason = "non existing";
            }
            rc = (Target) mkTarget (tid, sql);
            targets.put (targetID, rc);
        }
        if (! rc.valid ()) {
            throw new Exception ("TargetID " + tid + ": " + reason + " target found");
        }
        return rc;
    }

    public String moreStatusColumns () {
        return null;
    }

    public void moreStatusColumnsParse (Map <String, Object> row) throws Exception {
    }

    public void moreStatusColumnsPostParse () throws Exception {
    }

    public void setupMailingInformations (String prefix, String status) throws Exception {
        if (prefix.equals ("preview")) {
            String[]    opts = status.split (",");

            if (opts.length > 0) {
                try {
                    mailing_id = Long.parseLong (opts[0]);
                } catch (NumberFormatException e) {
                    logging (Log.WARNING, "setup", "Unparseable input string for mailing_id: \"" + opts[0] + "\": " + e.toString ());
                    mailing_id = 0;
                }
            } else {
                mailing_id = 0;
            }
            if (mailing_id > 0) {
                try {
                    company_id = dbase.queryLong ("SELECT company_id FROM mailing_tbl WHERE mailing_id = :mailingID", "mailingID", mailing_id);
                } catch (Exception e) {
                    throw new Exception ("Failed to query company_id for mailing_id " + mailing_id + ": " + e.toString ());
                }
            } else {
                mailing_id = 0;
                company_id = 1;
                mailinglist_id = 1;
                try {
                    if (opts.length > 1) {
                        company_id = Long.parseLong (opts[1]);
                        if (opts.length > 2) {
                            mailinglist_id = Long.parseLong (opts[2]);
                        }
                    }
                } catch (NumberFormatException e) {
                    logging (Log.WARNING, "setup", "Unparseable input string for preview \"" + status + "\": " + e.toString ());
                }
            }
            status_field = "P";
            sendtimestamp = null;
        } else
            throw new Exception ("Unknown status prefix \"" + prefix + "\" encountered");
    }

    class Layout implements org.springframework.jdbc.core.ResultSetExtractor {
        private Vector <Column> layout;
        private String      ref;

        public Layout (Vector <Column> nLayout, String nRef) {
            layout = nLayout;
            ref = nRef;
        }
        public Vector <Column> getLayout () {
            return layout;
        }
        public Object extractData (ResultSet rset) throws SQLException, org.springframework.dao.DataAccessException {
            ResultSetMetaData   meta = rset.getMetaData ();
            int         ccnt = meta.getColumnCount ();

            for (int n = 0; n < ccnt; ++n) {
                String  cname = meta.getColumnName (n + 1);
                int ctype = meta.getColumnType (n + 1);

                if (ctype == -1) {
                    String  tname = meta.getColumnTypeName (n + 1);

                    if (tname != null) {
                        tname = tname.toLowerCase ();
                        if (tname.equals ("varchar")) {
                            ctype = Types.VARCHAR;
                        }
                    }
                }
                if (Column.typeStr (ctype) != null) {
                    Column  c = new Column (cname, ctype);

                    c.setRef (ref);
                    if (layout == null) {
                        layout = new Vector <Column> ();
                    }
                    layout.addElement (c);
                }
            }
            return this;
        }
    }
    protected void getTableLayout (String table, String ref) throws Exception {
        String  query = "SELECT * FROM " + table + " WHERE 1 = 0";
        Layout  temp = new Layout (layout, ref);

        dbase.op ().query (query, temp);

        layout = temp.getLayout ();

        if (layout != null) {
            lcount = layout.size ();
            lusecount = lcount;
        }
    }

    private void setGenerationStatus (int fromStatus, int toStatus) throws Exception {
        if (maildrop_status_id > 0) {
            String  query;

            query = "UPDATE maildrop_status_tbl SET genchange = " + dbase.sysdate + ", genstatus = :tostatus " +
                "WHERE status_id = :statusID";
            if (fromStatus > 0) {
                query += " AND genstatus = :fromStatus";
            }
            try {
                int cnt = dbase.update (query, "tostatus", toStatus, "fromStatus", fromStatus, "statusID",  maildrop_status_id);

                if (cnt != 1) {
                    throw new Exception ("change should affect one row, but affects " + cnt + " rows");
                }
            } catch (Exception e) {
                String  m = "Unable to update generation state " + (fromStatus > 0 ? "from " + fromStatus + " " : "") + "to " + toStatus;

                throw new Exception (m + ": " + e.toString ());
            }
        }
    }

    public String extraURLTableClause () {
        return "";
    }
    public String extraURLTableColumns () {
        return "";
    }
    public void extraURLTableGetColumns (URL url, Map <String, Object> row) {
    }
    public void getURLDetails () {
    }

    /**
     * query all basic information about this mailing
     * @param status_id the reference to the mailing
     */
    private void queryMailingInformations (String status_id) throws Exception {
        checkDatabase ();
        try {
            String[]    sdetail = status_id.split (":", 2);

            if (sdetail.length == 2) {
                setupMailingInformations (sdetail[0], sdetail[1]);
            } else {
                Map <String, Object>
                    row;
                int     bs, st;
                int     genstat;
                String  moreCols;
                String  query;

                maildrop_status_id = Long.parseLong (status_id);

                moreCols = moreStatusColumns ();
                query = "SELECT company_id, mailing_id, status_field, senddate, step, blocksize, genstatus";
                if (moreCols != null) {
                    query += ", " + moreCols;
                }
                query += " FROM maildrop_status_tbl WHERE status_id = :statusID";
                row = dbase.querys (query, "statusID", maildrop_status_id);
                company_id = dbase.asLong (row.get ("company_id"));
                mailing_id = dbase.asLong (row.get ("mailing_id"));
                status_field = dbase.asString (row.get ("status_field"));
                sendtimestamp = (Timestamp) row.get ("senddate");
                st = dbase.asInt (row.get ("step"));
                bs = dbase.asInt (row.get ("blocksize"));
                genstat = dbase.asInt (row.get ("genstatus"));
                moreStatusColumnsParse (row);
                moreStatusColumnsPostParse ();
                if (status_field.equals ("C"))
                    status_field = "E";
                if (bs > 0)
                    setBlockSize (bs);
                setStepping (st);
                if (genstat != 1)
                    throw new Exception ("Generation state is not 1, but " + genstat);
                if (isAdminMailing () || isTestMailing () || isWorldMailing () || isRuleMailing () || isOnDemandMailing ()) {
                    setGenerationStatus (1, 2);
                }
            }
            if (mailing_id > 0) {
                retreiveMailingInformation ();

                if (targetExpression != null) {
                    StringBuffer    buf = new StringBuffer ();
                    int     tlen = targetExpression.length ();

                    for (int n = 0; n < tlen; ++n) {
                        char    ch = targetExpression.charAt (n);

                        if ((ch == '(') || (ch == ')')) {
                            buf.append (ch);
                        } else if ((ch == '&') || (ch == '|')) {
                            if (ch == '&')
                                buf.append (" AND");
                            else
                                buf.append (" OR");
                            while (((n + 1) < tlen) && (targetExpression.charAt (n + 1) == ch))
                                ++n;
                        } else if (ch == '!') {
                            buf.append (" NOT");
                        } else if ("0123456789".indexOf (ch) != -1) {
                            int newn = n;
                            long    tid = 0;
                            int pos;
                            Target  temp;

                            while ((n < tlen) && ((pos = "0123456789".indexOf (ch)) != -1)) {
                                newn = n;
                                tid *= 10;
                                tid += pos;
                                ++n;
                                if (n < tlen)
                                    ch = targetExpression.charAt (n);
                                else
                                    ch = '\0';
                            }
                            n = newn;
                            temp = getTarget (tid);
                            if ((temp != null) && temp.valid ())
                                buf.append (" (" + temp.sql + ")");
                        }
                    }
                    if (buf.length () >= 3)
                        subselect = buf.toString ();
                }
                retreiveMediaInformation ();
            }
            if (subselect == null) {
                if (isRuleMailing ()) {
                    try {
                        dbase.update ("DELETE FROM maildrop_status_tbl WHERE status_id = :statusID", "statusID", maildrop_status_id);
                    } catch (SQLException e) {
                        logging (Log.ERROR, "init", "Failed to disable rule based mailing: " + e.toString ());
                    }
                    throw new Exception ("Missing target: Rule based mailing generation aborted and disabled");
                } else if (isOnDemandMailing ()) {
                    try {
                        setGenerationStatus (0, 4);
                    } catch (Exception e) {
                        logging (Log.ERROR, "init", "Failed to set genreation status: " + e.toString ());
                    }
                    throw new Exception ("Missing target: On Demand mailing generation aborted and left in undefined condition");
                }
            }
            if ((encoding == null) || (encoding.length () == 0))
                encoding = defaultEncoding;
            if ((charset == null) || (charset.length () == 0))
                charset = defaultCharset;
            //
            // get all possible URLs that should be replaced
            List <Map <String, Object>> rc;

            URLlist = new Vector <URL> ();
            if (mailing_id > 0) {
                rc = dbase.query ("SELECT url_id, full_url, " + dbase.measureType + extraURLTableColumns () + " FROM rdir_url_tbl " +
                          "WHERE company_id = :companyID AND mailing_id = :mailingID" + extraURLTableClause (),
                          "companyID", company_id,
                          "mailingID", mailing_id);
                for (int n = 0; n < rc.size (); ++n) {
                    Map <String, Object>    row = rc.get (n);
                    long            id = dbase.asLong (row.get ("url_id"));
                    String          dest = dbase.asString (row.get ("full_url"));
                    long            usage = dbase.asLong (row.get (dbase.measureRepr));

                    if (usage != 0) {
                        URL url = new URL (id, dest, usage);

                        extraURLTableGetColumns (url, row);
                        URLlist.addElement (url);
                    }
                }
            }
            urlcount = URLlist.size ();
            getURLDetails ();

            //
            // and now try to determinate the layout of the
            // customer table
            getTableLayout ("customer_" + company_id + "_tbl", null);

            Hashtable <String, Column>  cmap = new Hashtable <String, Column> ();

            for (int n = 0; n < lcount; ++n) {
                Column  c = layout.get (n);

                cmap.put (c.name.toLowerCase (), c);
            }

            rc = dbase.query ("SELECT col_name, shortname FROM customer_field_tbl WHERE company_id = :companyID", "companyID", company_id);
            for (int n = 0; n < rc.size (); ++n) {
                Map <String, Object>    row = rc.get (n);
                String          column = dbase.asString (row.get ("col_name"));

                if (column != null) {
                    Column  c = cmap.get (column);

                    if (c != null) {
                        c.setAlias (dbase.asString (row.get ("shortname")));
                    }
                }
            }

            retreiveCompanyInfo ();
            if (rdirDomain != null) {
                if (profileURL == null)
                    profileURL = rdirDomain + profileTag;
                if (unsubscribeURL == null)
                    unsubscribeURL = rdirDomain + unsubscribeTag;
                if (autoURL == null)
                    autoURL = rdirDomain + autoTag;
                if (onePixelURL == null)
                    onePixelURL = rdirDomain + onePixelTag;
            }
        } catch (Exception e) {
            logging (Log.ERROR, "init", "Error in quering initial data: " + e);
            throw new Exception ("Database error/initial query: " + e, e);
        }
    }

    public Locale getLocale (String language, String country) {
        if (language == null) {
            return null;
        }
        if (country == null) {
            return new Locale (language);
        }
        return new Locale (language, country);
    }

    public Title getTitle (Long tid) {
        if (titles == null) {
            titles = new Hashtable <Long, Title> ();

            try {
                List <Map <String, Object>> rc;

                rc = dbase.query ("SELECT title_id, title, gender FROM title_gender_tbl " +
                          "WHERE title_id IN (SELECT title_id FROM title_tbl WHERE company_id = :companyID OR company_id = 0 OR company_id IS null)",
                          "companyID", company_id);
                for (int n = 0; n < rc.size (); ++n) {
                    Map <String, Object>    row = rc.get (n);
                    Long            id = dbase.asLong (row.get ("title_id"));
                    String          title = dbase.asString (row.get ("title"));
                    int         gender = dbase.asInt (row.get ("gender"));
                    Title           cur = null;

                    if ((cur = titles.get (id)) == null) {
                        cur = new Title (id);
                        titles.put (id, cur);
                    }
                    cur.setTitle (gender, title);
                }
            } catch (Exception e1) {
                logging (Log.ERROR, "title", "Failed to query titles: " + e1.toString ());
            }
        }
        return titles.get (tid);
    }

    public String[] getTag (String tagName) {
        String[]    rc = null;

        if (tagCache == null) {
            tagCache = new Hashtable <String, String[]> ();
        } else {
            rc = tagCache.get (tagName);
        }
        if (rc == null) {
            String              query;
            HashMap <String, Object>    input;
            SimpleJdbcTemplate      jdbc;

            rc = new String[2];
            query = "SELECT selectvalue, type FROM tag_tbl WHERE tagname = :tagname AND (company_id = :companyID OR company_id = 0) ORDER BY company_id DESC";
            input = new HashMap <String, Object> (2);
            input.put ("tagname", tagName);
            input.put ("companyID", company_id);
            jdbc = null;
            try {
                List <Map <String, Object>> rq;

                jdbc = dbase.request (query);
                rq = jdbc.queryForList (query, input);
                if (rq.size () > 0) {
                    Map <String, Object>    row = rq.get (0);

                    rc[0] = dbase.asString (row.get ("selectvalue"));
                    rc[1] = dbase.asString (row.get ("type"));
                }
                tagCache.put (tagName, rc);
            } catch (Exception e) {
                logging (Log.WARNING, "gettag", "Failed to retreive tag \"" + tagName + "\" using \"" + query + "\": " + e.toString ());
            } finally {
                if (jdbc != null) {
                    dbase.release (jdbc);
                }
            }
        }
        return rc;
    }

    /**
     * Fill already sent recipient in seen hashset for
     * recovery prupose
     * @param seen the hashset to fill with seen customerIDs
     */
    public void prefillRecipients (HashSet <Long>  seen) throws Exception {
        if (isWorldMailing () || isRuleMailing () || isOnDemandMailing ()) {
            File    recovery = new File (metaDir, "recover-" + maildrop_status_id + ".list");

            if (recovery.exists ()) {
                logging (Log.INFO, "recover", "Found recovery file " + recovery.getAbsolutePath ());
                markToRemove (recovery.getAbsolutePath ());

                FileInputStream in = new FileInputStream (recovery);
                byte[]      content = new byte[(int) recovery.length ()];

                in.read (content);
                in.close ();
                String[]    data = (new String (content, "US-ASCII")).split ("\n");

                for (int n = 0; n < data.length; ++n) {
                    if (data[n].length () > 0) {
                        seen.add (Long.decode (data[n]));
                    }
                }
            }
        }
    }

    /**
     * Set the blocksize for generation doing some sanity checks
     * @param newBlockSize the new block size to use
     */
    public void setBlockSize (int newBlockSize) {
        blocksPerStep = 1;
        blockSize = newBlockSize;
    }

    /**
     * Set the stepping in minutes
     * @param stepping value
     */
    public void setStepping (int newStep) {
        step = newStep;
    }

    /** increase starting block
     */
    public void increaseStartblockForStepping () {
        ++startBlockForStep;
    }

    /**
     * Validate all set variables and make a sanity check
     * on the database to avoid double triggering of a
     * mailing
     */
    public void checkMailingData () throws Exception {
        int cnt;
        String  msg;

        cnt = 0;
        msg = "";
        if (isWorldMailing ())
            try {
                List <Map <String, Object>> rq;
                Map <String, Object>        row;
                long                nid;

                checkDatabase ();
                rq = dbase.query ("SELECT status_id FROM maildrop_status_tbl WHERE status_field = :statusField AND mailing_id = :mailingID ORDER BY status_id", "statusField", "W", "mailingID", mailing_id);
                if (rq.size () == 0) {
                    throw new Exception ("no entry at all for mailingID " + mailing_id + " found");
                }
                row = rq.get (0);
                nid = dbase.asLong (row.get ("status_id"));
                if (nid != maildrop_status_id) {
                    ++cnt;
                    msg += "\tlowest maildrop_status_id is not mine (" + maildrop_status_id + ") but " + nid + "\n";
                    dbase.update ("DELETE FROM maildrop_status_tbl WHERE status_id = :statusID", "statusID", maildrop_status_id);
                }
            } catch (Exception e) {
                ++cnt;
                msg += "\tunable to requery my status_id: " + e.toString () + "\n";
            }
        if ((! isPreviewMailing ()) && (maildrop_status_id < 0)) {
            ++cnt;
            msg += "\tmaildrop_status_id is less than 1 (" + maildrop_status_id + ")\n";
        }
        if (company_id <= 0) {
            ++cnt;
            msg += "\tcompany_id is less than 1 (" + company_id + ")\n";
        }
        if (mailinglist_id <= 0) {
            ++cnt;
            msg += "\tmailinglist_id is less than 1 (" + mailinglist_id + ")\n";
        }
        if (mailing_id < 0) {
            ++cnt;
            msg += "\tmailing_id is less than 0 (" + mailing_id + ")\n";
        }
        if ((! isAdminMailing ()) &&
            (! isTestMailing ()) &&
            (! isCampaignMailing ()) &&
            (! isRuleMailing ()) &&
            (! isOnDemandMailing ()) &&
            (! isWorldMailing ()) &&
            (! isPreviewMailing ())) {
            ++cnt;
            msg += "\tstatus_field must be one of A, V, T, E, R, D, W or P (" + status_field + ")\n";
        }

        long    now = System.currentTimeMillis () / 1000;
        if (sendtimestamp != null)
            sendSeconds = sendtimestamp.getTime () / 1000;
        else if ((senddate != null) && (sendtime != null))
            sendSeconds = (senddate.getTime () + sendtime.getTime ()) / 1000;
        else
            sendSeconds = now;
        if (sendSeconds < now)
            currentSendDate = new java.util.Date (now * 1000);
        else
            currentSendDate = new java.util.Date (sendSeconds * 1000);
        if (step < 0) {
            ++cnt;
            msg += "\tstep is less than 0 (" + step + ")\n";
        }
        if ((encoding == null) || (encoding.length () == 0)) {
            ++cnt;
            msg += "\tmissing or empty encoding\n";
        }
        if ((charset == null) || (charset.length () == 0)) {
            ++cnt;
            msg += "\tmissing or empty charset\n";
        }
        if ((profileURL == null) || (profileURL.length () == 0)) {
            ++cnt;
            msg += "\tmissing or empty profile_url\n";
        }
        if ((unsubscribeURL == null) || (unsubscribeURL.length () == 0)) {
            ++cnt;
            msg += "\tmissing or empty unsubscribe_url\n";
        }
        if ((autoURL == null) || (autoURL.length () == 0)) {
            ++cnt;
            msg += "\tmissing or empty auto_url\n";
        }
        if ((onePixelURL == null) || (onePixelURL.length () == 0)) {
//          ++cnt;
            onePixelURL = "file://localhost/";
            msg += "\tmissing or empty onepixel_url\n";
        }
        if (lineLength < 0) {
            ++cnt;
            msg += "\tlinelength is less than zero\n";
        }
        if (cnt > 0) {
            logging (Log.ERROR, "init", "Error configuration report:\n" + msg);
            throw new Exception (msg);
        }
        if (msg.length () > 0)
            logging (Log.INFO, "init", "Configuration report:\n" + msg);
    }

    /** Setup logging interface
     * @param program to create the logging path
     * @param setprinter if we should also log to stdout
     */
    private void setupLogging (String program, boolean setprinter) {
        log = new Log (program, logLevel);
        if (setprinter)
            log.setPrinter (System.out);
    }

    /**
     * Write all settings to logfile
     */
    public  void logSettings () {
        logging (Log.DEBUG, "init", "Initial data valid");
        logging (Log.DEBUG, "init", "All set variables:");
        logging (Log.DEBUG, "init", "\tlogLevel = " + log.levelDescription () + " (" + log.level () + ")");
        logging (Log.DEBUG, "init", "\tmailDir = " + mailDir);
        logging (Log.DEBUG, "init", "\tdefaultEncoding = " + defaultEncoding);
        logging (Log.DEBUG, "init", "\tdefaultCharset = " + defaultCharset);
        logging (Log.DEBUG, "init", "\tdbDriver = " + dbDriver);
        logging (Log.DEBUG, "init", "\tdbLogin = " + dbLogin);
        logging (Log.DEBUG, "init", "\tdbPassword = ******");
        logging (Log.DEBUG, "init", "\tdbConnect = " + dbConnect);
        logging (Log.DEBUG, "init", "\tdbPoolsize = " + dbPoolsize);
        logging (Log.DEBUG, "init", "\tdbPoolgrow = " + dbPoolgrow);
        logging (Log.DEBUG, "init", "\tblockSize = " + blockSize);
        logging (Log.DEBUG, "init", "\tmetaDir = " + metaDir);
        logging (Log.DEBUG, "init", "\txmlBack = " + xmlBack);
        logging (Log.DEBUG, "init", "\txmlValidate = " + xmlValidate);
        logging (Log.DEBUG, "init", "\tsampleEmails = " + sampleEmails);
        logging (Log.DEBUG, "init", "\tmailLogNumber = " + mailLogNumber);
        logging (Log.DEBUG, "init", "\taccLogfile = " + accLogfile);
        logging (Log.DEBUG, "init", "\tbncLogfile = " + bncLogfile);
        logging (Log.DEBUG, "init", "\tdefaultUserStatus = " + defaultUserStatus);
        logging (Log.DEBUG, "init", "\tdbase = " + dbase);
        logging (Log.DEBUG, "init", "\tmaildrop_status_id = " + maildrop_status_id);
        logging (Log.DEBUG, "init", "\tcompany_id = " + company_id);
        if (company_name != null)
            logging (Log.DEBUG, "init", "\tcompany_name = " + company_name);
        if (mailtracking_table != null)
            logging (Log.DEBUG, "init", "\tmailtracking_table = " + mailtracking_table);
        logging (Log.DEBUG, "init", "\tmailinglist_id = " + mailinglist_id);
        logging (Log.DEBUG, "init", "\tmailing_id = " + mailing_id);
        logging (Log.DEBUG, "init", "\tstatus_field = " + status_field);
        logging (Log.DEBUG, "init", "\tsenddate = " + senddate);
        logging (Log.DEBUG, "init", "\tsendtime = " + sendtime);
        logging (Log.DEBUG, "init", "\tsendtimestamp = " + sendtimestamp);
        logging (Log.DEBUG, "init", "\tsendSeconds = " + sendSeconds);
        logging (Log.DEBUG, "init", "\tstep = " + step);
        logging (Log.DEBUG, "init", "\tblocksPerStep = " + blocksPerStep);
        logging (Log.DEBUG, "init", "\tsubselect = " + (subselect == null ? "*not set*" : subselect));
        logging (Log.DEBUG, "init", "\tmailing_name = " + (mailing_name == null ? "*not set*" : mailing_name));
        logging (Log.DEBUG, "init", "\tsubject = " + (subject == null ? "*not set*" : subject));
        logging (Log.DEBUG, "init", "\tfromEmail = " + (fromEmail == null ? "*not set*" : fromEmail.toString ()));
        logging (Log.DEBUG, "init", "\treplyTo = " + (replyTo == null ? "*not set*" : replyTo.toString ()));
        logging (Log.DEBUG, "init", "\tenvelopeFrom = " + (envelopeFrom == null ? "*not set*" : envelopeFrom.toString ()));
        logging (Log.DEBUG, "init", "\tencoding = " + encoding);
        logging (Log.DEBUG, "init", "\tcharset = " + charset);
        logging (Log.DEBUG, "init", "\tdomain = " + domain);
        logging (Log.DEBUG, "init", "\tboundary = " + boundary);
        if (eol.equals ("\r\n"))
            logging (Log.DEBUG, "init", "\teol = CRLF");
        else if (eol.equals ("\n"))
            logging (Log.DEBUG, "init", "\teol = LF");
        else
            logging (Log.DEBUG, "init", "\teol = unknown (" + eol.length () + ")");
        logging (Log.DEBUG, "init", "\tmailer = " + mailer);
        logging (Log.DEBUG, "init", "\tprofileURL = " + profileURL);
        logging (Log.DEBUG, "init", "\tunsubscribeURL = " + unsubscribeURL);
        logging (Log.DEBUG, "init", "\tautoURL = " + autoURL);
        logging (Log.DEBUG, "init", "\tonePixelURL = " + onePixelURL);
        logging (Log.DEBUG, "init", "\tmasterMailtype = " + masterMailtype);
        logging (Log.DEBUG, "init", "\tlineLength = " + lineLength);
        logging (Log.DEBUG, "init", "\tonepixlog = " + onepixlog);
        logging (Log.DEBUG, "init", "\tpassword = " + password);
        logging (Log.DEBUG, "init", "\trdirDomain = " + rdirDomain);
        logging (Log.DEBUG, "init", "\tmailloopDomain = " + mailloopDomain);
    }

    /*
     * the main configuration
     */
    public void configure () throws Exception {
        String  val;

        if ((val = cfg.cget ("LOGLEVEL")) != null) {
            try {
                logLevel = Log.matchLevel (val);
                if (log != null) {
                    log.level (logLevel);
                }
            } catch (NumberFormatException e) {
                throw new Exception ("Loglevel must be a known string or a numerical value, not " + val);
            }
        }
        mailDir = cfg.cget ("MAILDIR", mailDir);
        defaultEncoding = cfg.cget ("DEFAULT_ENCODING", defaultEncoding);
        defaultCharset = cfg.cget ("DEFAULT_CHARSET", defaultCharset);
        dbDriver = cfg.cget ("DB_DRIVER", dbDriver);
        dbLogin = cfg.cget ("DB_LOGIN", dbLogin);
        dbPassword = cfg.cget ("DB_PASSWORD", dbPassword);
        dbConnect = cfg.cget ("SQL_CONNECT", dbConnect);
        dbPoolsize = cfg.cget ("DB_POOLSIZE", dbPoolsize);
        dbPoolgrow = cfg.cget ("DB_POOLGROW", dbPoolgrow);
        blockSize = cfg.cget ("BLOCKSIZE", blockSize);
        metaDir = cfg.cget ("METADIR", metaDir);
        xmlBack = cfg.cget ("XMLBACK", xmlBack);
        xmlValidate = cfg.cget ("XMLVALIDATE", xmlValidate);
        if (((sampleEmails = cfg.cget ("SAMPLE_EMAILS", sampleEmails)) != null) &&
            ((sampleEmails.length () == 0) || sampleEmails.equals ("-"))) {
            sampleEmails = null;
        }
        domain = cfg.cget ("DOMAIN", domain);
        boundary = cfg.cget ("BOUNDARY", boundary);
        if ((val = cfg.cget ("EOL")) != null) {
            if (val.equalsIgnoreCase ("CRLF")) {
                eol = "\r\n";
            } else if (val.equalsIgnoreCase ("LF")) {
                eol = "\n";
            } else {
                throw new Exception ("EOL must be either CRLF or LF, not " + val);
            }
        }
        mailer = cfg.cget ("MAILER", mailer);
        mailLogNumber = cfg.cget ("MAIL_LOG_NUMBER", mailLogNumber);
        accLogfile = cfg.cget ("ACCOUNT_LOGFILE", accLogfile);
        bncLogfile = cfg.cget ("BOUNCE_LOGFILE", bncLogfile);
    }

    /*
     * Setup configuration
     * @param checkRsc first check for resource bundle
     */
    private void configuration (boolean checkRsc) throws Exception {
        boolean     done;

        cfg = new Config (log);
        done = false;
        if (checkRsc) {
            try {
                ResourceBundle  rsc;

                rsc = ResourceBundle.getBundle ("emm");
                if (rsc != null) {
                    done = cfg.loadConfig (rsc, "mailgun.ini");
                }
            } catch (Exception e) {
                ;
            }
        }
        if (! done) {
            cfg.loadConfig (INI_FILE);
        }
        configure ();
    }

    /**
     * Constructor for the class
     * @param program the name of the program (for logging setup)
     * @param status_id the status_id to read the mailing information from
     * @param option output option
     * @param conn optional opened database connection
     */
    public Data (String program, String status_id, String option) throws Exception {
        setupLogging (program, (option == null || !option.equals ("silent")));
        configuration (true);

        logging (Log.DEBUG, "init", "Data read from " + cfg.getSource () + " for " + status_id);
        setupDatabase ();
        logging (Log.DEBUG, "init", "Initial database connection established");
        try {
            queryMailingInformations (status_id);
        } catch (Exception e) {
            throw new Exception ("Database failure: " + e, e);
        }
        logging (Log.DEBUG, "init", "Initial data read from database");
        checkMailingData ();
        lid = "(" + company_id + "/" +
                mailinglist_id + "/" +
                mailing_id + "/" +
                maildrop_status_id + ")";
        if (islog (Log.DEBUG)) {
            logSettings ();
        }
    }

    /**
     * Constructor for non mailing based instances
     * @param program the program name for logging
     */
    public Data (String program) throws Exception {
        setupLogging (program, true);
        configuration (true);
        logging (Log.DEBUG, "init", "Starting up");
        setupDatabase ();
    }

    /**
     * Suspend call between setup and main execution
     * @param conn optional database connection
     */
    public void suspend () throws Exception {
        if (isCampaignMailing () || isPreviewMailing ())
            closeDatabase ();
    }

    /**
     * Resume before main execution
     * @param conn optional database connection
     */
    public void resume () throws Exception {
        if (isCampaignMailing () || isPreviewMailing ())
            if (dbase == null) {
                setupDatabase ();
            }
    }

    /**
     * Cleanup all open resources and write mailing status before
     */
    public void done () throws Exception {
        int cnt;
        String  msg;

        cnt = 0;
        msg = "";
        if (bigClause != null) {
            bigClause.done ();
            bigClause = null;
        }
        if (dbase != null) {
            logging (Log.DEBUG, "deinit", "Shuting down database connection");
            try {
                closeDatabase ();
            } catch (Exception e) {
                ++cnt;
                msg += "\t" + e + "\n";
            }
        }
        if (toRemove != null) {
            int fcnt = toRemove.size ();

            if (fcnt > 0) {
                logging (Log.DEBUG, "deinit", "Remove " + fcnt + " file" + Log.exts (fcnt) + " if existing");
                while (fcnt-- > 0) {
                    String  fname = toRemove.remove (0);
                    File    file = new File (fname);

                    if (file.exists ())
                        if (! file.delete ())
                            msg += "\trm " + fname + "\n";
                    file = null;
                }
            }
            toRemove = null;
        }
        if (cnt > 0)
            throw new Exception ("Unable to cleanup:\n" + msg);
        logging (Log.DEBUG, "deinit", "Cleanup done: " + msg);
    }

    /**
     * Sanity check for mismatch company_id and perhaps deleted
     * mailing
     */
    public void sanityCheck () throws Exception {
        if (! isPreviewMailing ()) {
            try {
                long            cid, del;
                Map <String, Object>    row;

                row = dbase.querys ("SELECT company_id, deleted FROM mailing_tbl WHERE mailing_id = :mailingID", "mailingID", mailing_id);
                cid = dbase.asLong (row.get ("company_id"));
                del = dbase.asLong (row.get ("deleted"));
                if (cid != company_id) {
                    throw new Exception ("Original companyID " + company_id + " for mailing " + mailing_id + " does not match current company_id " + cid);
                }
                if (del != 0) {
                    try {
                        setGenerationStatus (0, 4);
                    } catch (Exception e) {
                        logging (Log.ERROR, "sanity", "Failed to set generation status: " + e.toString ());
                    }
                    throw new Exception ("Mailing " + mailing_id + " marked as deleted");
                }
            } catch (Exception e) {
                logging (Log.ERROR, "sanity", "Error in quering mailing_tbl: " + e);
                throw new Exception ("Unable to find entry in mailing_tbl for " + mailing_id + ": " + e);
            }
        }
    }

    /**
     * Executed at start of mail generation
     */
    public void startExecution () throws Exception {
        bigClause = (BC) mkBigClause ();
        bigClause.setData (this);
        if (! bigClause.prepareClause ()) {
            throw new Exception ("Failed to setup main clause");
        }
        totalSubscribers = bigClause.subscriber ();
        totalReceivers = bigClause.receiver ();
        logging (Log.DEBUG, "start", "\ttotalSubscribers = " + totalSubscribers);
        logging (Log.DEBUG, "start", "\ttotalReceivers = " + totalReceivers);
    }

    /**
     * Executed at end of mail generation
     */
    public void endExecution () {
        if (bigClause != null) {
            bigClause.done ();
            bigClause = null;
        }
    }

    /**
     * Change generation state for the current mailing
     */
    public void updateGenerationState (int newstatus) {
        if (isAdminMailing () || isTestMailing () || isWorldMailing () || isRuleMailing () || isOnDemandMailing ()) {
            try {
                if (newstatus == 0) {
                    if (isRuleMailing () || isOnDemandMailing ())
                        newstatus = 1;
                    else
                        newstatus = 3;
                }
                setGenerationStatus (2, newstatus);
            } catch (Exception e) {
                logging (Log.ERROR, "genstate", "Unable to update generation state: " + e.toString ());
            }
        }
    }
    public void updateGenerationState () {
        updateGenerationState (0);
    }

    /**
     * Called when main generation starts
     */
    public Vector <String> generationClauses () {
        return bigClause.createClauses ();
    }

    /**
     * Save receivers to mailtracking table
     */
    public void toMailtrack () {
        if (mailtracking_table != null) {
            String  query = bigClause.mailtrackStatement (mailtracking_table);

            if (query != null)
                try {
                    dbase.update (query);
                } catch (Exception e) {
                    logging (Log.ERROR, "execute", "Unable to add mailtrack information using \"" + query + "\": " + e.toString ());
                }
        }
    }

    /**
     * Convert a given object to an integer
     * @param o the input object
     * @param what for logging purpose
     * @return the converted value
     */
    private int obj2int (Object o, String what) throws Exception {
        int rc;

        if (o.getClass () == new Integer (0).getClass ())
            rc = ((Integer) o).intValue ();
        else if (o.getClass () == new Long (0L).getClass ())
            rc = ((Long) o).intValue ();
        else if (o.getClass () == new String ().getClass ())
            rc = Integer.parseInt ((String) o);
        else
            throw new Exception ("Unknown data type for " + what);
        return rc;
    }

    /**
     * Convert a given object to a long
     * @param o the input object
     * @param what for logging purpose
     * @return the converted value
     */
    private long obj2long (Object o, String what) throws Exception {
        long    rc;

        if (o.getClass () == new Integer (0).getClass ())
            rc = ((Integer) o).longValue ();
        else if (o.getClass () == new Long (0L).getClass ())
            rc = ((Long) o).longValue ();
        else if (o.getClass () == new String ().getClass ())
            rc = Long.parseLong ((String) o);
        else
            throw new Exception ("Unknown data type for " + what);
        return rc;
    }

    /**
     * Convert a given object to a boolean
     * @param o the input object
     * @param what for logging purpose
     * @return the converted value
     */
    private boolean obj2bool (Object o, String what) throws Exception {
        boolean rc;

        if (o.getClass () == new Boolean (false).getClass ())
            rc = ((Boolean) o).booleanValue ();
        else
            throw new Exception ("Unknown data type for " + what);
        return rc;
    }

    /**
     * Convert a given object to a date
     * @param o the input object
     * @param what for logging purpose
     * @return the converted value
     */
    private java.util.Date obj2date (Object o, String what) throws Exception {
        java.util.Date  rc;

        if (o.getClass () == new java.util.Date ().getClass ())
            rc = (java.util.Date) o;
        else
            throw new Exception ("Unknown data type for " + what);
        return rc;
    }

    /**
     * Parse options passed during runtime
     * @param opts the options to use
     * @param state if 1, the before initialization pass, 2 on execution pass
     */
    @SuppressWarnings ("unchecked")
    public void options (Hashtable <String, Object> opts, int state) throws Exception {
        Object tmp;

        if (opts == null) {
            return;
        }
        if (state == 1) {
            tmp = opts.get ("custom-tags");
            if (tmp != null) {
                if (customTags == null)
                    customTags = new Vector <String> ();
                for (Enumeration<String> e = ((Hashtable<String, Object>) tmp).keys (); e.hasMoreElements (); ) {
                    String s = e.nextElement ();

                    if (s != null)
                        customTags.add (s);
                }
            }
            previewInput = (String) opts.get ("preview-input");
            tmp = opts.get ("preview-create-all");
            if (tmp != null)
                previewCreateAll = obj2bool (tmp, "preview-create-all");
        } else if (state == 2) {
            tmp = opts.get ("customer-id");
            if (tmp != null)
                campaignCustomerID = obj2long (tmp, "customer-id");
            tmp = opts.get ("transaction-id");
            if (tmp != null)
                campaignTransactionID = obj2long (tmp, "transaction-id");
            tmp = opts.get ("user-status");
            if (tmp != null)
                campaignUserStatus = obj2int (tmp, "user-status");
            fixedEmail = (String) opts.get ("fixed-email");
            tmp = opts.get ("preview-for");
            if (tmp != null)
                previewCustomerID = obj2long (tmp, "preview-for");
            previewOutput = (Page) opts.get ("preview-output");
            tmp = opts.get ("preview-anon");
            if (tmp != null)
                previewAnon = obj2bool (tmp, "preview-anon");
            previewSelector = (String) opts.get ("preview-selector");
            tmp = opts.get ("preview-cachable");
            if (tmp != null)
                previewCachable = obj2bool (tmp, "preview-cachable");
            tmp = opts.get ("preview-convert-entities");
            if (tmp != null)
                previewConvertEntities = obj2bool (tmp, "preview-convert-entities");
            tmp = opts.get ("preview-legacy-uids");
            if (tmp != null)
                previewLegacyUIDs = obj2bool (tmp, "preview-legacy-uids");
            tmp = opts.get ("send-date");
            if (tmp != null) {
                currentSendDate = obj2date (tmp, "send-date");
                sendSeconds = currentSendDate.getTime () / 1000;

                long    now = System.currentTimeMillis () / 1000;
                if (sendSeconds < now)
                    sendSeconds = now;
            }

            tmp = opts.get ("step");
            if (tmp != null)
                setStepping (obj2int (tmp, "step"));
            tmp = opts.get ("block-size");
            if (tmp != null)
                setBlockSize (obj2int (tmp, "block-size"));

            campaignSubselect = (TargetRepresentation) opts.get ("select");

            customMap = (Hashtable <String, String>) opts.get ("custom-tags");
            overwriteMap = (Hashtable <String, String>) opts.get ("overwrite");
            virtualMap = (Hashtable <String, String>) opts.get ("virtual");
            overwriteMapMulti = (Hashtable <Long, Hashtable <String, String>>) opts.get ("overwrite-multi");
            virtualMapMulti = (Hashtable <Long, Hashtable <String, String>>) opts.get ("virtual-multi");
        }
    }

    /**
     * Should we use this record, according to our virtual data?
     * @return true if we should
     */
    public boolean useRecord (Long cid) {
        return true;
    }

    /**
     * Optional initialization for virtual data
     * @param column the column to initialize
     */
    public void initializeVirtualData (String column) {
    }

    /**
     * Do we have data available to overwrite columns?
     * @return true in this case
     */
    public boolean overwriteData () {
        return (overwriteMap != null) || (overwriteMapMulti != null);
    }

    /**
     * Find entry in map for overwrite/virtual records
     * @param cid the customer id
     * @param multi optional available multi hash table
     * @param simple optional simple hash table
     * @param colname the name of the column
     * @return the found string or null
     */
    private String findInMap (Long cid, Hashtable <Long, Hashtable <String, String>> multi, Hashtable <String, String> simple, String colname) {
        Hashtable <String, String>   map;

        if ((multi != null) && multi.containsKey (cid))
            map = multi.get (cid);
        else
            map = simple;
        if ((map != null) && map.containsKey (colname))
            return map.get (colname);
        return null;
    }

    /**
     * Find an overwrite column
     * @param cid the customer id
     * @param colname the name of the column
     * @return the found string or null
     */
    public String overwriteData (Long cid, String colname) {
        return findInMap (cid, overwriteMapMulti, overwriteMap, colname);
    }

    /**
     * Find a virtual column
     * @param cid the customer id
     * @param colname the name of the column
     * @return the found string or null
     */
    public String virtualData (Long cid, String colname) {
        return findInMap (cid, virtualMapMulti, virtualMap, colname);
    }

    /**
     * Get envelope address
     * @return the punycoded envelope address
     */
    public String getEnvelopeFrom () {
        return (envelopeFrom != null && envelopeFrom.pure_puny != null) ? envelopeFrom.pure_puny : (fromEmail != null ? fromEmail.pure_puny : null);
    }

    /**
     * If we have another subselection during runtime
     * return it from here
     * @return the extra subselect or null
     */
    public String getCampaignSubselect () {
        String  rc = null;
        String  sql;

        if (campaignSubselect != null) {
            sql = campaignSubselect.generateSQL ();
            if ((sql != null) && (sql.length () > 0))
                rc = sql;
        }
        return rc;
    }

    /** If we have further restrictions due to reference mailing
     * @return extra subsulect or null
     */
    public String getReferenceSubselect () {
        return null;
    }

    /** If we have further restrictions due to selected media
     * @return extra subsulect or null
     */
    public String getMediaSubselect () {
        return null;
    }

    /** Returns a default image link for a generic picture
     * @param name the image name
     * @return the created link
     */
    public String defaultImageLink (String name) {
        return rdirDomain + "/image?ci=" + Long.toString (company_id) + "&mi=" + Long.toString (mailing_id) + "&name=" + name;
    }

    /**
     * Mark a filename to be removed during cleanup phase
     * @param fname the filename
     */
    public void markToRemove (String fname) {
        if (toRemove == null)
            toRemove = new Vector <String> ();
        if (! toRemove.contains (fname))
            toRemove.addElement (fname);
    }

    /**
     * Mark a file to be removed during cleanup
     * @param file a File instance for the file to be removed
     */
    public void markToRemove (File file) {
        markToRemove (file.getAbsolutePath ());
    }

    /**
     * Unmark a filename to be removed, if we already removed
     * it by hand
     * @param fname the filename
     */
    public void unmarkToRemove (String fname) {
        if ((toRemove != null) && toRemove.contains (fname))
            toRemove.remove (fname);
    }

    /**
     * Unmark a file to be removed
     * @param file a File instance
     */
    public void unmarkToRemove (File file) {
        unmarkToRemove (file.getAbsolutePath ());
    }

    /**
     * Check if we have to write logging for a given loglevel
     * @param loglvl the loglevel to check against
     * @return true if we should log
     */
    public boolean islog (int loglvl) {
        return log.islog (loglvl);
    }

    /**
     * Write entry to logfile
     * @param loglvl the level to report
     * @param mid the ID of the message
     * @param msg the message itself
     */
    public void logging (int loglvl, String mid, String msg) {
        if (lid != null)
            if (mid != null)
                mid = mid + "/" + lid;
            else
                mid = lid;
        if (log != null)
            log.out (loglvl, mid, msg);
    }

    /**
     * Create a path to write test-/admin mails to
     * @return the path to use
     */
    public String mailDir () {
        return mailDir;
    }

    private void iniValidate (String value, String name) throws Exception {
        if (value == null) {
            throw new Exception ("mailgun.ini." + name + " is unset");
        }
    }
    public String dbDriver () throws Exception {
        iniValidate (dbDriver, "db_driver");
        return dbDriver;
    }

    /** returns the database login
     * @return login string
     */
    public String dbLogin () throws Exception {
        iniValidate (dbLogin, "db_login");
        return dbLogin;
    }

    /** returns the database password
     * @return password string
     */
    public String dbPassword () throws Exception {
        iniValidate (dbPassword, "db_password");
        return dbPassword;
    }

    /** returns the connection string for the database
     * @return connection string
     */
    public String dbConnect () throws Exception {
        iniValidate (dbConnect, "db_connect");
        return dbConnect;
    }

    public int dbPoolsize () {
        return dbPoolsize;
    }

    public boolean dbPoolgrow () {
        return dbPoolgrow;
    }

    /** returns the block size to be used
     * @return block size
     */
    public int blockSize () {
        return blockSize;
    }

    /** returns the directory to write meta files to
     * @return path to meta
     */
    public String metaDir () throws Exception {
        iniValidate (metaDir, "metadir");
        return metaDir;
    }

    /** returns the path to xmlback program
     * @return path to xmlback
     */
    public String xmlBack () {
        return xmlBack;
    }

    /** returns the path to the acounting logfile
     * @return path to logfile
     */
    public String accLogfile () {
        return accLogfile;
    }

    /** returns the path to the bounce logfile
     * @return path to logfile
     */
    public String bncLogfile () {
        return bncLogfile;
    }

    /** returns wether we should validate generated XML files
     * @return true if validation should take place
     */
    public boolean xmlValidate () {
        return xmlValidate;
    }

    /** returns the optional used sample receivers
     * @return receiver list
     */
    public String sampleEmails () {
        return sampleEmails;
    }

    /** returns the number of generate mails to write log entries for
     * @return number of mails
     */
    public int mailLogNumber () {
        return mailLogNumber;
    }

    /** returns the X-Mailer: header content
     * @return mailer name
     */
    public String makeMailer () {
        if ((mailer != null) && (company_name != null))
            return StringOps.replace (mailer, "[agnMANDANT]", company_name);
        return mailer;
    }

    /** if this is a admin mail
     * @return true, if admin mail
     */
    public boolean isAdminMailing () {
        return status_field.equals ("A");
    }

    /** if this is a test mail
     * @return true, if test mail
     */
    public boolean isTestMailing () {
        return status_field.equals ("T");
    }

    /** if this is a campaign mail
     * @return true, if campaign mail
     */
    public boolean isCampaignMailing () {
        return status_field.equals ("E");
    }

    /** if this is a date based mailing
     * @return true, if its date based
     */
    public boolean isRuleMailing ()
    {
        return status_field.equals ("R");
    }

    /** if this an on demand mailing
     * @return true, if this is on demand
     */
    public boolean isOnDemandMailing ()
    {
        return status_field.equals ("D");
    }


    /** if this is a world mail
     * @return true, if world mail
     */
    public boolean isWorldMailing () {
        return status_field.equals ("W");
    }

    /** if this is a preview
     * @return true, if preview
     */
    public boolean isPreviewMailing () {
        return status_field.equals ("P");
    }

    /**
     * Set standard field to be retreived from database
     * @param predef the hashset to store field name to
     */
    public void setStandardFields (HashSet <String> predef, Hashtable<String, EMMTag> tags) {
        predef.add ("email");
        for (Enumeration<EMMTag> e = tags.elements(); e.hasMoreElements(); ) {
            EMMTag tag = e.nextElement ();

            try {
                tag.requestFields (this, predef);
            } catch (Exception ex) {
                logging (Log.ERROR, "tag", "Failed to get required fields for tag " + tag.mTagFullname + ": " + ex.toString ());
            }
        }
    }

    /**
     * Set standard columns, if they are not already found in database
     * @param use already used column names
     */
    public void setUsedFieldsInLayout (HashSet <String> use, Hashtable<String, EMMTag> tags) {
        int sanity = 0;
        HashSet <String>
            predef;

        if (use != null) {
            predef = new HashSet <String> (use);
        } else {
            predef = new HashSet <String> ();
        }
        if (targets != null) {
            for (Enumeration <Target> e = targets.elements (); e.hasMoreElements (); ) {
                Target  t = e.nextElement ();

                t.requestFields (this, predef);
            }
        }
        setStandardFields (predef, tags);
        for (int n = 0; n < lcount; ++n) {
            Column  c = layout.elementAt (n);

            if (predef.contains (c.qname)) {
                if (! c.inuse) {
                    c.inuse = true;
                    ++lusecount;
                }
                ++sanity;
            } else {
                if (c.inuse) {
                    c.inuse = false;
                    --lusecount;
                }
            }
        }
        if (sanity != lusecount)
            logging (Log.ERROR, "layout", "Sanity check failed in setUsedFieldsInLayout");
    }

    /** find a column by its alias
     * @param alias
     * @return the column on success, null otherwise
     */
    public Column columnByAlias (String alias) {
        for (int n = 0; n < lcount; ++n) {
            Column  c = layout.elementAt (n);

            if ((c.alias != null) && c.alias.equalsIgnoreCase (alias))
                return c;
        }
        return null;
    }

    /** find a column by its name
     * @param name
     * @return the column on success, null otherwise
     */
    public Column columnByName (String name, String ref) {
        for (int n = 0; n < lcount; ++n) {
            Column  c = layout.elementAt (n);

            if (c.name.equalsIgnoreCase (name) &&
                (((c.ref == null) && (ref == null)) || ((c.ref != null) && (ref != null) && (c.ref.equalsIgnoreCase (ref)))))
                return c;
        }
        return null;
    }
    public Column columnByName (String name) {
        return columnByName (name, null);
    }
    public Column columnByIndex (int idx) {
        return (idx >= 0) && (idx < lcount) ? layout.elementAt (idx) : null;
    }

    /** return the name of the column at a given position
     * @param col the position in the column layout
     * @return the column name
     */
    public String columnName (int col) {
        return layout.elementAt (col).name;
    }

    /** return the type of the column at a given position
     * @param col the position in the column layout
     * @return the column type
     */
    public int columnType (int col) {
        return layout.elementAt (col).type;
    }

    /** return the type as string of the column at a given position
     * @param col the position in the column layout
     * @return the column type as string
     */
    public String columnTypeStr (int col) {
        return layout.elementAt (col).typeStr ();
    }

    /** Set a column from a result set
     * @param col the position in the column layout
     * @param rset the result set
     * @param index position in the result set
     */
    public void columnSet (int col, ResultSet rset, int index) {
        layout.elementAt (col).set (rset, index);
    }

    /** Get a value from a column
     * @param col the position in the column layout
     * @return the contents of that column
     */
    public String columnGetStr (int col) {
        return layout.elementAt (col).get ();
    }

    /** Check wether a columns value is NULL
     * @param col the position in the column layout
     * @return true of column value is NULL
     */
    public boolean columnIsNull (int col) {
        return layout.elementAt (col).isNull ();
    }

    /** Check wether a column is in use
     * @param col the position in the column layout
     * @return true if column is in use
     */
    public boolean columnUse (int col) {
        return layout.elementAt (col).inUse ();
    }

    /** create a RFC compatible Date: line
     * @param ts the input time
     * @return the RFC representation
     */
    public String RFCDate (java.util.Date ts) {
        SimpleDateFormat    fmt = new SimpleDateFormat ("EEE, d MMM yyyy HH:mm:ss z",
                                    new Locale ("en"));
        fmt.setTimeZone (TimeZone.getTimeZone ("GMT"));
        if (ts == null)
            ts = new java.util.Date ();
        return fmt.format (ts);
    }

    /** Optional string to add to filename generation
     * @return optional string
     */
    public String getFilenameDetail () {
        return "";
    }

    public String getFilenameCompanyID () {
        return Long.toString (company_id);
    }

    public String getFilenameMailingID () {
        return Long.toString (mailing_id);
    }

    /** Should we generate URLs already here?
     * @return true, if we should generate them
     */
    public boolean generateCodedURLs () {
        return true;
    }
}
