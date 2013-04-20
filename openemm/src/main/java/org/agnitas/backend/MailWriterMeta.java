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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.agnitas.util.Log;

/** Implements writing of mailing information to
 * a XML file
 */
public class MailWriterMeta extends MailWriter {
    /** Write a log entry to the database after that number of mails */
    private int     logSize;
    /** Reference to available tagnames */
    protected Hashtable tagNames;
    /** Base pathname without extension to write to */
    private String      fname;
    /** The pathname for the real XML file */
    private String      pathname;
    /** Output stream */
    private OutputStream    out;
    /** Output buffer */
    public XMLWriter    writer;
    /** Counter to give each block written an unique ID */
    private int     blockID;
    /** if we should keep admin/test mails for debug purpose */
    public  boolean     keepATmails;
    /** after this number of records flush buffer to disk */
    public int      flushCount;

    /** Flush the created buffer to disk
     */
    private void flushBuffer () throws Exception {
        if ((out == null) || (writer == null))
            throw new Exception ("Try to flush buffer to not existing stream");
        writer.flush ();
    }

    private Vector <String> escape (Vector <String> input) {
        Vector <String> rc = new Vector <String> (input.size ());
        
        for (int n = 0; n < input.size (); ++n) {
            rc.add (input.get (n).replace ("\\", "\\\\"));
        }
        return rc;
    }
    
    /** Start the mail generating backend
     * @param output detailed output description
     * @param filename pathname to the XML file
     */
    private void startXMLBack (Vector <String> options, String output, String filename) throws Exception {
        Vector <String> command = new Vector <String> ();
        String      cmd;
        File        efile;
        int     rc;

        try {
            efile = File.createTempFile ("error", null);
        } catch (Exception e) {
            String  javaTemp = System.getProperty ("java.io.tmpdir");
            data.logging (Log.ERROR, "write/meta", "Failed to create temp.file due to: " + e.toString () + " (missing temp.directory '" + javaTemp + "'?)");
            throw e;
        }

        command.add (data.xmlBack ());
        if (options != null) {
            for (int n = 0; n < options.size (); ++n)
                command.add (options.elementAt (n));
        }
        command.add ("-q");
        if (data.eol.equals ("\n")) {
            command.add ("-l");
        }
        command.add ("-E" + efile.getAbsolutePath ());
        command.add ("-o" + output);
        command.add (filename);
        cmd = command.toString ();
        data.markToRemove (efile);
        try {
            data.logging (Log.DEBUG, "write/meta", "Try to execute " + cmd);

            ProcessBuilder  bp = new ProcessBuilder (escape (command));
            Process     proc = bp.start ();
            OutputStream    otemp = proc.getOutputStream ();
            InputStream itemp;

            if (otemp != null) {
                try {
                    otemp.close ();
                } catch (IOException ie) {
                    data.logging (Log.VERBOSE, "write/meta", "Failed to close stdin for " + cmd + ": " + ie.toString ());
                }
            }
            rc = proc.waitFor ();
            itemp = proc.getInputStream ();
            if (itemp != null) {
                try {
                    itemp.close ();
                } catch (IOException ie) {
                    data.logging (Log.VERBOSE, "write/meta", "Failed to close stdout for " + cmd + ": " + ie.toString ());
                }
            }
            itemp = proc.getErrorStream ();
            if (itemp != null) {
                try {
                    itemp.close ();
                } catch (IOException ie) {
                    data.logging (Log.VERBOSE, "write/meta", "Failed to close stderr for " + cmd + ": " + ie.toString ());
                }
            }

            FileInputStream err;
            String      msg;
            int     size;

            try {
                err = new FileInputStream (efile);
            } catch (FileNotFoundException e) {
                err = null;
            }
            msg = null;
            size = 0;
            if (err != null) {
                size = err.available ();
                if (size > 0) {
                    int use = size > 4096 ? 4096 : size;
                    byte[]  buf = new byte[use];

                    err.read (buf);
                    msg = new String (buf);
                }
                err.close ();
            }
            if ((rc != 0) || (msg != null)) {
                data.logging (rc == 0 ? Log.INFO : Log.ERROR, "writer/meta", "command " + cmd + " returns " + rc + (msg != null ? ":\n" + msg : ""));
                if (rc != 0)
                    throw new Exception ("command returns " + rc + (msg != null ? " (" + size + ")" : ""));
            }
        } catch (Exception e) {
            data.logging (Log.ERROR, "writer/meta", "command " + cmd + " failed (Missing binary? Wrong permissions?): " + e);
            throw new Exception ("Execution of " + cmd + " failed: " + e);
        } finally {
            if (efile.delete ())
                data.unmarkToRemove (efile);
        }
    }

