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
package org.agnitas.util;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * This class provides a common logging interface with separate
 * logging levels. Logfiles are typically written under the
 * home directory of the user in var/log
 */
public class Log {
    /** 
     * global error which may harm things beyond the application 
     */
    final public static int     GLOBAL = 0;
    /**
     * fatal error which requires manual correction and termiantes
     * the application
     */
    final public static int     FATAL = 1;
    /**
     * error which can require manual correction 
     */
    final public static int     ERROR = 2;
    /**
     * warning which can be a hint for some problems 
     */
    final public static int     WARNING = 3;
    /**
     * more important runtime information 
     */
    final public static int     NOTICE = 4;
    /**
     * some general runtime information 
     */
    final public static int     INFO = 5;
    /**
     * more verbose information 
     */
    final public static int     VERBOSE = 6;
    /**
     * just debug output, in normal operation mostly useless 
     */
    final public static int     DEBUG = 7;
    /**
     * textual represenation of loglevels 
     */
    final static String[]       DESC = {
        "GLOBAL",
        "FATAL",
        "ERROR",
        "WARNING",
        "NOTICE",
        "INFO",
        "VERBOSE",
        "DEBUG"
    };
    /**
     * the level up to we will write to the logfile 
     */
    private int         level;
    /**
     * optional output print 
     */
    private PrintStream     printer;
    /**
     * to provide some hirarchical log IDs 
     */
    private Stack <String>  idc;
    /** 
     * the base path to the log file directory 
     */
    private String      path;
    /**
     * the part of the logfile after the current date 
     */
    private String      append;
    /**
     * format of date for logfilename 
     */
    private SimpleDateFormat    fmt_fname;
    /**
     * format of date to be written to logfile 
     */
    private SimpleDateFormat    fmt_msg;

    /**
     * Find the numeric representation of a textual loglevel
     *
     * @param desc the textual loglevel
     * @return its numeric value
     * @throws java.lang.NumberFormatException
     */
    static public int matchLevel (String desc) throws NumberFormatException {
        for (int n = 0; n < DESC.length; ++n) {
            if (DESC[n].equalsIgnoreCase (desc)) {
                return n;
            }
        }
        return Integer.parseInt (desc);
    }

    /**
     * Make a textual description of the given loglevel
     *
     * @param loglvl the numeric loglevel
     * @return its string version
     */
    static public String levelDescription (int loglvl) {
        if ((loglvl >= 0) && (loglvl < DESC.length)) {
            return DESC[loglvl];
        }
        return "(" + loglvl + ")";
    }
    
    /**
     * For pretty printing, this returns an empty string on 1,
     * otherwise "s"
     *
     * @param nr the number to check
     * @return the extension based on the number
     */
    static public String exts (long nr) {
        return (nr == 1) ? "" : "s";
    }

    /**
     * Wrapper for integer input to exts
     *
     * @param nr the number to check
     * @return the extension based on the number
     */
    static public String exts (int nr) {
        return exts ((long) nr);
    }
    
    /**
     * Like exts, but for words like entry vs. entries
     *
     * @param nr the number to check
     * @return the extension based on the number
     */
    static public String exty (long nr) {
        return (nr == 1) ? "y" : "ies";
    }

    /**
     * Wrapper for integer input to exty
     *
     * @param nr the number to check
     * @return the extension based on the number
     */
    static public String exty (int nr) {
        return exty ((long) nr);
    }

