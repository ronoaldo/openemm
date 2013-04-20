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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.agnitas.util.Const;
import org.agnitas.util.Log;

/**
 * Holds all Blocks of a Mailing
*/
public class BlockCollection {
    /**
     * Reference to configuration
     */
    private Data         data;

    /**
     * All blocks in the mailing
     */
    public BlockData    blocks[];

    /**
     * Total number of text blocks
     */
    protected int       totalText = 0;

    /**
     * Total number of all blocks
     */
    protected int       totalNumber = 0;

    /**
     * All dynamic blocks
     */
    public DynCollection    dynContent;

    /**
     * Collection of all found dynamic names in blocks
     */
    public Vector <String>  dynNames;

    /**
     * Number of all names in dynNames
     */
    public int      dynCount;

    /**
     * if we have any attachments
     */
    public boolean      hasAttachment = false;

    /**
     * total amount of attachments
     */
    public int      numberOfAttachments = 0;

    /**
     * referenced database fields in conditions
     */
    public HashSet <String> conditionFields;

    public Object mkDynCollection (Object nData) {
        return new DynCollection ((Data) nData);
    }

    public Object mkEMMTag (String tag, boolean isCustom) throws Exception {
        EMMTag  tg = new EMMTag (data, tag, isCustom);

        tg.initialize (data, false);
        return tg;
    }

    /**
     * Constructor for this class
     */
    public void setupBlockCollection (Object nData, String customText) throws Exception {
        data = (Data) nData;

        if (customText == null) {
            totalText = 0;
            totalNumber = 0;
            blocks = null;
            readBlockdata ();

            dynContent = (DynCollection) mkDynCollection (data);
            dynContent.collectParts ();

            dynNames = new Vector <String> ();
            dynCount = 0;

            conditionFields = new HashSet <String> ();
        } else {
            BlockData   block = (BlockData) mkBlockData ();

            block.id = 0;
            block.content = customText;
            block.cid = Const.Component.NAME_TEXT;
            block.is_parseable = true;
            block.is_text = true;
            block.type = BlockData.TEXT;
            block.media = Media.TYPE_EMAIL;
            block.comptype = 0;

            totalText = 1;
            totalNumber = 1;
            blocks = new BlockData[1];
            blocks[0] = block;
        }
    }

    /**
     * Add a string to the receiver `To:' line in the mailing
     * to mark an admin- or testmailing
     *
     * @return the optional string
     */
    public String addTo () {
        if (data.isAdminMailing ()) {
            return "\"Adminmail\" ";
        } else if (data.isTestMailing ()) {
            return "\"Testmail\" ";
        }
        return "";
    }

    /**
     * Add a string to the receiver `Subject:' line
     *
     * @return the optional string
     */
    public String addSubject () {
        return "";
    }

    public Object mkBlockData () {
        return new BlockData ();
    }

    /** Returns the envelope sender address
     * @return the address
     */
    public String envelopeFrom () {
        return data.getEnvelopeFrom ();
    }
    public String returnPath () {
        return null;
    }

    /** Adds the `From' line to header
     * @return the from line
     */
    public String headFrom () {
        if (data.fromEmail.full != data.fromEmail.pure) {
            return "HFrom: " + data.fromEmail.full_puny + data.eol;
        } else {
            return "HFrom: <" + data.fromEmail.full_puny + ">" + data.eol;
        }
    }

    /** Adds the 'Reply-To' line to head
     * @return the reply-to line
     */
    public String headReplyTo () {
        if ((data.replyTo != null) && data.replyTo.valid (true) && (! data.fromEmail.full.equals (data.replyTo.full))) {
            return "HReply-To: " + data.replyTo.full_puny + data.eol;
        }
        return "";
    }

    public String headAdditional () {
        return "";
    }

