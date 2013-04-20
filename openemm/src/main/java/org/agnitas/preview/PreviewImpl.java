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
import  java.util.ResourceBundle;
import  java.util.Set;
import  java.util.StringTokenizer;
import  java.util.regex.Pattern;
import  java.util.regex.Matcher;
import  java.security.MessageDigest;
import  org.agnitas.backend.MailgunImpl;
import  org.agnitas.util.Log;

public class PreviewImpl implements Preview {
    /** PCache (Page Cache)
     * This class is used to cache full generated pages for a single
     * customer
     */
    class PCache {
        class PEntry {
            protected long  timestamp;
            protected Page  page;

            protected PEntry (long nTimestamp, Page nPage) {
                timestamp = nTimestamp;
                page = nPage;
            }
        }
        private int     maxAge;
        private int     maxEntries;
        private int     size;
        private Hashtable <String, PEntry>
                    cache;
        private Log     logger;
        private String      logid;

        protected PCache (int nMaxAge, int nMaxEntries) {
            maxAge = nMaxAge;
            maxEntries = nMaxEntries;
            size = 0;
            cache = new Hashtable <String, PEntry> ();
            logger = null;
            logid = "cache";
        }

        protected void done () {
            cache.clear ();
            size = 0;
        }

        protected void setLogger (Log nLogger, String nLogid) {
            logger = nLogger;
            if (nLogid != null) {
                logid = nLogid;
            }
        }

        private void log (int level, String msg) {
            if (logger != null) {
                logger.out (level, logid, msg);
            }
        }

        protected Page find (long mailingID, long customerID, String selector, long now) {
            String      key = mkKey (mailingID, customerID, selector);
            PEntry      ent = cache.get (key);
            Page        rc = null;

            if (ent != null) {
                log (Log.DEBUG, "Found entry for key \"" + key + "\"");
                if (ent.timestamp + maxAge < now) {
                    cache.remove (ent);
                    --size;
                    log (Log.DEBUG, "Entry is too old, remove it from cache, remaining cachesize is " + size);
                } else {
                    rc = ent.page;
                }
            } else {
                log (Log.DEBUG, "No page in cache found for key \"" + key + "\"");
            }
            return rc;
        }

        protected void store (long mailingID, long customerID, String selector, long now, Page page) {
            String      key = mkKey (mailingID, customerID, selector);
            PEntry      ent;

            while (size + 1 > maxEntries) {
                PEntry  cur = null;

                for (Enumeration e = cache.elements (); e.hasMoreElements (); ) {
                    PEntry  chk = (PEntry) e.nextElement ();

                    if ((cur == null) || (cur.timestamp > chk.timestamp))
                        cur = chk;
                }
                if (cur != null) {
                    log (Log.DEBUG, "Shrink cache as there are currently " + size + " of " + maxEntries + " possible cache elements");
                    cache.remove (cur);
                    --size;
                } else {
                    log (Log.DEBUG, "Failed shrinking cache, even it has " + size + " of " + maxEntries + " elements");
                    break;
                }
            }
            ent = new PEntry (now, page);
            cache.put (key, ent);
            ++size;
            log (Log.DEBUG, "Store page for key \"" + key + "\" in cache, cache has now " + size + " elements");
        }

        protected int getSize () {
            return size;
        }

        private String mkKey (long mailingID, long customerID, String selector) {
            return "[" + mailingID + "/" + customerID + "]" + (selector == null ? "" : ":" + selector);
        }
    }
    /** limited list for caching mailings */
    private Cache   mhead, mtail;
    /** max age in seconds for an entry in the cache */
    private int maxAge;
    /** max number of entries in the cache */
    private int maxEntries;
    /** current number of entries */
    private int msize;
    /** cache for generated pages */
    private PCache  pcache;
    /** cache for generated anon pages */
    private PCache  acache;
    /** last statistics report */
    private long    lastrep;
    /** logger */
    protected Log   log;

