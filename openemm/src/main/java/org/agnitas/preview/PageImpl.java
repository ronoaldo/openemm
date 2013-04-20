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
package org.agnitas.preview;

import  java.util.Arrays;
import  java.util.ArrayList;
import  java.util.Hashtable;
import  java.util.Enumeration;
import  java.util.regex.Pattern;
import  java.util.regex.Matcher;

public class PageImpl implements Page {
    private Hashtable <String, String>  content;
    private Hashtable <String, String[]>    header;
    private StringBuffer            error;
    private Hashtable <String, Object>  compat;

    public PageImpl () {
        content = new Hashtable <String, String> ();
        header = null;
        error = null;
        compat = null;
    }

    public void addContent (String key, String value) {
        content.put (key, value);
    }

    public void setError (String msg) {
        if (error == null) {
            error = new StringBuffer ();
        }
        error.append (msg);
        error.append ('\n');
    }

    public String getError () {
        return error != null ? error.toString () : null;
    }

    public Hashtable <String, Object> compatibilityRepresentation () {
        if (compat == null) {
            compat = new Hashtable <String, Object> ();
            if (content != null) {
                for (Enumeration e = content.keys (); e.hasMoreElements (); ) {
                    String  key = (String) e.nextElement ();

                    compat.put (key, content.get (key));
                }
            }
            if (error != null) {
                compat.put (Preview.ID_ERROR, error.toString ());
            }
        }
        return compat;
    }

    /** Pattern to find entities to escape */
    static private Pattern      textReplace = Pattern.compile ("[&<>'\"]");
    /** Values to escape found entities */
    static private Hashtable <String, String>
                    textReplacement = new Hashtable <String, String> ();
    static {
        textReplacement.put ("&", "&amp;");
        textReplacement.put ("<", "&lt;");
        textReplacement.put (">", "&gt;");
        textReplacement.put ("'", "&apos;");
        textReplacement.put ("\"", "&quot;");
    }
    /** escapeEntities
     * This method escapes the HTML entities to be displayed
     * in a HTML context
     * @param s the input string
     * @return null, if input string had been null,
     *         the escaped version of s otherwise
     */
    private String escapeEntities (String s) {
        if (s != null) {
            int     slen = s.length ();
            Matcher     m = textReplace.matcher (s);
            StringBuffer    buf = new StringBuffer (slen + 128);
            int     pos = 0;

            while (m.find (pos)) {
                int next = m.start ();
                String  ch = m.group ();

                if (pos < next)
                    buf.append (s.substring (pos, next));
                buf.append (textReplacement.get (ch));
                pos = m.end ();
            }
            if (pos != 0) {
                if (pos < slen)
                    buf.append (s.substring (pos));
                s = buf.toString ();
            }
        }
        return s;
    }