    /** Constructor
     * @param data Reference to configuration
     * @param allBlocks all content blocks
     * @param nTagNames all tag definitions
     */
    public MailWriterMeta (Data data, BlockCollection allBlocks, Hashtable nTagNames) throws Exception {
        super (data, allBlocks);
        logSize = data.mailLogNumber ();
        tagNames = nTagNames;
        fname = null;
        pathname = null;
        out = null;
        writer = null;
        if (data.isAdminMailing () || data.isTestMailing () || data.isCampaignMailing () || data.isPreviewMailing ())
            blockSize = 0;
        else
            blockSize = data.blockSize ();
        blockID = 1;
        keepATmails = false;
        flushCount = 100;
    }

    public String fileName () {
        return fname;
    }

    /** Create xmlback generation string
     * @return the newly formed string
     */
    public String generateOutputOptions () {
        return "generate:temporary=true;syslog=false;account-logfile=" + data.accLogfile () + ";bounce-logfile=" + data.bncLogfile () + ";media=email;path=" + data.mailDir ();
    }
    public String previewOutputOptions (String output) {
        return "preview:path=" + output;
    }
    public void previewOptions (Vector <String> options) {
        options.add ("-r");
    }

    /** Cleanup
     */
    public void done () throws Exception {
        super.done ();
        if (data.isAdminMailing () || data.isTestMailing ()) {
            if (pathname != null) {
                if (! keepATmails) {
                    data.markToRemove (pathname);
                }

                String  gen = generateOutputOptions ();

                startXMLBack (null, gen, pathname);
                if (! keepATmails) {
                    if ((new File (pathname)).delete ()) {
                        data.unmarkToRemove (pathname);
                    }
                }
            }
        } else if (data.isPreviewMailing ()) {
            if (pathname != null) {
                File    output = File.createTempFile ("preview", ".xml");
                String  path = output.getAbsolutePath ();
                String  opts = previewOutputOptions (path);
                String  error = null;

                data.markToRemove (pathname);
                data.markToRemove (path);
                try {
                    Vector <String> options = new Vector <String> ();

                    previewOptions (options);
                    startXMLBack (options, opts, pathname);
                } catch (Exception e) {
                    error = e.toString ();
                }
                if (data.previewOutput != null) {
                    try {
                        DocumentBuilderFactory
                                docBuilderFactory = DocumentBuilderFactory.newInstance ();
                        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder ();
                        Document    doc = docBuilder.parse (output);
                        Element     root = doc.getDocumentElement ();
                        NodeList    nlist = root.getElementsByTagName ("content");
                        int     ncount = nlist.getLength ();

                        for (int n = 0; n < ncount; ++n) {
                            Node        node = nlist.item (n);
                            NamedNodeMap    attr = node.getAttributes ();
                            Node        name = attr.getNamedItem ("name");

                            if (name != null) {
                                Node    text = node.getFirstChild ();

                                data.previewOutput.addContent (name.getNodeValue (), (text == null ? "" : text.getNodeValue ()));
                            }
                        }
                    } catch (Exception e) {
                        if (error != null)
                            error += "\n" + e.toString ();
                        else
                            error = e.toString ();
                    }
                    if (error != null)
                        data.previewOutput.setError (error);
                }
                if ((new File (path)).delete ()) {
                    data.unmarkToRemove (path);
                }
                if ((new File (pathname)).delete ()) {
                    data.unmarkToRemove (pathname);
                }
            }
        } else if (fname != null) {
            try {
                FileOutputStream    temp;
                String          msg;

                msg = data.company_id + "-" + data.mailing_id + "-" + blockCount + "\t" +
                      "Start: " + startExecutionTime + "\tEnd: " + endExecutionTime + "\n";
                temp = new FileOutputStream (fname + ".final");
                temp.write (msg.getBytes ());
                temp.close ();
            } catch (FileNotFoundException e) {
                throw new Exception ("Unable to write final stamp file " + fname + ".final: " + e);
            }
        }
    }