    /**
     * Creates the first block holding the header information.
     *
     * @return the newly created block
     */
    public BlockData createBlockZero () {
        BlockData   b = (BlockData) mkBlockData ();
        String      head;

        if ((data.fromEmail != null) &&
            data.fromEmail.valid (false) &&
            (data.subject != null)) {
            String mfrom = envelopeFrom ();
            String rpath = returnPath ();

            if (mfrom == null) {
                mfrom = "";
            }
            if (rpath == null) {
                rpath = mfrom;
            }
            head =  "T[agnSYSINFO name=\"EPOCH\"]" + data.eol +
                "S<" + mfrom + ">" + data.eol +
                "R<[agnEMAIL code=\"punycode\"]>" + data.eol +
                "H?mP?Return-Path: <" + rpath +">" + data.eol +
                "HReceived: by [agnSYSINFO name=\"FQDN\" default=\"" + data.domain + "\"] for <[agnEMAIL]>; [agnSYSINFO name=\"RFCDATE\"]" + data.eol +
                "HMessage-ID: <" + EMMTag.internalTag (EMMTag.TI_MESSAGEID) + ">" + data.eol +
                "HDate: [agnSYSINFO name=\"RFCDATE\"]" + data.eol;
            head += headFrom ();
            head += headReplyTo ();
            head += "HTo: " + addTo () + "<" + "[agnEMAIL code=\"punycode\"]" + ">" + data.eol +
                "HSubject: " + addSubject () + data.subject + data.eol;
            head += headAdditional ();
            head += "HX-Mailer: " + data.makeMailer () + data.eol +
                "HMIME-Version: 1.0" + data.eol;
        } else {
            head = "- unset -" + data.eol;
        }

        b.content = head;
        b.cid = Const.Component.NAME_HEADER;
        b.is_parseable = true;
        b.is_text = true;
        b.type = BlockData.HEADER;
        b.media = Media.TYPE_EMAIL;
        b.comptype = 0;
        return b;
    }

    /**
     * Retreives the blockdata from a SQL record
     *
     * @return the filled blockdata
     */
    public Object retreiveBlockdata (Map <String, Object> row) {
        BlockData   tmp;
        int     comptype;
        long        urlid;
        String      compname;
        String      mtype;
        int     target_id;
        String      emmblock;
        byte[]      binary;

        comptype = data.dbase.asInt (row.get ("comptype"));
        urlid = data.dbase.asLong (row.get ("url_id"));
        compname = data.dbase.asString (row.get ("compname"));
        mtype = data.dbase.asString (row.get ("mtype"));
        target_id = data.dbase.asInt (row.get ("target_id"));
        emmblock = data.dbase.asClob (row.get ("emmblock"));
        binary = data.dbase.asBlob (row.get ("binblock"));
        tmp = (BlockData) mkBlockData ();
        tmp.media = Media.TYPE_UNRELATED;
        if (comptype == 0) {
            if (compname.equals (Const.Component.NAME_TEXT)) {
                tmp.type = BlockData.TEXT;
                tmp.media = Media.TYPE_EMAIL;
            } else if (compname.equals (Const.Component.NAME_HTML)) {
                tmp.type = BlockData.HTML;
                tmp.media = Media.TYPE_EMAIL;
            } else {
                data.logging (Log.WARNING, "collect", "Invalid compname " + compname + " for comptype 0 found");
                return null;
            }
            tmp.is_parseable = true;
            tmp.is_text = true;
        } else if (comptype == 1) {
            tmp.type = BlockData.RELATED_BINARY;
        } else if (comptype == 3) {
            tmp.type = BlockData.ATTACHMENT_BINARY;
            tmp.is_attachment = true;
        } else if (comptype == 4) {
            tmp.type = BlockData.ATTACHMENT_TEXT;
            tmp.is_parseable = true;
            tmp.is_text = true;
            tmp.is_attachment = true;
        } else if (comptype == 5) {
            tmp.type = BlockData.RELATED_BINARY;
        } else {
            data.logging (Log.WARNING, "collect", "Invalid comptype " + comptype + " found");
            return null;
        }
        tmp.comptype = comptype;
        tmp.urlID = urlid;
        tmp.cid = compname;
        tmp.mime = mtype;
        tmp.targetID = target_id;

        // write to different String, depending on text/binary data
        if (emmblock != null) {
            if (tmp.is_parseable) {
                tmp.content =  StringOps.convertOld2New (emmblock);
            } else {
                tmp.content = emmblock;
            }
        }
        tmp.binary = binary;
        if (tmp.binary != null) {
            if ((! tmp.is_parseable) && (tmp.content != null) && (tmp.content.length () == 0)) {
                tmp.content = null;
            }
        }
        return tmp;
    }

