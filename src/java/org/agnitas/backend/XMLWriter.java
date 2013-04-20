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

import  java.io.OutputStream;
import  java.io.IOException;
import  java.util.ArrayList;
import  java.util.regex.Pattern;
import  java.util.regex.Matcher;

public class XMLWriter {
    class Entity {
        String  name;
        Entity  parent;
        
        protected Entity (String nName, Entity nParent) {
            name = nName;
            parent = nParent;
        }
    }
    public class Creator {
        String          name;
        ArrayList <String>  vars;
        ArrayList <String>  vals;
        private int     count;
        
        protected Creator (String nName) {
            name = nName;
            vars = new ArrayList <String> ();
            vals = new ArrayList <String> ();
            count = 0;
        }
        
        public void add (String var, Object val) {
            vars.add (var);
            vals.add (val.toString ());
            ++count;
        }
        
        public void add (Object ... param) {
            int plen = param.length;
        
            for (int n = 0; n < plen; n += 2) {
                vars.add (param[n].toString ());
                vals.add (param[n + 1].toString ());
            }
        }
        
        public String[] getVariables () {
            return vars.toArray (new String[count]);
        }
        
        public String[] getValues () {
            return vals.toArray (new String[count]);
        }
    }
        
    
    private StringBuffer    buf;
    private OutputStream    out;
    private Entity      entity;
    private int     depth;
    private boolean     indentNext;

    private void indent () {
        if (indentNext) {
            buf.append ('\n');
            for (int n = 0; n < depth; ++n) {
                buf.append ("  ");
            }
        } else {
            indentNext = true;
        }
    }

    private static Pattern  escaper = Pattern.compile ("[<>&'\"\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u000b\u000c\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f]");
    private void escape (String s) {
        if (s != null) {
            Matcher m = escaper.matcher (s);
            int last;
        
            last = 0;
            while (m.find ()) {
                int pos = m.start ();
            
                if (pos > last) {
                    buf.append (s.substring (last, pos));
                }
                switch (s.charAt (pos)) {
                case '<':
                    buf.append ("&lt;");
                    break;
                case '>':
                    buf.append ("&gt;");
                    break;
                case '&':
                    buf.append ("&amp;");
                    break;
                case '\'':
                    buf.append ("&apos;");
                    break;
                case '"':
                    buf.append ("&quot;");
                    break;
                default:
                    break;
                }
                last = pos + 1;
            }
            if (last == 0) {
                buf.append (s);
            } else if (last < s.length ()) {
                buf.append (s.substring (last));
            }
        }
    }
    
    private static final String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private void encode (byte[] cont) {
        int len;
        int limit;
        int count;
        int i0, i1, i2;

        len = cont.length;
        limit = ((len + 2) / 3) * 3;
        count = 0;
        for (int n = 0; n < limit; n += 3) {
            if (count == 0)
                buf.append ('\n');
            i0 = cont[n] & 0xff;
            if (n + 1 < len) {
                i1 = cont[n + 1] & 0xff;
                if (n + 2 < len)
                    i2 = cont[n + 2] & 0xff;
                else
                    i2 = 0;
            } else
                i1 = i2 = 0;
            buf.append (code.charAt (i0 >>> 2));
            buf.append (code.charAt (((i0 & 0x3) << 4) | (i1 >>> 4)));
            if (n + 1 < len) {
                buf.append (code.charAt (((i1 & 0xf) << 2) | (i2 >>> 6)));
                if (n + 2 < len)
                    buf.append (code.charAt (i2 & 0x3f));
                else
                    buf.append ("=");
            } else
                buf.append ("==");
            count += 4;
            if (count >= 76)
                count = 0;
        }
        buf.append ('\n');
    }

    public XMLWriter (OutputStream destination) {
        buf = new StringBuffer ();
        out = destination;
        entity = null;
        depth = 0;
        indentNext = true;
    }

    public void flush () throws IOException {
        if (buf.length () > 0) {
            out.write (buf.toString ().getBytes ("UTF8"));
            out.flush ();
            buf.setLength (0);
        }
    }
    
    public void start () {
        buf.setLength (0);
        buf.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    public void end () throws IOException {
        while (entity != null) {
            close ();
        }
        buf.append ('\n');
        flush ();
    }
    
    public void vopen (boolean simple, String name, String[] vars, String[] vals) {
        indent ();
        buf.append ("<");
        buf.append (name);
        if (vars != null) {
            for (int n = 0; n < vars.length; ++n) {
                String  val = (vals != null) && (n < vals.length) ? vals[n] : "";
            
                buf.append (" ");
                buf.append (vars[n]);
                buf.append ("=\"");
                escape (val);
                buf.append ("\"");
            }
        }
        if (simple) {
            buf.append ("/>");
        } else {
            buf.append (">");
            entity = new Entity (name, entity);
            ++depth;
        }
    }
    public void open (boolean simple, String name, Object ... param) {
        int     plen = param.length;
        String[]    vars = new String[plen >> 1];
        String[]    vals = new String[plen >> 1];

        for (int n = 0; n < plen; n += 2) {
            vars[n >> 1] = param[n].toString ();
            vals[n >> 1] = param[n + 1].toString ();
        }
        vopen (simple, name, vars, vals);
    }
    public void opennode (String name, Object ... param) {
        open (false, name, param);
    }
    public void openclose (String name, Object ... param) {
        open (true, name, param);
    }
    
    public Creator create (String name, Object ... param) {
        Creator c = new Creator (name);

        c.add (param);
        return c;
    }
    public void opennode (Creator c) {
        vopen (false, c.name, c.getVariables (), c.getValues ());
    }
    public void openclose (Creator c) {
        vopen (true, c.name, c.getVariables (), c.getValues ());
    }
    
    public void close (String name) {
        boolean match = false;
        
        while ((! match) && (entity != null)) {
            match = (name == null) || entity.name.equals (name);
            --depth;
            indent ();
            buf.append ("</");
            buf.append (entity.name);
            buf.append (">");
            entity = entity.parent;
        }
    }
    
    public void close () {
        close ((String) null);
    }
    
    public void close (Creator c) {
        close (c.name);
    }
    
    public void data (String s) {
        escape (s);
        indentNext = false;
    }
    
    public void data (byte[] b) {
        encode (b);
        indentNext = false;
    }

    public void single (String name, String content, Object ... param) {
        if ((content != null) && (content.length () > 0)) {
            opennode (name, param);
            data (content);
            close (name);
        } else {
            openclose (name, param);
        }
    }
    
    public void single (String name, byte[] content, Object ... param) {
        if ((content != null) && (content.length > 0)) {
            opennode (name, param);
            data (content);
            close (name);
        } else {
            openclose (name, param);
        }
    }
    
    public void single (String name, Object content, Object ... param) {
        single (name, content.toString (), param);
    }
    
    public void single (Creator c, String content) {
        if ((content != null) && (content.length () > 0)) {
            opennode (c);
            data (content);
            close (c);
        } else {
            openclose (c);
        }
    }
    
    public void single (Creator c, byte[] content) {
        if ((content != null) && (content.length > 0)) {
            opennode (c);
            data (content);
            close (c);
        } else {
            openclose (c);
        }
    }
    public void single (Creator c, Object content) {
        single (c, content.toString ());
    }

    public void comment (String s) {
        indent ();
        buf.append ("<!-- ");
        buf.append (s);
        buf.append (" -->");
    }
    
    public void empty () {
        buf.append ('\n');
        indentNext = true;
    }
}