    /** Get encoding for block
     * @param b the block to examine
     * @return the encoding for this block
     */
    public String getEncoding (Object ob) {
        BlockData b = (BlockData) ob;
        String  encode;

        if (b.is_text) {
            if (b.media == Media.TYPE_EMAIL)
                encode = data.encoding;
            else
                encode = "none";
        } else {
            encode = "base64";
        }
        return encode;
    }

    /** Write entry part for a single block
     * @param b the block to write
     * @param encode the encoding for this block
     */
    public void emitBlockEntry (Object ob, XMLWriter.Creator c, String encode) {
        BlockData b = (BlockData) ob;
        String  cid;
        String  flag;

        if (b.mime != null) {
            c.add ("mimetype", b.getContentMime ());
        }
        c.add ("charset", data.charset, "encode", encode);
        cid = b.getContentFilename ();
        if (cid != null) {
            c.add ("cid", cid);
        }
        if (b.is_parseable) {
            flag = "is_parsable";
        } else if (b.is_text) {
            flag = "is_text";
        } else {
            flag = "is_binary";
        }
        c.add (flag, true);
        if (b.is_attachment) {
            c.add ("is_attachment", true);
        }
        if (b.media != Media.TYPE_UNRELATED) {
            c.add ("media", b.mediaType ());
        }
        if (b.condition != null) {
            c.add ("condition", b.condition);
        }
    }

    /** Write content part for a single block
     * @param b the block to write
     */
    public void emitBlockContent (Object ob) {
        BlockData b = (BlockData) ob;
        String  content;

        if (b.content != null) {
            content = b.content;
        } else if (b.binary == null) {
            content = "";
        } else {
            content = null;
        }
        if ((content == null) || (content.length () > 0)) {
            writer.opennode ("content");
            if (content == null) {
                writer.data (b.binary);
            } else {
                writer.data (content);
            }
            writer.close ("content");
        } else {
            writer.openclose ("content");
        }
    }

    /** Write blocks tag information
     * param b the block to write
     */
    public void emitBlockTags (BlockData b, int isHeader) {
        if (b.is_parseable && (b.tag_position != null)) {
            Vector  p = b.tag_position;
            int count = p.size ();

            for (int m = 0; m < count; ++m) {
                TagPos          tp = (TagPos) p.elementAt (m);
                int         type;
                XMLWriter.Creator   c = writer.create ("tagposition", "name", tp.tagname, "hash", tp.tagname.hashCode ());

                type = 0;
                if (tp.isDynamic ())
                    type |= 0x1;
                if (tp.isDynamicValue ())
                    type |= 0x2;
                if (type != 0) {
                    c.add ("type", type);
                }
                if (tp.content != null) {
                    writer.opennode (c);
                    emitBlock (tp.content, (isHeader != 0 ? isHeader + 1 : 0), 0);
                    writer.close (c);
                } else
                    writer.openclose (c);
            }
        }
    }

    /** Write a single block
     * @param b the block to write
     * @param isHeader if this is the header block
     * @param index the unique block number
     */
    private void emitBlock (BlockData b, int isHeader, int index) {
        String      encode;

        if (isHeader != 0) {
            encode = "header";
        } else {
            encode = getEncoding (b);
        }
        XMLWriter.Creator   c = writer.create ("block", "id", blockID++, "nr", index);

        emitBlockEntry (b, c, encode);
        writer.opennode (c);
        emitBlockContent (b);
        emitBlockTags (b, isHeader);
        writer.close (c);
    }

    /** Write description entity
     */
    public void emitDescription () {
        writer.open (data.companyInfo == null, "company", "id", data.company_id);
        if (data.companyInfo != null) {
            for (Enumeration e = data.companyInfo.keys (); e.hasMoreElements (); ) {
                String  name = (String) e.nextElement ();
                String  value = data.companyInfo.get (name);

                writer.single ("info", value, "name", name);
            }
            writer.close ("company");
        }
        writer.openclose ("mailinglist", "id", data.mailinglist_id);
        writer.open (data.mailingInfo == null, "mailing", "id", data.mailing_id, "name", data.mailing_name);
        if (data.mailingInfo != null) {
            for (Enumeration e = data.mailingInfo.keys (); e.hasMoreElements (); ) {
                String  name = (String) e.nextElement ();
                String  value = data.mailingInfo.get (name);

                writer.single ("info", value, "name", name);
            }
            writer.close ("mailing");
        }
        writer.openclose ("maildrop", "status_id", data.maildrop_status_id);
        writer.openclose ("status", "field", data.status_field);
    }

