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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.dao.DataAccessException;

import org.agnitas.util.Const;
import org.agnitas.util.Blackdata;
import org.agnitas.util.Blacklist;
import org.agnitas.util.Log;

/** Central control class for generating mails
 */
public class MailgunImpl implements Mailgun {
    /** Reference to configuration */
    private Data        data;
    /** All content blocks */
    private BlockCollection allBlocks = null;
    /** All tags for this mailing */
    private Hashtable <String, EMMTag>
                tagNames = null;
    /** Creator for all URLs */
    private URLMaker    urlMaker = null;
    /** The blacklist information for this mailing */
    public Blacklist    blist = null;
    /** Query for normal selection */
    private String      selectQuery = null;
    /** Query for the world part selection */
    private String      wSelectQuery = null;

    /** Constructor
     * must be followed by initializeMailung ()
     */
    public MailgunImpl () {
        data = null;
    }

    /** Setter for data
     * @param nData new data instance
     */
    public void setData (Data nData) {
        data = nData;
    }

    /** Allocate new data instance
     * @param status_id the string version of the statusID to use
     */
    protected void mkData (String status_id) throws Exception {
        setData (new Data ("mailgun", status_id, "meta:xml/gz"));
    }

    /**
     * Initialize internal data
     * @param status_id the string version of the statusID to use
     */
    public void initializeMailgun (String status_id) throws Exception {
        data = null;
        try {
            mkData (status_id);
        } catch (Exception e) {
            done ();
            throw new Exception ("Error reading ini file: " + e, e);
        }
        data.suspend ();
    }

    /** Setup a mailgun without starting generation
     * @param opts options to control the setup beyond DB information
     */
    public void prepareMailgun (Hashtable <String, Object> opts) throws Exception {
        try {
            doPrepare (opts);
        } catch (Exception e) {
            if (data != null) {
                data.suspend ();
            }
            throw e;
        }
    }

    /** Execute an already setup mailgun
     * @param opts options to control the execution beyond DB information
     */
    public synchronized void
    executeMailgun (Hashtable <String, Object> opts) throws Exception {
        try {
            doExecute (opts);
        } catch (Exception e) {
            data.suspend ();
            throw e;
        }
    }

    /** retreive the current mailing id
     * @return the mailing ID
     */
    public long mailingID () {
        return data.mailing_id;
    }

    /** Cleanup
     */
    public void done () throws Exception {
        if (data != null) {
            try {
                data.done ();
            } catch (Exception e) {
                data = null;
                throw new Exception ("Failed in cleanup: " + e);
            }
            data = null;
        }
    }

    /** Retreive blacklist from database
     */
    public void retreiveBlacklist () throws Exception {
        String      query = "SELECT company_id, email FROM cust_ban_tbl WHERE company_id = 0 OR company_id = :companyID";
        List <Map <String, Object>>
                rq = data.dbase.query (query, "companyID", data.company_id);

        for (int n = 0; n < rq.size (); ++n) {
            Map <String, Object>    row = rq.get (n);
            int         cid = data.dbase.asInt (row.get ("company_id"));
            String          email = data.dbase.asString (row.get ("email"));

            if (email != null) {
                blist.add (email, cid == 0);
            }
        }
    }

    protected Object mkBlacklist () {
        return new Blacklist ();
    }

    /** Read in the global and local blacklist
     */
    private void readBlacklist () throws Exception {
        blist = (Blacklist) mkBlacklist ();
        if (! data.isPreviewMailing ()) {
            try {
                retreiveBlacklist ();
            } catch (Exception e) {
                data.logging (Log.FATAL, "readblist", "Unable to get blacklist: " + e.toString ());
                throw new Exception ("Unable to get blacklist: " + e.toString ());
            }

            data.logging (Log.INFO, "readblist", "Found " + blist.globalCount () + " entr" + Log.exty (blist.globalCount ()) + " in global blacklist, " +
                                          blist.localCount () + " entr" + Log.exty (blist.localCount ()) + " in local blacklist");
        }
    }

    /** Check the sample email receiver if they should receive
     * the sample for this mailing
     * @param inp the expression to validate
     * @return null, if no mail should be sent, otherwise the email address
     */
    private String validateSampleEmail (String inp) {
        String  email;
        int n = inp.indexOf (':');

        if (n != -1) {
            String  minsub;

            minsub = inp.substring (0, n);
            try {
                long    minsubscriber = Long.parseLong (minsub);

                if (minsubscriber < data.totalSubscribers) {
                    email = inp.substring (n + 1);
                } else {
                    email = null;
                }
            } catch (NumberFormatException e) {
                email = null;
            }
        } else {
            email = inp;
        }
        return email;
    }