    /**
     * Retreive optional related data for newly created block
     *
     * @return the optional block
     */
    public Object retreiveRelatedBlockdata (Object obd) {
        return null;
    }

    /**
     * Return mailing_id related part of the where clause to
     * retreive the components
     *
     * @return the clause part
     */
    public String mailingClause () {
        return "mailing_id = " + data.mailing_id;
    }

    public String reduceClause () {
        if (data.isPreviewMailing ())
            return "AND comptype IN (0, 4) ";
        return "";
    }

    public String componentFields () {
        return "comptype, url_id, compname, mtype, target_id, emmblock, binblock";
    }

    public void cleanupBlockCollection(Vector<BlockData> c) {
    }

    /**
     * Reads the blocks used by this mailing from the database
     */
    public void readBlockdata () throws Exception {
        String  query;

        query = "SELECT " + componentFields () + " " +
            "FROM component_tbl " +
            "WHERE company_id = " + data.company_id + " AND (" + mailingClause () + ") " + reduceClause () +
            "ORDER BY component_id";
        try {
            Vector <BlockData>      collect;
            List <Map <String, Object>> rq;
            int             n;

            collect = new Vector <BlockData> ();
            collect.addElement (createBlockZero ());
            totalNumber = 1;
            rq = data.dbase.query (query);
            for (n = 0; n < rq.size (); ++n) {
                Map <String, Object>    row = rq.get (n);
                BlockData       tmp;

                tmp = (BlockData) retreiveBlockdata (row);
                if (tmp == null) {
                    continue;
                }

                if ((tmp.type == BlockData.ATTACHMENT_BINARY) || (tmp.type == BlockData.ATTACHMENT_TEXT)) {
                    hasAttachment = true;
                    ++numberOfAttachments;
                }

                collect.addElement (tmp);
                ++totalNumber;
                tmp = (BlockData) retreiveRelatedBlockdata (tmp);
                if (tmp != null) {
                    collect.addElement (tmp);
                    ++totalNumber;
                }
            }
            cleanupBlockCollection (collect);
            totalText = 0;
            totalNumber = collect.size ();
            blocks = collect.toArray (new BlockData[totalNumber]);
            for (n = 0; n < totalNumber; ++n) {
                BlockData   b = blocks[n];

                if (b.isEmailText ()) {
                    ++totalText;
                }
                data.logging (Log.DEBUG, "collect",
                          "Block " + n + " (" + totalNumber + "): " + b.cid + " [" + b.mime + "]");
            }

            Arrays.sort ((Object[]) blocks);

            for (n = 0; n < totalNumber; ++n) {
                BlockData   b = blocks[n];

                b.id = n;
                if (b.targetID != 0) {
                    Target  tgt = data.getTarget (b.targetID);

                    if ((tgt != null) && tgt.valid ()) {
                        b.condition = tgt.sql;
                    }
                }
                data.logging (Log.DEBUG, "collect",
                          "Block " + n + " (" + totalNumber + "): " + b.cid + " [" + b.mime + "] " + b.targetID + (b.condition != null ? " SQL: " + b.condition : ""));
            }
        } catch (Exception e) {
            throw new Exception ("Unable to read block: " + e, e);
        }
    }

    /**
     * returns the block at the given position
     *
     * @param pos the index into the block array
     * @return the block at the requested position
     */
    public BlockData getBlock (int pos) {
        return blocks[pos];
    }

    /**
     * Parses a block, collecting all tags in a hashtable
     *
     * @param cb the block to parse
     * @param tag_table the hashtable to collect tag
     */
    public void parseBlock (BlockData cb, Hashtable <String, EMMTag> tag_table) throws Exception {
        if (cb.is_parseable) {
            int tag_counter = 0;
            String current_tag;

            // get all tags inside the block
            while( (current_tag = cb.get_next_tag() ) != null){
                try{
                    // add tag and EMMTag data structure to hashtable
                    if (! tag_table.containsKey (current_tag)) {
                        EMMTag  ntag = (EMMTag) mkEMMTag (current_tag, false);
                        String  dyName;

                        if ((ntag.tagType == EMMTag.TAG_INTERNAL) &&
                            (ntag.tagSpec == EMMTag.TI_DYN) &&
                            ((dyName = ntag.mTagParameters.get ("name")) != null)) {
                            int n;

                            for (n = 0; n < dynCount; ++n) {
                                if (dyName.equals (dynNames.elementAt (n))) {
                                    break;
                                }
                            }
                            if (n == dynCount) {
                                dynNames.addElement (dyName);
                                dynCount++;
                            }
                        }
                        tag_table.put(current_tag, ntag);
                        data.logging (Log.DEBUG, "collect", "Added Tag: " + current_tag);
                    } else
                        data.logging (Log.DEBUG, "collect", "Skip existing Tag: " + current_tag);
                } catch (Exception e) {
                    throw new Exception (
                        "Error while trying to query block " + tag_counter + " :" +e);
                }
                tag_counter++;
            }

            // check for tagless blocks
            if ( tag_counter == 0 ) {
                cb.is_parseable = false; // block contained no tags!
            }

        }
    }