    /**
     * Constructor for class
     *
     * @param program the name of the application
     * @param level the maximum log level to report
     */
    public Log (String program, int level) {
        this.level = level;
        printer = null;

        String  separator = System.getProperty ("file.separator");
        String  home = System.getProperty ("user.home", ".");
        String  logdir;
        String  hostname;
        int idx;

        idc = new Stack <String> ();
        logdir = System.getProperty ("log.home", home + separator + "var" + separator + "log");
        if (logdir == null) {
            path = "";
        } else {
            path = logdir + separator;
        }

        try {
            InetAddress addr = InetAddress.getLocalHost ();
            
            try {
                hostname = addr.getHostName ();
                if ((idx = hostname.indexOf ('.')) != -1) {
                    hostname = hostname.substring (0, idx);
                }
            } catch (SecurityException e) {
                hostname = addr.getHostAddress ();
            }
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }

        if ((idx = program.lastIndexOf (separator)) != -1) {
            program = program.substring (idx + 1);
        }
        append = "-" + hostname + "-" + program + ".log";
        fmt_fname = new SimpleDateFormat ("yyyyMMdd");
        fmt_msg = new SimpleDateFormat ("[dd.MM.yyyy  HH:mm:ss] ");
    }

    /** 
     * returns the current loglevel
     *
     * @return log level
     */
    public int level () {
        return level;
    }
    
    /** 
     * sets the current loglevel
     *
     * @param nlevel new log level
     */
    public void level (int nlevel) {
        level = nlevel;
    }
    
    /**
     * returns the textual representation of the
     * current loglevel
     *
     * @return current loglevel as string
     */
    public String levelDescription () {
        return levelDescription (level);
    }

    /**
     * sets the loglevel using its textual representation
     * @param desc the loglevel as string
     */
    public void levelDescription (String desc) throws NumberFormatException {
        level = matchLevel (desc);
    }
    
    /** sets the optional output stream
     *
     * @param nprinter new stream
     */
    public void setPrinter (PrintStream nprinter) {
        printer = nprinter;
    }
    
    /**
     * Pushes a new id, so a chain of IDs is put into any
     * logfile entry
     *
     * @param nid the new id
     * @param separator howto separate the the IDs
     */
    public void pushID (String nid, String separator) {
        if ((separator != null) && (! idc.empty ())) {
            idc.push (idc.peek () + separator + nid);
        } else {
            idc.push (nid);
        }
    }
    
    /**
     * Pushes a new id without separator
     *
     * @param nid the new id
     */
    public void pushID (String nid) {
        pushID (nid, null);
    }
    
    /** 
     * Removes to top element of the ID stack
     *
     * @return the top ID on the stack or null, if stack is empty
     */
    public String popID () {
        try {
            return idc.pop ();
        } catch (EmptyStackException e) {
            ;
        }
        return null;
    }

    /** 
     * Clear all stacked IDs
     */
    public void clrID () {
        idc.clear ();
    }
    
    /** 
     * Set ID after removing all existing IDs
     *
     * @param mid the ID to set
     */
    public void setID (String mid) {
        idc.clear ();
        idc.push (mid);
    }

    /**
     * check if the given level should be logged
     *
     * @param loglvl the level to check
     * @return true, if logging is enabled for this level
     */
    public boolean islog (int loglvl) {
        return loglvl <= level;
    }
    
    /**
     * writes an entry to the logfile
     *
     * @param loglvl the level of this message
     * @param mid the ID of this message
     * @param msg the message itself
     */
    public void out (int loglvl, String mid, String msg) {
        if (loglvl <= level) {
            Date    now = new Date ();
            String  fname = path + fmt_fname.format (now) + append;
            String  output = fmt_msg.format (now) + levelDescription (loglvl) + (mid != null ? "/" + mid : "") + ": " + msg + "\n";

            try {
                FileOutputStream    file = new FileOutputStream (fname, true);
            
                file.write (output.getBytes ());
                file.close ();
                if (printer != null) {
                    printer.println (msg);
                }
            } catch (Exception e) {
                System.err.print (output);
            }
        }
    }

    /**
     * writes an entry to the logfile using the stacked ID
     *
     * @param loglvl the level of this message
     * @param msg the mesage itself
     */
    public void out (int loglvl, String msg) {
        String  mid;
        
        try {
            mid = idc.peek ();
        } catch (EmptyStackException e) {
            mid = null;
        }
        out (loglvl, mid, msg);
    }
}