    protected Object mkBlockCollection () throws Exception {
        return new BlockCollection ();
    }

    protected Object mkURLMaker () throws Exception {
        return new URLMaker (data);
    }

    /** Prepare the mailgun
     * @param opts options to control the setup beyond DB information
     */
    private void doPrepare (Hashtable <String, Object> opts) throws Exception {
        data.resume ();
        data.options (opts, 1);

        data.logging (Log.DEBUG, "prepare", "Starting firing");
        // create new Block collection and store in member var
        allBlocks = (BlockCollection) mkBlockCollection ();
        allBlocks.setupBlockCollection (data, data.previewInput);

        data.logging (Log.DEBUG, "prepare", "Parse blocks");
        // read all tag names contained in the blocks into Hashtable
        // - read selectvalues and store in EMMTag associated with tag name in Hashtable
        tagNames = allBlocks.parseBlocks();
        data.setUsedFieldsInLayout (allBlocks.conditionFields, tagNames);

        // add default tags to Hastable
        try{
            String[]    preset = {
                "agnMAILTYPE",
                "agnONEPIXEL"
            };
            for (int n = 0; n < preset.length; ++n) {
                boolean useit;

                switch (n) {
                default:
                    useit = true;
                    break;
                case 1:
                    useit = data.onepixlog != Data.OPL_NONE;
                    break;
                }
                if (useit) {
                    String  tn = "[" + preset[n] + "]";

                    if (! tagNames.containsKey (tn))
                        tagNames.put (tn, (EMMTag) allBlocks.mkEMMTag (tn, false));
                }
            }
        } catch (Exception e){
            data.suspend ();
            throw new Exception("Error adding default tags: " + e);
        }
        allBlocks.replace_fixed_tags (tagNames);

        // prepare special url string maker
        urlMaker = (URLMaker) mkURLMaker ();
        readBlacklist ();

        data.suspend ();
    }

    /** Prepare collection of customers
     * @return a hashset for already seen customers
     */
    public HashSet <Long> prepareCollection () throws Exception {
        return new HashSet <Long> ((int) data.totalReceivers + 1);
    }

    /** Get new instance for index collection
     * @return new instance
     */
    public Object mkIndices () {
        return new Indices ();
    }

    public Object mkCustinfo () {
        return new Custinfo ();
    }

    public Object mkMailWriterMeta (Object nData, Object allBlocks, Hashtable tagNames) throws Exception {
        return new MailWriterMeta ((Data) nData, (BlockCollection) allBlocks, tagNames);
    }

    /** Return used mediatypes (currently only email)
     * @param cid the customerID to get types for
     * @return mediatypes
     */
    public String getMediaTypes (long customerID) {
        return "email";
    }

    /** Write final data to database
     */
    public void finalizeMailingToDatabase (MailWriter mailer) throws Exception {
        data.toMailtrack ();
    }

    public void skipSetup () throws Exception {
    }
    public void skipFinalize () {
    }
    /** if we should not send mail to this recipient
     * @param customerID the customerID of the recipient
     * @return true, if we should not send mail, false otherwise
     */
    public boolean skipRecipient (long customerID) {
        return false;
    }

    class Extractor implements ResultSetExtractor {
        private Data            data;
        private Mailgun         mg;
        private EMMTag          mailtypeTag;
        private Blacklist       blist;
        private URLMaker        urlMaker;
        private MailWriter      mailer;
        private boolean         needSamples;
        private HashSet <Long>      seen;
        private boolean         hasOverwriteData;
        private boolean         hasVirtualData;
        private Vector <EMMTag>     emailTags;
        private int         emailCount;
        private ResultSetMetaData   meta;
        private int         metacount;
        private Column[]        rmap;
        private Indices         indices;
        private Custinfo        cinfo;

        public Extractor (Data nData, Mailgun nMg, EMMTag nMailtypeTag,
                  Blacklist nBlist, URLMaker nUrlMaker, MailWriter nMailer,
                  boolean nNeedSamples, HashSet <Long> nSeen,
                  boolean nHasOverwriteData, boolean nHasVirtualData,
                  Vector <EMMTag> nEmailTags, int nEmailCount) {
            data = nData;
            mg = nMg;
            mailtypeTag = nMailtypeTag;
            blist = nBlist;
            urlMaker = nUrlMaker;
            mailer = nMailer;
            needSamples = nNeedSamples;
            seen = nSeen;
            hasOverwriteData = nHasOverwriteData;
            hasVirtualData = nHasVirtualData;
            emailTags = nEmailTags;
            emailCount = nEmailCount;
            meta = null;
            metacount = 0;
            rmap = null;
            indices = (Indices) mg.mkIndices ();
            cinfo = (Custinfo) mg.mkCustinfo ();
        }