    /**
     * converts a string to an interger, using a default value
     * on errors or unset input
     *
     * @param s the string to convert
     * @param dflt the default, if string is unset or unparsable
     * @return the integer for the input string
     */
    protected int atoi (String s, int dflt) {
        int rc;

        if (s == null)
            rc = dflt;
        else
            try {
                rc = Integer.parseInt (s);
            } catch (NumberFormatException e) {
                rc = dflt;
            }
        return rc;
    }

    /**
     * converts a string to a boolean, using a default value
     * on unset input
     *
     * @param s the string to convert
     * @param dflt the default, if string is unset
     * @return the integer for the input string
     */
    protected boolean atob (String s, boolean dflt) {
        boolean rc;

        if (s == null)
            rc = dflt;
        else
            if ((s.length () == 0) || s.equalsIgnoreCase ("true") || s.equalsIgnoreCase ("on"))
                rc = true;
            else {
                char    ch = s.charAt (0);

                rc = ((ch == 'T') || (ch == 't') || (ch == 'Y') || (ch == 'y') || (ch == '1') || (ch == '+'));
            }
        return rc;
    }

    /** getRsc
     * retreives a value from resource bundle, if available
     * @param rsc the resource bundle
     * @param keys the keys in this bundle
     * @param key the key of the value to retreive
     * @return the value, if available, otherwise null
     */
    protected String getRsc (ResourceBundle rsc, Set <String> keys, String key) {
        return keys.contains (key) ? rsc.getString (key) : null;
    }

    protected void setFromResource (ResourceBundle rsc, Set <String> keys) {
    }

    /** PreviewImpl
     * the constructor reading the configuration
     * from emm.properties
     */
    public PreviewImpl () {
        String  age = null;
        String  size = null;
        String  pcage = null;
        String  pcsize = null;
        String  acage = null;
        String  acsize = null;
        String  logname = null;
        String  loglevel = null;
        try {
            ResourceBundle  rsc;
            Set <String>    keys;

            rsc = ResourceBundle.getBundle ("emm");
            if (rsc != null) {
                keys = rsc.keySet ();
                age = getRsc (rsc, keys, "preview.mailgun.cache.age");
                size = getRsc (rsc, keys, "preview.mailgun.cache.size");
                pcage = getRsc (rsc, keys, "preview.page.cache.age");
                pcsize = getRsc (rsc, keys, "preview.page.cache.size");
                acage = getRsc (rsc, keys, "preview.anon.cache.age");
                acsize = getRsc (rsc, keys, "preview.anon.cache.size");
                logname = getRsc (rsc, keys, "preview.logname");
                loglevel = getRsc (rsc, keys, "preview.loglevel");
                setFromResource (rsc, keys);
            }
        } catch (Exception e) {
        }
        mhead = null;
        mtail = null;
        maxAge = atoi (age, 300);
        maxEntries = atoi (size, 20);
        msize = 0;
        pcache = new PCache (atoi (pcage, 120), atoi (pcsize, 50));
        acache = new PCache (atoi (acage, 120), atoi (acsize, 250));

        if (logname == null) {
            logname = "preview";
        }
        int level;
        if (loglevel == null)
            level = Log.INFO;
        else
            try {
                level = Log.matchLevel (loglevel);
            } catch (NumberFormatException e) {
                level = Log.INFO;
            }
        lastrep = 0;
        log = new Log (logname, level);
        pcache.setLogger (log, "view-cache");
        acache.setLogger (log, "anon-cache");
    }

    /** done
     * CLeanup code
     */
    public void done () {
        Cache   temp;
        int count;

        count = 0;
        while (mhead != null) {
            temp = mhead;
            mhead = mhead.next;
            try {
                temp.release ();
            } catch (Exception e) {
                log.out (Log.ERROR, "done", "Failed releasing cache: " + e.toString ());
            }
            ++count;
        }
        log.out (Log.DEBUG, "done", "Released " + count + " mailgun cache entries of expected " + msize);
        mhead = null;
        mtail = null;
        msize = 0;
        pcache.done ();
        acache.done ();
    }


    public int getMaxAge () {
        return maxAge;
    }

    public void setMaxAge (int nMaxAge) {
        maxAge = nMaxAge;
    }

    public int getMaxEntries () {
        return maxEntries;
    }