    /** Get transfer encoding
     * @param b the block to emit
     * @return the encoding
     */
    public String getTransferEncoding (Object ob) {
        BlockData b = (BlockData) ob;

        return b.is_text ? data.encoding : "base64";
    }

    public void getDynamicInfo (Object od, XMLWriter.Creator c) {
    }

    public void generalURLs () {
        writer.single ("profile_url", data.profileURL);
        writer.single ("unsubscribe_url", data.unsubscribeURL);
        writer.single ("auto_url", data.autoURL);
        writer.single ("onepixel_url", data.onePixelURL);
    }

    public void secrets () {
        writer.single ("password", data.password);
    }

    public void layout () {
        if (data.lusecount > 0) {
            writer.opennode ("layout", "count", data.lusecount);
            for (int n = 0; n < data.lcount; ++n) {
                Column c = data.columnByIndex (n);

                if (c.inUse ()) {
                    XMLWriter.Creator   cr = writer.create ("element", "name", c.name);

                    if (c.ref != null) {
                        cr.add ("ref", c.ref);
                    }
                    cr.add ("type", c.typeStr ());
                    writer.openclose (cr);
                }
            }
            writer.close ("layout");
        } else {
            writer.comment ("no layout");
        }
    }

    public void urls () {
        if (data.urlcount > 0) {
            writer.opennode ("urls", "count", data.urlcount);
            for (int n = 0; n < data.urlcount; ++n) {
                URL url = data.URLlist.elementAt (n);
                XMLWriter.Creator   cr = writer.create ("url", "id", url.id, "destination", url.url, "usage", url.usage);

                if (url.adminLink) {
                    cr.add ("admin_link", url.adminLink);
                }
                writer.openclose (cr);
            }
            writer.close ("urls");
        } else {
            writer.comment ("no urls");
        }
    }