        private void extractRecord (ResultSet rset) throws SQLException, DataAccessException {
            if (meta == null) {
                meta = rset.getMetaData ();
                metacount = meta.getColumnCount ();
                rmap = new Column[metacount];
                cinfo.setup (data, urlMaker);
                for (int n = 0; n < metacount; ++n) {
                    String  cname = meta.getColumnName (n + 1);
                    int ctype = meta.getColumnType (n + 1);

                    if (Column.typeStr (ctype) != null) {
                        rmap[n] = new Column (cname, ctype);
                        cname = cname.toLowerCase ();
                        indices.checkIndex (cname, n);
                    } else
                        rmap[n] = null;
                }
            }

            long    cid = rset.getLong (1);
            Long    ocid = new Long (cid);

            if (seen.contains (ocid))
                return;
            seen.add (ocid);
            if (mg.skipRecipient (cid))
                return;
            if (hasVirtualData && (! data.useRecord (ocid)))
                return;

            String  userType = rset.getString (2);

            int offset = 1;
            int count;

            for (count = 0; count < 2; ++count) {
                rmap[count].set (rset, count + offset);
            }

            // get values from this recordset
            // store in Emmtag inside Hashtable
            // the tags are in the correct order
            //
            for (EMMTag tmpTag : tagNames.values ()) {
                if ((! tmpTag.globalValue) && (! tmpTag.fixedValue) && (! tmpTag.mutableValue) && (tmpTag.tagType == EMMTag.TAG_DBASE)) {
                    tmpTag.mTagValue = null;
                    if (rmap[count] != null) {
                        rmap[count].set (rset, count + offset);
                        if (! rmap[count].isNull ()) {
                            tmpTag.mTagValue = rmap[count].get ();
                        }
                    }
                    ++count;
                }
            } // end for

            for (int n = count; n < metacount; ++n)
                if (rmap[n] != null)
                    rmap[n].set (rset, n + offset);

            if (data.lusecount > 0) {
                int m;

                m = 0;
                for (int n = 0; n < data.lcount; ++n)
                    if (data.columnUse (n))
                        data.columnSet (n, rset, count + offset + m++);
            }

            if (hasOverwriteData || hasVirtualData) {
                for (EMMTag tmpTag : tagNames.values ()) {
                    if (hasOverwriteData && (tmpTag.tagType == EMMTag.TAG_INTERNAL) && (tmpTag.tagSpec == EMMTag.TI_DB)) {
                        tmpTag.dbOverwrite = data.overwriteData (ocid, tmpTag.mSelectString.toUpperCase ());
                    } else if (hasVirtualData && (tmpTag.tagType == EMMTag.TAG_INTERNAL) && (tmpTag.tagSpec == EMMTag.TI_DBV)) {
                        tmpTag.dbOverwrite = data.virtualData (ocid, tmpTag.mSelectString.toUpperCase ());
                    }
                }
            }

            String mailtype = (mailtypeTag != null ? mailtypeTag.mTagValue : null);
            int mtype;

            if (data.isPreviewMailing ()) {
                mtype = Const.Mailtype.HTML | Const.Mailtype.HTML_MOBILE;
                mailtype = Integer.toString (mtype);
            } else {
                if (mailtype == null) {
                    data.logging (Log.WARNING, "mailgun", "Unset mailtype for customer_id " + cid + ", using default");
                    mtype = Const.Mailtype.HTML;
                    mailtype = Integer.toString (mtype);
                } else {
                    mtype = Integer.parseInt (mailtype);
                    if (mtype == 2) {
                        mtype = Const.Mailtype.HTML | Const.Mailtype.HTML_OFFLINE;
                    }
                    mtype &= data.masterMailtype;
                }
            }

            cinfo.clear ();
            cinfo.setCustomerID (cid);
            cinfo.setUserType (userType);
            cinfo.setFromDatabase (rmap, indices);

            for (int n = 0; n < emailCount; ++n) {
                EMMTag  etag = emailTags.elementAt (n);

                etag.mTagValue = cinfo.email;
            }

            if (! data.isPreviewMailing ()) {
                boolean     isblisted = false;

                for (int blstate = 0; blstate < cinfo.checkForBlacklist; ++blstate) {
                    String  check = cinfo.blacklistValue (blstate);
                    String  what = cinfo.blacklistName (blstate);

                    if (check == null)
                        continue;

                    Blackdata   bl = (Blackdata) blist.isBlackListed (check);
                    if (bl != null) {
                        String  where;

                        data.logging (Log.WARNING, "mailgun", "Found " + what + ": " + check + " (" + cid + ") in " + bl.where () + " blacklist, ignored");
                        blist.writeBounce (data.mailing_id, cid);
                        isblisted = true;
                    }
                }
                if (isblisted)
                    return;
            }

            String  mediatypes = getMediaTypes (cid);
            if (mediatypes == null)
                return;

            urlMaker.setCustomerID (cid);
            try {
                mailer.writeMail (cinfo, 0, mtype, cid, mediatypes, tagNames, urlMaker);
            } catch (Exception e) {
                data.logging (Log.ERROR, "mailgun", "Failed to write mail: " + e.toString ());
            }

            if (needSamples) {
                urlMaker.setCustomerID (0);

                Vector  v = StringOps.splitString (data.sampleEmails ());

                for (int mcount = 0; mcount < v.size (); ++mcount) {
                    String  email = validateSampleEmail ((String) v.elementAt (mcount));

                    if ((email != null) && (email.length () > 3) && (email.indexOf ('@') != -1)) {
                        cinfo.setEmail (email);
                        for (int n = 0; n < emailCount; ++n) {
                            EMMTag  etag = emailTags.elementAt (n);

                            etag.mTagValue = email;
                        }
                        for (int n = 0; n < Const.Mailtype.MAX; ++n) {
                            if ((n == 0) || ((n & data.masterMailtype) != 0)) {
                                mailtypeTag.mTagValue = Integer.toString (n);
                                try {
                                    mailer.writeMail (cinfo, mcount + 1, n, 0, "email", tagNames, urlMaker);
                                } catch (Exception e) {
                                    data.logging (Log.ERROR, "mailgun", "Failed to write sample mail: " + e.toString ());
                                }
                            }
                        }
                    }
                }
                needSamples = false;
            }
        }