    /** encode
     * Encodes a string to a byte stream using the given character set,
     * if escape is true, HTML entities are escaped prior to encoding
     * @param s the string to encode
     * @param charset the character set to convert the string to
     * @param escape if HTML entities should be escaped
     * @return the coded string as a byte stream
     */
    private byte[] encode (String s, String charset, boolean escape) {
        if (escape && (s != null)) {
            s = "<pre>\n" + escapeEntities (s) + "</pre>\n";
        }
        try {
            return s == null ? null : s.getBytes (charset);
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
    }

    /** get
     * a null input save conversion variant
     * @param s the input string
     * @param escape to escape HTML entities
     * @return the converted string
     */
    private String convert (String s, boolean escape) {
        if (escape && (s != null)) {
            return escapeEntities (s);
        }
        return s;
    }

    protected String strip (String html) {
        return null;
    }

    /**
     * Get header-, text- or HTML-part from hashtable created by
     * createPreview as byte stream
     */
    public byte[] getPartByID (String id, String charset, boolean escape) {
        return encode (content.get (id), charset, escape);
    }
    public byte[] getHeaderPart (String charset, boolean escape) {
        return getPartByID (Preview.ID_HEAD, charset, escape);
    }
    public byte[] getHeaderPart (String charset) {
        return getHeaderPart (charset, false);
    }
    public byte[] getTextPart (String charset, boolean escape) {
        return getPartByID (Preview.ID_TEXT, charset, escape);
    }
    public byte[] getTextPart (String charset) {
        return getTextPart (charset, false);
    }
    public byte[] getHTMLPart (String charset, boolean escape) {
        return getPartByID (Preview.ID_HTML, charset, escape);
    }
    public byte[] getHTMLPart (String charset) {
        return getHTMLPart (charset, false);
    }
    public byte[] getMHTMLPart (String charset, boolean escape) {
        return getPartByID (Preview.ID_MHTML, charset, escape);
    }
    public byte[] getMHTMLPart (String charset) {
        return getMHTMLPart (charset, false);
    }

    /**
     * Get header-, text- or HTML-part as strings
     */
    public String getByID (String id, boolean escape) {
        return convert (content.get (id), escape);
    }
    public String getStrippedByID (String id, boolean escape) {
        return convert (strip (content.get (id)), escape);
    }

    public String getHeader (boolean escape) {
        return getByID (Preview.ID_HEAD, escape);
    }
    public String getHeader () {
        return getHeader (false);
    }

    public String getText (boolean escape) {
        return getByID (Preview.ID_TEXT, escape);
    }
    public String getText () {
        return getText (false);
    }

    public String getHTML (boolean escape) {
        return getByID (Preview.ID_HTML, escape);
    }
    public String getHTML () {
        return getHTML (false);
    }
    public String getStrippedHTML (boolean escape) {
        return getStrippedByID (Preview.ID_HTML, escape);
    }
    public String getStrippedHTML () {
        return getStrippedHTML (false);
    }

    public String getMHTML (boolean escape) {
        return getByID (Preview.ID_MHTML, escape);
    }
    public String getMHTML () {
        return getMHTML (false);
    }
    public String getStrippedMHTML (boolean escape) {
        return getStrippedByID (Preview.ID_MHTML, escape);
    }
    public String getStrippedMHTML () {
        return getStrippedMHTML (false);
    }

    /**
     * Get attachment names and content
     */
    private boolean isID (String name) {
        return name.startsWith ("__") && name.endsWith ("__");
    }
    private String[] getList (boolean asAttachemnts) {
        ArrayList <String>  collect = new ArrayList <String> ();
        
        for (String name : content.keySet ()) {
            if (isID (name) != asAttachemnts) {
                collect.add (name);
            }
        }
        return collect.toArray (new String[collect.size ()]);
    }
    public String[] getIDs () {
        return getList (false);
    }
    public String[] getAttachmentNames () {
        return getList (true);
    }

    public byte[] getAttachment (String name) {
        if (isID (name) || (! content.containsKey (name))) {
            return null;
        }

        byte[]  rc = null;
        String  coded = content.get (name);

        if (coded != null) {
            String  valid = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
            byte[]  temp = new byte[coded.length ()];
            int tlen = 0;
            long    val;
            int count;
            int pad;
            byte    pos;

            val = 0;
            count = 0;
            pad = 0;
            for (int n = 0; n < coded.length (); ++n) {
                char    ch = coded.charAt (n);

                if (ch == '=') {
                    ++pad;
                    ++count;
                } else if ((pos = (byte) valid.indexOf (ch)) != -1) {
                    switch (count++) {
                    case 0:
                        val = pos << 18;
                        break;
                    case 1:
                        val |= pos << 12;
                        break;
                    case 2:
                        val |= pos << 6;
                        break;
                    case 3:
                        val |= pos;
                        break;
                    }
                }
                if (count == 4) {
                    temp[tlen] = (byte) ((val >> 16) & 0xff);
                    temp[tlen + 1] = (byte) ((val >> 8) & 0xff);
                    temp[tlen + 2] = (byte) (val & 0xff);
                    tlen += 3 - pad;
                    count = 0;
                    if (pad > 0)
                        break;
                }
            }
            rc = Arrays.copyOf (temp, tlen);
        }
        return rc;
    }

    private synchronized void parseHeader () {
        if (header == null) {
            String  head = content.get (Preview.ID_HEAD);

            header = new Hashtable <String, String[]> ();
            if (head != null) {
                String[]    lines = head.split ("\r?\n");
                String      cur = null;

                for (int n = 0; n <= lines.length; ++n) {
                    String  line = (n < lines.length ? lines[n] : null);

                    if ((line == null) || ((line.indexOf (' ') != 0) && (line.indexOf ('\t') != 0))) {
                        if (cur != null) {
                            String[]    parsed = cur.split (": +", 2);

                            if (parsed.length == 2) {
                                String      key = parsed[0].toLowerCase ();
                                String[]    field = header.get (key);
                                int     nlen = (field == null ? 1 : field.length + 1);
                                String[]    nfield = new String[nlen];

                                if (field != null)
                                    for (int m = 0; m < field.length; ++m)
                                        nfield[m] = field[m];
                                nfield[nlen - 1] = parsed[1];
                                header.put (key, nfield);
                            }
                        }
                        cur = line;
                    } else if (cur != null) {
                        cur += '\n' + line;
                    }
                }
            }
        }
    }

    /**
     * Get individual lines from the header
     */
    public String[] getHeaderFields (String field) {
        parseHeader ();
        return  header.get (field.toLowerCase ());
    }

    public String getHeaderField (String field, boolean escape) {
        String      rc = null;
        String[]    head = getHeaderFields (field);

        if ((head != null) && (head.length > 0)) {
            rc = escape ? escapeEntities (head[0]) : head[0];
        }
        return rc;
    }

    public String getHeaderField (String field) {
        return getHeaderField (field, false);
    }
}