    /** Start writing a new block
     */
    public void startBlock () throws Exception {
        super.startBlock ();
        fname = data.metaDir () + dirSeparator + filenamePattern;
        if (data.isAdminMailing () || data.isTestMailing ()) {
            pathname = fname + ".xml";
            out = new FileOutputStream (pathname);
        } else {
            pathname = fname + ".xml.gz";
            out = new GZIPOutputStream (new FileOutputStream (pathname));
        }
        writer = new XMLWriter (out);
        writer.start ();
        writer.opennode ("blockmail");
        writer.opennode ("description");
        emitDescription ();
        writer.close ("description");
        writer.empty ();
        writer.opennode ("general");
        writer.single ("subject", data.subject);
        writer.single ("from_email", data.fromEmail == null ? null : data.fromEmail.full);
        generalURLs ();
        secrets ();
        writer.single ("total_subscribers", data.totalSubscribers);
        writer.close ("general");
        writer.empty ();
        writer.opennode ("mailcreation");
        writer.single ("blocknr", blockCount);
        writer.single ("innerboundary", innerBoundary);
        writer.single ("outerboundary", outerBoundary);
        writer.single ("attachboundary", attachBoundary);
        writer.close ("mailcreation");
        writer.empty ();

        int mediasize;
        Media   tmp;

        for (tmp = data.media, mediasize = 0; tmp != null; tmp = (Media) tmp.next)
            ++mediasize;

        writer.open (mediasize == 0, "mediatypes", "count", mediasize);
        if (mediasize > 0) {
            for (tmp = data.media; tmp != null; tmp = (Media) tmp.next) {
                Vector  vars = tmp.getParameterVariables ();
                boolean hasVars = ((vars != null) && (vars.size () > 0));

                writer.open (! hasVars, "media", "type", tmp.typeName (), "priority", tmp.priorityName (), "status", tmp.statusName ());
                if (hasVars) {
                    for (int m = 0; m < vars.size (); ++m) {
                        String  name = (String) vars.elementAt (m);
                        Vector  vals = tmp.findParameterValues (name);
                        boolean hasVals = ((vals != null) & (vals.size () > 0));

                        writer.open (! hasVals, "variable", "name", name);
                        if (hasVals) {
                            for (int o = 0; o < vals.size (); ++o) {
                                writer.single ("value", (String) vals.elementAt (o));
                            }
                            writer.close ("variable");
                        }
                    }
                    writer.close ("media");
                }
            }
            writer.close ("mediatypes");
        }
        writer.empty ();

        writer.opennode ("blocks", "count", allBlocks.totalNumber);
        for (int n = 0; n < allBlocks.totalNumber; ++n) {
            BlockData   b = allBlocks.getBlock (n);

            emitBlock (b, (b.type == BlockData.HEADER ? 1 : 0), n);
        }
        writer.close ("blocks");
        writer.empty ();

        writer.opennode ("types", "count", 3);
        for (int n = 0; n < 3; ++n) {
            writer.opennode ("type", "id", n);

            Vector <BlockData>  use = new Vector <BlockData> ();
            int         used, part, text;

            part = -1;
            text = -1;
            for (BlockData b : allBlocks.blocks) {
                boolean doit = b.isEmailHeader () || b.isEmailAttachment ();

                if (! doit) {
                    switch (n) {
                    case 0:
                        doit = b.isEmailPlaintext ();
                        break;
                    case 1:
                        doit = b.isEmailText ();
                        break;
                    case 2:
                        doit = true;
                        break;
                    }
                }
                if (doit) {
                    if ((part == -1) && b.isEmailAttachment ()) {
                        part = use.size () - 1;
                    }
                    if (b.isEmailText ()) {
                        text = use.size ();
                    }
                    use.add (b);
                }
            }
            used = use.size ();
            if (part == -1) {
                part = used;
            }
            if (text == -1) {
                text = used;
            }
            for (int m = 0; m < used; ++m) {
                BlockData       b = use.elementAt (m);
                XMLWriter.Creator   c = writer.create ("blockspec", "nr", b.id);

                if (b.isEmailPlaintext () && (data.lineLength > 0)) {
                    c.add ("linelength", data.lineLength);
                } else if (b.isEmailHTML () && (data.onepixlog != Data.OPL_NONE)) {
                    String  opl;

                    switch (data.onepixlog) {
                    default:
                        opl = null;
                        break;
                    case Data.OPL_TOP:
                        opl = "top";
                        break;
                    case Data.OPL_BOTTOM:
                        opl = "bottom";
                        break;
                    }
                    if (opl != null) {
                        c.add ("onepixlog", opl);
                    }
                }
                writer.opennode (c);
                if (b.comptype == 0) {
                    if (b.type == BlockData.HEADER) {
                        writer.opennode ("postfix", "output", 0);
                        writer.opennode ("fixdata", "valid", "simple");
                        if (n == 0) {       // simple text mail
                            writer.data ("HContent-Type: text/plain; charset=\"" + data.charset + "\"" + data.eol +
                                     "HContent-Transfer-Encoding: " + data.encoding + data.eol);
                        } else if (n == 1) {    // online HTML
                            writer.data ("HContent-Type: multipart/alternative;" + data.eol +
                                     "\tboundary=\"" + outerBoundary + "\"" + data.eol);
                        } else {        // offline HTML
//                          buf.append ("HContent-Type: multipart/related; type=\"multipart/alternative\";" + data.eol +
//                                     "\tboundary=\"" + xmlStr (outerBoundary) + "\"" + data.eol);
                            writer.data ("HContent-Type: multipart/related;" + data.eol +
                                     "\tboundary=\"" + outerBoundary + "\"" + data.eol);
                        }
                        writer.data ("." + data.eol);
                        writer.close ("fixdata");
                        writer.opennode ("fixdata", "valid", "attach");
                        writer.data ("HContent-Type: multipart/mixed; boundary=\"" + attachBoundary +"\"" + data.eol +
                                 "." + data.eol);
                        writer.close ("fixdata");
                        writer.close ("postfix");
                    } else if (b.type == BlockData.TEXT) {
                        writer.opennode ("prefix");
                        if (n > 0) {
                            writer.opennode ("fixdata", "valid", "simple");
                            writer.data ("This is a multi-part message in MIME format." + data.eol +
                                     data.eol +
                                     "--" + outerBoundary + data.eol);
                            if (n == 2) {
                                writer.data ("Content-Type: multipart/alternative;" + data.eol +
                                         "\tboundary=\"" + innerBoundary + "\"" + data.eol +
                                         data.eol +
                                         "--" + innerBoundary + data.eol);
                            }
                            writer.data ("Content-Type: text/plain; charset=\"" + data.charset + "\"" + data.eol +
                                     "Content-Transfer-Encoding: " + data.encoding + data.eol +
                                     data.eol);
                            writer.close ("fixdata");
                        }
                        writer.opennode ("fixdata", "valid", "attach");
                        writer.data ("--" + attachBoundary + data.eol);
                        if (n == 0) {
                            writer.data ("Content-Type: text/plain; charset=\"" +  data.charset + "\"" + data.eol +
                                     "Content-Transfer-Encoding: " + data.encoding + data.eol +
                                     data.eol);
                        } else if (n == 1) {
                            writer.data ("Content-Type: multipart/alternative;" + data.eol +
                                     "\tboundary=\"" + outerBoundary + "\"" + data.eol +
                                     data.eol);
                        } else {
                            writer.data ("Content-Type: multipart/related;" + data.eol +
                                     "\tboundary=\"" + outerBoundary + "\"" + data.eol +
                                     data.eol);
                        }
                        if (n > 0) {
                            writer.data ("--" + outerBoundary + data.eol);
                            if (n == 2) {
                                writer.data ("Content-Type: multipart/alternative;" + data.eol +
                                         "\tboundary=\"" + innerBoundary + "\"" + data.eol +
                                         data.eol +
                                         "--" + innerBoundary + data.eol);
                            }
                            writer.data ("Content-Type: text/plain; charset=\"" + data.charset + "\"" + data.eol +
                                     "Content-Transfer-Encoding: " + data.encoding + data.eol +
                                     data.eol);
                        }
                        writer.close ("fixdata");
                        writer.close ("prefix");
                        if (n == 2) {
                            writer.opennode ("postfix", "output", text, "pid", "inner");
                            writer.opennode ("fixdata", "valid", "all");
                            writer.data ("--" + innerBoundary + "--" + data.eol +
                                     data.eol);
                            writer.close ("fixdata");
                            writer.close ("postfix");
                        }
                        if (n > 0) {
                            writer.opennode ("postfix", "output", part, "pid", "outer");
                            writer.opennode ("fixdata", "valid", "all");
                            writer.data ("--" + outerBoundary + "--" + data.eol +
                                     data.eol);
                            writer.close ("fixdata");
                            writer.close ("postfix");
                        }
                        writer.opennode ("postfix", "output", allBlocks.totalNumber, "pid", "attach");
                        writer.opennode ("fixdata", "valid","attach");
                        writer.data ("--" + attachBoundary + "--" + data.eol +
                                 data.eol);
                        writer.close ("fixdata");
                        writer.close ("postfix");
                    } else {
                        writer.opennode ("prefix");
                        writer.opennode ("fixdata", "valid", "all");
                        if (n == 1) {
                            writer.data ("--" + outerBoundary + data.eol);
                        } else {
                            writer.data ("--" + innerBoundary + data.eol);
                        }
                        writer.data ("Content-Type: " + b.getContentMime () + "; charset=\"" + data.charset + "\"" + data.eol +
                                 "Content-Transfer-Encoding: " + getTransferEncoding (b) + data.eol +
                                 data.eol);
                        writer.close ("fixdata");
                        writer.close ("prefix");
                    }
                } else {        // offline + attachments
                    writer.opennode ("prefix");
                    if ((b.type == BlockData.ATTACHMENT_TEXT) || (b.type == BlockData.ATTACHMENT_BINARY)) {
                        writer.opennode ("fixdata", "valid", "attach");
                        writer.data ("--" + attachBoundary + data.eol +
                                 "Content-Type: " + b.getContentMime () + data.eol +
                                 "Content-Disposition: attachment; filename=\"" + b.getContentFilename () + "\"" + data.eol +
                                 "Content-Transfer-Encoding: " + getTransferEncoding (b) + data.eol +
                                 data.eol);
                        writer.close ("fixdata");
                    } else {
                        writer.opennode ("fixdata", "valid", "all");
                        writer.data ("--" + outerBoundary + data.eol +
                                 "Content-Type: " + b.getContentMime () + data.eol +
                                 "Content-Transfer-Encoding: " + getTransferEncoding (b) + data.eol +
                                 "Content-Location: " + b.getContentFilename () + data.eol +
                                 data.eol);
                        writer.close ("fixdata");
                    }
                    writer.close ("prefix");
                    if ((b.type == BlockData.ATTACHMENT_TEXT) || (b.type == BlockData.ATTACHMENT_BINARY)) {
                        writer.opennode ("postfix", "output", allBlocks.totalNumber, "pid", "attach");
                        writer.opennode ("fixdata", "valid", "attach");
                        writer.data ("--" + attachBoundary + "--" + data.eol +
                                 data.eol);
                        writer.close ("fixdata");
                        writer.close ("postfix");
                    } else {
                        writer.opennode ("postfix", "output", part, "pid", "outer");
                        writer.opennode ("fixdata", "valid", "all");
                        writer.data ("--" + outerBoundary + "--" + data.eol +
                                 data.eol);
                        writer.close ("fixdata");
                        writer.close ("postfix");
                    }
                }
                writer.close (c);
            }
            writer.close ("type");
        }
        writer.close ("types");
        writer.empty ();

        layout ();

        boolean found;

        found = false;
        for (Enumeration e = tagNames.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();

            if (! found) {
                writer.opennode ("taglist", "count", tagNames.size ());
                found = true;
            }
            writer.openclose ("tag", "name", tag.mTagFullname, "hash", tag.mTagFullname.hashCode ());
        }
        if (found) {
            writer.close ("taglist");
        } else {
            writer.comment ("no taglist");
        }
        writer.empty ();

        found = false;
        for (Enumeration e = tagNames.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();
            String  ttype = tag.getType ();
            String  value;

            switch (tag.tagType) {
            case EMMTag.TAG_INTERNAL:
                if ((tag.fixedValue || tag.globalValue) && ((value = tag.makeInternalValue (data, null)) != null)) {
                    if (! found) {
                        writer.opennode ("global_tags");
                        found = true;
                    }
                    XMLWriter.Creator   c = writer.create ("tag", "name", tag.mTagFullname, "hash", tag.mTagFullname.hashCode ());

                    if (ttype != null) {
                        c.add ("type", ttype);
                    }
                    if (value != null) {
                        writer.opennode (c);
                        writer.data (value);
                        writer.close (c);
                    } else {
                        writer.openclose (c);
                    }
                }
                break;
            }
        }
        if (found) {
            writer.close ("global_tags");
        } else {
            writer.comment ("no global_tags");
        }
        writer.empty ();

        if ((allBlocks.dynContent != null) && (allBlocks.dynContent.ncount > 0)) {
            writer.opennode ("dynamics", "count", allBlocks.dynContent.ncount);
            for (Enumeration e = allBlocks.dynContent.names.elements (); e.hasMoreElements (); ) {
                DynName         dtmp = (DynName) e.nextElement ();
                XMLWriter.Creator   c = writer.create ("dynamic", "id", dtmp.id, "name", dtmp.name);

                getDynamicInfo (dtmp, c);
                writer.opennode (c);
                for (int n = 0; n < dtmp.clen; ++n) {
                    DynCont cont = dtmp.content.elementAt (n);

                    if (cont.targetID != DynCont.MATCH_NEVER) {
                        XMLWriter.Creator   cr = writer.create ("dyncont", "id", cont.id, "order", cont.order);

                        if ((cont.targetID != DynCont.MATCH_ALWAYS) && (cont.condition != null)) {
                            cr.add ("condition", cont.condition);
                        }
                        writer.opennode (cr);
                        if (cont.text != null) {
                            emitBlock (cont.text, 0, 0);
                        }
                        if (cont.html != null) {
                            emitBlock (cont.html, 0, 1);
                        }
                        writer.close (cr);
                    }
                }
                writer.close (c);
            }
            writer.close ("dynamics");
        } else {
            writer.comment ("no dynamics");
        }
        writer.empty ();

        urls ();    
        writer.empty ();

        writer.opennode ("receivers");
    }