        @Override
        public Object extractData (ResultSet rset) throws SQLException, DataAccessException {
            while (rset.next ()) {
                extractRecord (rset);
            }
            return null;
        }
    }
        
    /** Execute a prepared mailgun
     * @param opts options to control the execution beyond DB information
     */
    private void doExecute (Hashtable <String, Object> opts) throws Exception {
        data.resume ();
        data.options (opts, 2);
        data.sanityCheck ();

        // get constructed selectvalue based on tag names in Hashtable
        data.startExecution ();
        selectQuery = getSelectvalue (tagNames, false);
        wSelectQuery = getSelectvalue (tagNames, true);

        MailWriter  mailer = (MailWriter) mkMailWriterMeta (data, allBlocks, tagNames);

        int columnCount = 0;
        Vector <EMMTag>
            email_tags = new Vector <EMMTag> ();
        int email_count = 0;
        boolean hasOverwriteData = false;
        boolean hasVirtualData = false;

        for (Enumeration e = tagNames.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();

            if ((! tag.globalValue) && (! tag.fixedValue) && (! tag.mutableValue)) {
                if (tag.tagType == EMMTag.TAG_DBASE) {
                    ++columnCount;
                } else if ((tag.tagType == EMMTag.TAG_INTERNAL) && (tag.tagSpec == EMMTag.TI_EMAIL)) {
                    email_tags.add (tag);
                    email_count++;
                } else if ((tag.tagType == EMMTag.TAG_INTERNAL) && (tag.tagSpec == EMMTag.TI_DBV)) {
                    hasVirtualData = true;
                    data.initializeVirtualData (tag.mSelectString);
                } else if (tag.tagType == EMMTag.TAG_CUSTOM) {
                    if ((data.customMap != null) && data.customMap.containsKey (tag.mTagFullname))
                        tag.mTagValue = data.customMap.get (tag.mTagFullname);
                    else
                        tag.mTagValue = null;
                }
            }
        }

        if (data.lusecount > 0)
            columnCount += data.lusecount;
        hasOverwriteData = data.overwriteData ();
        skipSetup ();

        try {
            data.logging (Log.INFO, "execute", "Start creation of mails");

            boolean     needSamples = data.isWorldMailing () && (data.sampleEmails () != null) && ((data.availableMedias & (1 << Media.TYPE_EMAIL)) != 0);
            Vector      clist = data.generationClauses ();
            HashSet <Long>  seen = prepareCollection ();
            Extractor   ex;

            data.prefillRecipients (seen);
            ex = new Extractor (data, this, tagNames.get ("[agnMAILTYPE]"),
                        blist, urlMaker, mailer,
                        needSamples, seen,
                        hasOverwriteData, hasVirtualData,
                        email_tags, email_count);
            
            for (int state = 0; state < clist.size (); ++state) {
                String  clause = (String) clist.get (state);
                if (clause == null)
                    continue;

                String  query = (state == 0 ? selectQuery : wSelectQuery) + " " + clause;

                if ((state == 1) && (seen.size () > 0)) {
                    data.increaseStartblockForStepping ();
                }
                if ((mailer.blockSize > 0) && (mailer.inBlockCount > 0))
                    mailer.checkBlock (true);

                data.dbase.op (query).query (query, ex);
            }
        } catch (SQLException e){
            data.updateGenerationState (4);
            data.suspend ();
            throw new Exception("Error during main query or mail generation:" + e);
        }
        skipFinalize ();

        mailer.done ();
        if (! data.isPreviewMailing ()) {
            finalizeMailingToDatabase (mailer);
        }
        data.endExecution ();
        data.updateGenerationState ();

        data.logging (Log.DEBUG, "execute", "Successful end");
        data.suspend ();
    }