    /**
     * Substidute parts of a filename using some pattern
     *
     * @return the replacement string
     */
    public String substituteParameter (String mod, String parm, String dflt) {
        return dflt;
    }

    private String parseSubstitution (String src) {
        int     cur = 0;
        int     start, end;
        StringBuffer    res = null;

        while ((start = src.indexOf ("%[", cur)) != -1) {
            end = src.indexOf ("]%", start);
            if (end == -1) {
                break;
            }
            if (res == null)
                res = new StringBuffer (src.length ());
            res.append (src.substring (cur, start));

            String  cont = src.substring (start + 2, end);
            int parmoffset = cont.indexOf (':');
            String  mod, parm;

            if (parmoffset == -1) {
                mod = cont;
                parm = null;
            } else {
                mod = cont.substring (0, parmoffset);
                ++parmoffset;
                while ((parmoffset < cont.length ()) && Character.isWhitespace (cont.charAt (parmoffset))) {
                    ++parmoffset;
                }
                parm = cont.substring (parmoffset);
            }
            res.append (substituteParameter (mod, parm, src.substring (start, end + 2)));
            cur = end + 2;
        }
        if ((res != null) && (cur < src.length ())) {
            res.append (src.substring (cur));
        }
        return res == null ? null: res.toString ();
    }

    /**
     * Parses all blocks returning a hashtable with all found
     * tags
     *
     * @return the hashtable with all tags
     */
    public Hashtable <String, EMMTag> parseBlocks() throws Exception {
        Hashtable <String, EMMTag>  tag_table = new Hashtable <String, EMMTag> ();

        // first add all custom tags
        if (data.customTags != null) {
            for (int n = 0; n < data.customTags.size (); ++n) {
                String  tname = data.customTags.get (n);

                if (! tag_table.containsKey (tname)) {
                    EMMTag  ntag = (EMMTag) mkEMMTag (tname, true);

                    tag_table.put (tname, ntag);
                }
            }
        }
        // go through all blocks
        for (int count = 0; count < this.totalNumber; count++) {
            data.logging (Log.DEBUG, "collect", "Parsing block " + count);

            parseBlock (blocks[count], tag_table);
        }

        if (dynContent != null) {
            for (Enumeration<DynName> e = dynContent.names.elements (); e.hasMoreElements (); ) {
                DynName tmp = e.nextElement ();
                String  cname = tmp.getAssignedColumn ();

                if (cname != null) {
                    conditionFields.add (cname);
                }
                for (int n = 0; n < tmp.clen; ++n) {
                    DynCont cont = tmp.content.elementAt (n);

                    if (cont.text != null) {
                        parseBlock (cont.text, tag_table);
                    }
                    if (cont.html != null) {
                        parseBlock (cont.html, tag_table);
                    }
                }
            }
        }

        for (int count = 0; count < totalNumber; ++count) {
            BlockData   b = blocks[count];

            switch (b.comptype) {
            case 3:
            case 4:
            case 7:
                b.cidEmit = parseSubstitution (b.cid);
                b.mimeEmit = parseSubstitution (b.mime);
                break;
            case 5:
                boolean match = false;

                for (Enumeration <EMMTag> e = tag_table.elements (); e.hasMoreElements (); ) {
                    EMMTag  tag = e.nextElement ();

                    if ((tag.tagType == EMMTag.TAG_DBASE) && (tag.tagSpec == EMMTag.TDB_IMAGE)) {
                        String  name = tag.mTagParameters.get ("name");

                        if ((name != null) && name.equals (b.cid)) {
                            b.cidEmit = tag.mTagValue;
                            match = true;
                        }
                    } else if ((tag.tagType == EMMTag.TAG_INTERNAL) && (tag.tagSpec == EMMTag.TI_IMGLINK)) {
                        String  name = tag.mTagParameters.get ("name");

                        if ((name != null) && name.equals (b.cid)) {
                            tag.imageLinkReference (data, b.urlID);
                            b.cidEmit = tag.ilURL;
                            match = true;
                        }
                    }
                }
                if (! match) {
                    if (b.mime.startsWith ("image/")) {
                        b.cidEmit = data.defaultImageLink (b.cid);
                    }
                }
                break;
            }
        }
        return tag_table;
    }