    /** Finalize a block
     */
    public void endBlock () throws Exception {
        super.endBlock ();
        if (out != null) {
            writer.close ("receivers");
            writer.close ("blockmail");
            flushBuffer ();
            writer.end ();
            writer = null;
            out.close ();
            out = null;

            if (data.xmlValidate ()) {
                data.logging (Log.INFO, "writer/meta", "Validating XML output");
                startXMLBack (null, "none", pathname);
                data.logging (Log.INFO, "writer/meta", "Validation done");
            } else
                data.logging (Log.INFO, "writer/meta", "Skip validation of XML document");

            if (! (data.isAdminMailing () || data.isTestMailing () || data.isPreviewMailing ()))
                try {
                    FileOutputStream    temp;
                    String          msg;

                    msg = data.company_id + "-" + data.mailing_id + "-" + blockCount + "\t" +
                          "Start: " + startBlockTime + "\tEnd: " + endBlockTime + "\n";
                    temp = new FileOutputStream (fname + ".stamp");
                    temp.write (msg.getBytes ());
                    temp.close ();
                } catch (FileNotFoundException e) {
                    throw new Exception ("Unable to write stamp file " + fname + ".stamp: " + e);
                }
        }
    }

    /** Create string for media informations
     * @param cinfo information about this customer
     * @return the media string
     */
    public void getMediaInformation (Object cinfop, XMLWriter.Creator c) {
        Custinfo cinfo = (Custinfo) cinfop;

        if (cinfo.email != null) {
            c.add ("to_email", cinfo.email);
        }
    }