    public Object mkDestroyer (int mailingId) throws Exception {
        return new Destroyer (mailingId);
    }

    /** Full execution of a mail generation
     * @param custid optional customer id
     * @return Status string
     */
    public String fire (String custid) throws Exception {
        String  str;

        str = null;
        try {
            data.logging (Log.INFO, "mailgun", "Starting up");
            Hashtable <String, Object>  opts = new Hashtable <String, Object> ();

            if (custid != null)
                opts.put ("customer-id", custid);
            doPrepare (opts);
            doExecute (opts);
            str = "Success: Mailgun fired.";
        } catch (Exception e) {
            data.logging (Log.ERROR, "mailgun", "Creation failed: " + e.toString ());
            if ((data != null) && (data.mailing_id > 0)) {
                Destroyer   d = (Destroyer) mkDestroyer ((int) data.mailing_id);

                data.logging (Log.INFO, "mailgun", "Try to remove failed mailing: " + e);
                str = d.destroy ();
                d.done ();
            }
            try {
                done ();
            } catch (Exception temp) {
                data.logging (Log.ERROR, "mailgun", "Failed to finalize mailgun (after failure): " + temp.toString ());
            }
            throw e;
        }
        data.logging (Log.INFO, "mailgun", "Execution done: " + str);
        try {
            done ();
        } catch (Exception e) {
            data.logging (Log.ERROR, "mailgun", "Failed to finalize mailgun: " + e.toString ());
        }
        return str;
    }

    /** Optional add database hint
     * @return the hint
     */
    public String getHint () {
        return "";
    }

    /** Build the complete big query
     * @param tagNames the tags
     * @param allBlocks all content information
     * @return the created query
     */
    public String getSelectvalue (Hashtable tagNames, boolean hint) throws Exception {
        StringBuffer    select_string = new StringBuffer();

        select_string.append("SELECT ");
        if (hint) {
            String  hstr = getHint ();

            if (hstr.length () > 0) {
                select_string.append (hstr);
            }
        }
        if (tagNames != null) {
            EMMTag current_tag=null;

            // append all select string values of all tags
            select_string.append ("cust.customer_id, bind.user_type");
            for ( Enumeration e = tagNames.elements(); e.hasMoreElements(); ) {
                current_tag = (EMMTag) e.nextElement(); // new
                if ((! current_tag.globalValue) && (! current_tag.fixedValue) && (! current_tag.mutableValue) && (current_tag.tagType == EMMTag.TAG_DBASE)) {
                    select_string.append(", " + current_tag.mSelectString);
                }
            }
            if (data.lusecount > 0)
                for (int n = 0; n < data.lcount; ++n) {
                    Column  c = data.columnByIndex (n);

                    if (c.inuse) {
                        select_string.append (", ");
                        select_string.append (c.ref == null ? "cust" : c.ref);
                        select_string.append (".");
                        select_string.append (c.name);
                    }
                }
            // remove last comma
            // select_string.deleteCharAt(select_string.length() - 1);
        } else
            select_string.append ("count(distinct customer_id)");

        // turn stringbuffer into string
        String result = select_string.toString();

        data.logging (Log.DEBUG, "selectvalue", "SQL-String: " + result);

        return result;
    }
}