    public synchronized void setMaxEntries (int nMaxEntries) {
        if (nMaxEntries >= 0) {
            maxEntries = nMaxEntries;
            while (msize > maxEntries) {
                Cache   c = pop ();

                log.out (Log.DEBUG, "max", "Reduce entries, currently " + msize + " in cache, new max value is " + maxEntries);
                try {
                    c.release ();
                } catch (Exception e) {
                    log.out (Log.ERROR, "max", "Failed releasing cache: " + e.toString ());
                }
            }
        }
    }
    
    public boolean shallCreateAll () {
        return false;
    }

    /** mkMailgun
     * Creates a new instance for a mailgun
     * @return the new instance
     */
    public Object mkMailgun () throws Exception {
        return new MailgunImpl ();
    }
    public Page mkPage () {
        return (Page) new PageImpl ();
    }

    /**
     * create an ID for a optioanl given text
     * @param text the text
     * @return id part of the text
     */
    private String makeTextID (String text) {
        String  rc;

        if (text.length () < 32) {
            rc = text;
        } else {
            try {
                MessageDigest   md = MessageDigest.getInstance ("MD5");
                byte[]      digest;
                StringBuffer    buf;
                String[]    hd = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

                md.update (text.getBytes ("UTF8"));
                digest = md.digest ();
                buf = new StringBuffer (md.getDigestLength ());
                for (int n = 0; n < digest.length; ++n) {
                    buf.append (hd[(digest[n] >> 4) & 0xf]);
                    buf.append (hd[digest[n] & 0xf]);
                }
                rc = buf.toString ();
            } catch (Exception e) {
                rc = text;
            }
        }
        return rc;
    }