    /**
     * create the corresponding url_string for the tags:
     * 1 - Profile
     * 2 - Unsubscribe
     * 3 - AutoURL
     * 4 - Onepixellog
     *
     * @param tag the tag itself
     * @param urlMaker an instance of TagString to create the URLs
     * @return the newly created URL
     */
    protected String create_url_tag (EMMTag tag, URLMaker urlMaker) throws Exception {
        switch (tag.tagSpec) {
        case 1: return urlMaker.profileURL ();
        case 2: return urlMaker.unsubscribeURL ();
        case 3:
            long    urlid = Long.parseLong (tag.mTagParameters.get ("url"));

            if (urlid <= 0) {
                data.logging (Log.FATAL, "collect", "Invalid Autourl parameter or parameter not found");
                throw new Exception ("Failed due to missing/wrong URL parameter in auto url");
            }
            return urlMaker.autoURL (urlid);
        case 4: return urlMaker.onepixelURL ();
        default:
            throw new Exception ("Unknown tagSpec " + tag.tagSpec);
        }
    }

    /**
     * Already parse and replace tags with fixed value
     *
     * @param b the block to parse
     * @param tagTable the tag collection
     */
    public void parse_fixed_block (BlockData b, Hashtable <String, EMMTag> tagTable) {
        String      cont = b.content != null ? b.content : "";
        int     clen = cont.length ();
        StringBuffer    buf = new StringBuffer (clen + 128);
        Vector <TagPos> pos = b.tag_position;
        int     count = pos.size ();
        int     start = 0;
        int     offset = 0;
        boolean     changed = false;

        for (int m = 0; m < count; ) {
            TagPos  tp = pos.get (m);
            EMMTag  tag = tagTable.get (tp.tagname);
            String  value = tag.mTagValue;

            if ((value == null) && tag.fixedValue) {
                tag.fixedValue = false;
            }
            if (tag.fixedValue) {
                offset += value.length () - tag.mTagFullname.length ();
                buf.append (cont.substring (start, tp.start) + value);
                start = tp.end + 1;
                pos.removeElementAt (m);
                --count;
                changed = true;
            } else {
                if ((tp.content != null) && tp.content.is_parseable)
                    parse_fixed_block (tp.content, tagTable);
                tp.start += offset;
                tp.end += offset;
                ++m;
            }
        }
        if (changed) {
            if (start < clen) {
                buf.append (cont.substring (start));
            }
            b.content = StringOps.convertOld2New (buf.toString ());
            if (count == 0) {
                b.is_parseable = false;
            }
        }
    }

    /**
     * Parse and replace all tags with fixed value
     *
     * @param tagTable the collection of all tags
     */
    public void replace_fixed_tags (Hashtable <String, EMMTag> tagTable) {
        for (int n = 0; n < totalNumber; ++n) {
            if (blocks[n].is_parseable) {
                parse_fixed_block (blocks[n], tagTable);
            }
        }
        if (dynContent != null) {
            for (Enumeration<DynName> e = dynContent.names.elements (); e.hasMoreElements (); ) {
                DynName tmp = e.nextElement ();

                for (int n = 0; n < tmp.clen; ++n) {
                    DynCont cont = tmp.content.elementAt (n);

                    if (cont.text != null) {
                        parse_fixed_block (cont.text, tagTable);
                    }
                    if (cont.html != null) {
                        parse_fixed_block (cont.html, tagTable);
                    }
                }
            }
        }
    }
}