    /** Write a single receiver record
     * @param cinfo Information about the customer
     * @param mcount if more than one mail is written for this receiver
     * @param mailtype the mailtype for this receiver
     * @param icustomer_id the customer ID
     * @param tag_names the available tags
     * @param urlMaker to create the URLs
     */
    public void writeMail (Custinfo cinfo,
                   int mcount, int mailtype, long icustomer_id,
                   String mediatypes, Hashtable tag_names, URLMaker urlMaker) throws Exception {
        super.writeMail (cinfo, mcount, mailtype, icustomer_id, mediatypes, tag_names, urlMaker);
        if ((mailCount % flushCount) == 0) {
            flushBuffer ();
        }
        if ((mailCount % 100) == 0) {
            data.logging (Log.VERBOSE, "writer/meta", "Currently at " + mailCount + " mails (in block " + blockCount + ": " + inBlockCount + ") ");
        }
        if (billingCounter != null)
            if ((logSize > 0) && ((mailCount % logSize) == 0))
                billingCounter.update_log (mailCount);

        XMLWriter.Creator   c = writer.create ("receiver", "customer_id", icustomer_id, "user_type", cinfo.usertype);

        getMediaInformation (cinfo, c);
        c.add ("message_id", messageID, "mailtype", mailtype);

        if (mediatypes != null) {
            c.add ("mediatypes", mediatypes);
        }
        writer.opennode (c);
        writer.opennode ("tags");

        for (Enumeration e = tag_names.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();
            String  value;

            switch (tag.tagType) {
            case EMMTag.TAG_DBASE:
                if (tag.mutableValue)
                    value = tag.makeMutableValue (data, cinfo);
                else if (! (tag.fixedValue || tag.globalValue))
                    value = tag.mTagValue;
                else
                    value = null;
                break;
            case EMMTag.TAG_URL:
                value = allBlocks.create_url_tag (tag, urlMaker);
                break;
            case EMMTag.TAG_INTERNAL:
                if (! (tag.fixedValue || tag.globalValue))
                    value = tag.makeInternalValue (data, cinfo);
                else
                    value = null;
                break;
            default:
                throw new Exception ("Invalid tag type: " + tag.toString ());
            }
            if (value != null) {
                writer.opennode ("tag", "name", tag.mTagFullname, "hash", tag.mTagFullname.hashCode ());
                writer.data (value);
                writer.close ("tag");
            }
        }
        writer.close ("tags");

        if ((data.urlcount > 0) && (data.generateCodedURLs ())) {
            for (int n = 0; n < data.urlcount; ++n) {
                URL url = data.URLlist.elementAt (n);

                writer.openclose ("codedurl", "id", url.id, "destination", urlMaker.autoURL (url.id));
            }
        }
        if (data.lusecount > 0) {
            for (int n = 0; n < data.lcount; ++n) {
                if (data.columnUse (n)) {
                    if (data.columnIsNull (n)) {
                        writer.opennode ("data", "null", true);
                    } else {
                        writer.opennode ("data");
                    }
                    writer.data (data.columnGetStr (n));
                    writer.close ("data");
                }
            }
        }
        writer.close (c);

        if (billingCounter != null)
            if (icustomer_id != 0)
                billingCounter.sadd (mailtype);
        writeMailDone ();
    }
}