    /** makePreview
     * The main entrance for this class, a preview for all
     * parts of the mail is generated into a hashtable for
     * the given mailing and customer. If cachable is set
     * to true, the result is cached for speed up future
     * access.
     * @param mailingID the mailing-id to create the preview for
     * @param customerID the customer-id to create the preview for
     * @param selector optional selector for selecting different version of cached page
     * @param anon if we should anonymize the result
     * @param convertEntities replace non ascii characters by ther HTML entity representation
     * @param legacyUIDs if set we should stick to legacy UIDs
     * @param createAll if set create all displayable parts of the mailing
     * @param cachable if the result should be cached
     * @return the preview
     */
    public Page makePreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean convertEntities, boolean legacyUIDs, boolean createAll, boolean cachable) {
        long        now;
        String      lid;
        String      error;
        PCache      pc;
        Cache       c;
        Page        rc;

        now = System.currentTimeMillis () / 1000;
        lid = "[" + mailingID + "/" + customerID +
                (convertEntities ? "&" : "") +
                (legacyUIDs ? "^" : "") +
                (createAll ? "*" : "") + 
                (selector == null ? "" : ":" + selector) +
              "]" + (text == null ? "" : ", " + makeTextID (text));
        error = null;
        pc = anon ? acache : pcache;
        if (cachable) synchronized (pc) {
            if (lastrep + 3600 < now) {
                log.out (Log.INFO, "stat", "Mailing cache: " + msize + ", Page cache: " + pcache.getSize () + ", Anon cache: " + acache.getSize ());
                lastrep = now;
            }
            rc = pc.find (mailingID, customerID, selector, now);
            if (rc == null) {
                if (text == null) {
                    for (c = mhead; c != null; c = c.next)
                        if (c.mailingID == mailingID)
                            break;
                    if (c != null) {
                        pop (c);
                        if (c.ctime + maxAge < now) {
                            log.out (Log.DEBUG, "create", "Found entry for " + mailingID + "/" + customerID + " in mailgun cache, but it is expired");
                            try {
                                c.release ();
                                c = null;
                            } catch (Exception e) {                         ;
                                log.out (Log.ERROR, "create", "Failed releasing cache: " + e.toString ());
                            }
                        } else {
                            log.out (Log.DEBUG, "create", "Found entry for " + mailingID + "/" + customerID + " in mailgun cache");
                            push (c);
                        }
                    }
                    if (c == null) {
                        try {
                            c = new Cache (mailingID, now, null, createAll, this);
                            push (c);
                            log.out (Log.DEBUG, "create", "Created new mailgun cache entry for " + mailingID + "/" + customerID);
                        } catch (Exception e) {
                            c = null;
                            error = getErrorMessage(e);
                            log.out (Log.ERROR, "create", "Failed to create new mailgun cache entry for " + mailingID + "/" + customerID + ": " + error);
                        }
                    }
                    if (c != null) {
                        try {
                            rc = c.makePreview (customerID, selector, anon, convertEntities, legacyUIDs, cachable, this);
                            log.out (Log.DEBUG, "create", "Created new page for " + lid);
                        } catch (Exception e) {
                            error = getErrorMessage(e);
                            log.out (Log.ERROR, "create", "Failed to create preview for " + lid + ": " + error);
                        }
                    }
                } else {
                    c = null;
                    try {
                        c = new Cache (mailingID, now, text, createAll, this);
                        rc = c.makePreview (customerID, selector, anon, convertEntities, legacyUIDs, cachable, this);
                        c.release ();
                    } catch (Exception e) {
                        error = getErrorMessage(e);
                        log.out (Log.ERROR, "create", "Failed to create custom text preview for " + lid + ": " + error);
                    }
                }
                if ((error == null) && (rc != null)) {
                    pc.store (mailingID, customerID, selector, now, rc);
                }
            } else {
                log.out (Log.DEBUG, "create", "Found page in page cache for " + lid);
            }
        } else {
            rc = null;
            try {
                c = new Cache (mailingID, now, text, createAll, this);
                rc = c.makePreview (customerID, selector, anon, convertEntities, legacyUIDs, cachable, this);
                c.release ();
                log.out (Log.DEBUG, "create", "Created uncached preview for " + lid);
            } catch (Exception e) {
                error = getErrorMessage(e);
                log.out (Log.ERROR, "create", "Failed to create uncached preview for " + lid + ": " + error);
            }
        }
        if (error != null) {
            if (rc == null) {
                rc = mkPage ();
            }
            rc.setError (error);
        }
        error = rc.getError ();
        if (error != null)
            log.out (Log.INFO, "create", "Found error for " + mailingID + "/" + customerID + ": " + error);
        return rc;
    }
    private String getErrorMessage(Exception e) {
        StringBuffer sb = new StringBuffer(e.toString());
        Throwable t = e;
        while (t != null) {
            StackTraceElement[] stackTrace = t.getStackTrace();
            if (stackTrace != null) {
                for (int i = 0; i < stackTrace.length; i++) {
                    sb.append("\n\tat ");
                    sb.append(stackTrace[i].toString());
                }
            }
            t = t.getCause();
            if (t!= null) {
                sb.append("\nCaused by: " + t + "\n");
            }
        }
        return sb.toString();
    }
    public Page makePreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean convertEntities, boolean legacyUIDs, boolean cachable) {
        return makePreview (mailingID, customerID, selector, text, anon, convertEntities, legacyUIDs, shallCreateAll (), cachable);
    }
    public Page makePreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean cachable) {
        return makePreview (mailingID, customerID, selector, text, anon, false, false, cachable);
    }
    public Page makePreview (long mailingID, long customerID, String selector, boolean anon, boolean cachable) {
        return makePreview (mailingID, customerID, selector, null, anon, false, false, cachable);
    }
    public Page makePreview (long mailingID, long customerID, boolean cachable) {
        return makePreview (mailingID, customerID, null, null, false, false, false, cachable);
    }
    /* Wrapper for heatmap generation
     * @param mailingID the mailing to generate the heatmap for
     * @param customerID the customerID to generate the heatmap for
     * @return the preview
     */
    public String makePreviewForHeatmap (long mailingID, long customerID) {
        Page page = makePreview (mailingID, customerID, null, null, false, false, true, false, false);

        return page != null ? page.getHTML () : null;
    }

    private Cache pop (Cache c) {
        if (c != null) {
            if (c.next != null) {
                c.next.prev = c.prev;
            } else {
                mtail = c.prev;
            }
            if (c.prev != null) {
                c.prev.next = c.next;
            } else {
                mhead = c.next;
            }
            c.next = null;
            c.prev = null;
            --msize;
        }
        return c;
    }

    private Cache pop () {
        Cache   rc;

        rc = mtail;
        if (rc != null) {
            mtail = mtail.prev;
            if (mtail != null) {
                mtail.next = null;
            } else {
                mhead = null;
            }
            --msize;
            rc.next = null;
            rc.prev = null;
        }
        return rc;
    }

    private void push (Cache c) {
        if (msize >= maxEntries) {
            Cache   tmp = pop ();

            if (tmp != null) {
                if (tmp == c) {
                    log.out (Log.ERROR, "push", "Try to release pushed cache");
                } else {
                    try {
                        tmp.release ();
                    } catch (Exception e) {
                        log.out (Log.ERROR, "push", "Failed releasing cache: " + e.toString ());
                    }
                }
                --msize;
            }
        }
        c.next = mhead;
        c.prev = null;
        if (mhead != null) {
            mhead.prev = c;
        }
        mhead = c;
        ++msize;
    }

    /******************** deprecated part ********************/
    @Deprecated
    public Hashtable <String, Object> createPreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean convertEntities, boolean legacyUIDs, boolean createAll, boolean cachable) {
        Page    p = makePreview (mailingID, customerID, selector, text, anon, convertEntities, legacyUIDs, createAll, cachable);

        return p != null ? p.compatibilityRepresentation () : null;
    }
    @Deprecated
    public Hashtable <String, Object> createPreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean convertEntities, boolean legacyUIDs, boolean cachable) {
        return createPreview (mailingID, customerID, selector, text, anon, convertEntities, legacyUIDs, shallCreateAll (), cachable);
    }
    @Deprecated
    public Hashtable <String, Object> createPreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean cachable) {
        return createPreview (mailingID, customerID, selector, text, anon, false, false, cachable);
    }
    @Deprecated
    public Hashtable <String, Object> createPreview (long mailingID, long customerID, String selector, boolean anon, boolean cachable) {
        return createPreview (mailingID, customerID, selector, null, anon, false, false, cachable);
    }
    @Deprecated
    public Hashtable <String, Object> createPreview (long mailingID, long customerID, boolean cachable) {
        return createPreview (mailingID, customerID, null, null, false, false, false, cachable);
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

    /**
     * Get header-, text- or HTML-part from hashtable created by
     * createPreview as byte stream
     */
    @Deprecated
    public byte[] getHeaderPart (Hashtable <String, Object> output, String charset, boolean escape) {
        return encode ((String) output.get (ID_HEAD), charset, escape);
    }
    @Deprecated
    public byte[] getHeaderPart (Hashtable <String, Object> output, String charset) {
        return getHeaderPart (output, charset, false);
    }

    @Deprecated
    public byte[] getTextPart (Hashtable <String, Object> output, String charset, boolean escape) {
        return encode ((String) output.get (ID_TEXT), charset, escape);
    }
    @Deprecated
    public byte[] getTextPart (Hashtable <String, Object> output, String charset) {
        return getTextPart (output, charset, false);
    }

    @Deprecated
    public byte[] getHTMLPart (Hashtable <String, Object> output, String charset, boolean escape) {
        return encode ((String) output.get (ID_HTML), charset, escape);
    }
    @Deprecated
    public byte[] getHTMLPart (Hashtable <String, Object> output, String charset) {
        return getHTMLPart (output, charset, false);
    }

    /**
     * Get header-, text- or HTML-part as strings
     */
    @Deprecated
    public String getHeader (Hashtable <String, Object> output, boolean escape) {
        return convert ((String) output.get (ID_HEAD), escape);
    }
    @Deprecated
    public String getHeader (Hashtable <String, Object> output) {
        return getHeader (output, false);
    }

    @Deprecated
    public String getText (Hashtable <String, Object> output, boolean escape) {
        return convert ((String) output.get (ID_TEXT), escape);
    }
    @Deprecated
    public String getText (Hashtable <String, Object> output) {
        return getText (output, false);
    }

    @Deprecated
    public String getHTML (Hashtable <String, Object> output, boolean escape) {
        return convert ((String) output.get (ID_HTML), escape);
    }
    @Deprecated
    public String getHTML (Hashtable <String, Object> output) {
        return getHTML (output, false);
    }

    /**
     * Get attachment names and content
     */
    private boolean isAttachment (String name) {
        return (! name.startsWith ("__")) && (! name.endsWith ("__"));
    }
    @Deprecated
    public String[] getAttachmentNames (Hashtable <String, Object> output) {
        ArrayList <String>  collect = new ArrayList <String> ();

        for (Enumeration <String> e = output.keys (); e.hasMoreElements (); ) {
            String  name = e.nextElement ();

            if (isAttachment (name)) {
                collect.add (name);
            }
        }
        return collect.toArray (new String[collect.size ()]);
    }

    @Deprecated
    public byte[] getAttachment (Hashtable <String, Object> output, String name) {
        if ((! isAttachment (name)) || (! output.containsKey (name))) {
            return null;
        }

        byte[]  rc = null;
        String  coded = (String) output.get (name);

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

    /**
     * Get individual lines from the header
     */
    @Deprecated
    @SuppressWarnings ("unchecked")
    public String[] getHeaderField (Hashtable <String, Object> output, String field) {
        String[]    rc = null;

        synchronized (output) {
            Hashtable <String, String[]>
                    header = (Hashtable <String, String[]>) output.get (ID_HDETAIL);

            if (header == null) {
                String  head = (String) output.get (ID_HEAD);

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
                                    String[]    content = header.get (key);
                                    int     nlen = (content == null ? 1 : content.length + 1);
                                    String[]    ncontent = new String[nlen];

                                    if (content != null)
                                        for (int m = 0; m < content.length; ++m)
                                            ncontent[m] = content[m];
                                    ncontent[nlen - 1] = parsed[1];
                                    header.put (key, ncontent);
                                }
                            }
                            cur = line;
                        } else if (cur != null) {
                            cur += '\n' + line;
                        }
                    }
                }
                output.put (ID_HDETAIL, header);
            }
            rc = header.get (field.toLowerCase ());
        }
        return rc;
    }
    @Deprecated
    public String getPartOfHeader (Hashtable <String, Object> output, boolean escape, String headerKeyword) {
        String      rc = null;
        String[]    head = getHeaderField (output, headerKeyword);

        if ((head != null) && (head.length > 0)) {
            rc = escape ? escapeEntities (head[0]) : head[0];
        }
        return rc;
    }

    // well, we could create a global Hashmap containing all the values for this preview
    // but the part-Method is called not very often, so its more efficient to parse
    // the header if we need it.
    // As parameter give the "Keyword" you will get then the appropriate return String.
    // Possible Values for the Header are:
    // "Return-Path", "Received", "Message-ID", "Date", "From", "To", "Subject", "X-Mailer", "MIME-Version"
    // warning! We do a "startswith" comparison, that means, if you give "Re" as parameter, you will
    // get either "Return-Path" or "Received", depending on what comes at last.
    @Deprecated
    public String getPartOfHeader(Hashtable <String, Object> output, String charset, boolean forHTML, String headerKeyword) {
       String returnString = null;
       String tmpLine = null;
       // use just \n as line delimiter. Warning, if you use Windows, that will not work...
       StringTokenizer st = new StringTokenizer( new String(getHeaderPart(output, charset, forHTML)) , "\n");
       while (st.hasMoreElements() ) {
           // get next line and cut the leading and trailing whitespaces of.
           tmpLine = ((String) st.nextElement()).trim();
           // convert Header String to lower and compare with lower-case given String
           if (tmpLine.toLowerCase().startsWith(headerKeyword.toLowerCase())) {
               // get index of first :
               int endOfHeaderKeyword = tmpLine.indexOf(':') +1;
               // return everything from first ":" and remove trailing whitespaces..
               returnString = tmpLine.substring(endOfHeaderKeyword).trim();
           }
       }
       return returnString;
    }
}
