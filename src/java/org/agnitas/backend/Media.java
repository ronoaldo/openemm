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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/** Class to collect all information for emailing
 */
public class Media {
    /** Unspecified media type */
    static public final int TYPE_UNRELATED = -1;
    /** Mediatype email */
    static public final int TYPE_EMAIL = 0;
    static public final int TYPE_MAX = 1;
    /** Entry is not used */
    static public final int STAT_UNUSED = 0;
    /** Entry marked as inactive */
    static public final int STAT_INACTIVE = 1;
    /** Entry marked as active */
    static public final int STAT_ACTIVE = 2;

    /** The media type itself */
    public int      type;
    /** Its usage priority */
    public int      prio;
    /** The status if active */
    public int      stat;
    /** Assigned paramter as found in database */
    public String       parm;
    /** Parsed version of parameters */
    public Hashtable <String, Vector <String>>
                ptab;
    /** Single linked list */
    public Object       next;

    /** Returns the string for a media type
     * @param t the mediatype
     * @return its string representation
     */
    static public String typeName (int t) {
        switch (t) {
        case TYPE_EMAIL:
            return "email";
        }
        return Integer.toString (t);
    }

    /** Return the priority as string
     * @param p the numeric priority
     * @return its string representation
     */
    static public String priorityName (int p) {
        return Integer.toString (p);
    }

    /** Return the status as string
     * @param s the numeric status
     * @return its string representation
     */
    static public String statusName (int s) {
        switch (s) {
        case STAT_UNUSED:
            return "unused";
        case STAT_INACTIVE:
            return "inactive";
        case STAT_ACTIVE:
            return "active";
        }
        return Integer.toString (s);
    }

    /** parse set parameter into its single variable/value pairs
     */
    @SuppressWarnings ("fallthrough")
    private void parseParameter () {
        int     state;
        int     start;
        String      variable;
        StringBuffer    value;

        state = 0;
        start = 0;
        variable = null;
        value = null;
        for (int n = 0; n < parm.length (); ++n) {
            char    ch = parm.charAt (n);

            switch (state) {
            case 0:
                if (Character.isWhitespace (ch))
                    break;
                variable = null;
                value = null;
                start = n;
                state = 1;
                // Fall through . . .
            case 1:
                if (Character.isWhitespace (ch) || (ch == '=') || (ch == ',')) {
                    variable = parm.substring (start, n);
                    if (Character.isWhitespace (ch))
                        state = 2;
                    else if (ch == '=')
                        state = 3;
                    else
                        state = 100;
                }
                break;
            case 2:
                if (ch == '=')
                    state = 3;
                else if (state == ',')
                    state = 100;
                else if (! Character.isWhitespace (ch))
                    state = 101;
                break;
            case 3:
                if (ch == '"') {
                    state = 4;
                    value = new StringBuffer ();
                } else if (! Character.isWhitespace (ch))
                    state = 101;
                break;
            case 4:
                if (ch == '\\')
                    state = 5;
                else if ((ch == '"') && ((n + 1 == parm.length ()) || (parm.charAt (n + 1) == ','))) // bad hack to be compatible with frontend
                    state = 100;
                else
                    value.append (ch);
                break;
            case 5:
                value.append (ch);
                state = 4;
                break;
            }
            if (state == 100) {
                if (value != null)
                    addParameter (variable, value.toString ());
                else
                    addParameter (variable, new Vector <String> ());
                state = 101;
            } else if (state == 101) {
                if (ch == ',')
                    state = 0;
            }
        }
    }

    /** The constructor
     * @param mediatype the type for this entry
     * @param priority its priority
     * @param status its activity status
     * @param parameter the parameter as found in the database
     */
    public Media (int mediatype, int priority, int status, String parameter) {
        type = mediatype;
        prio = priority;
        stat = status;
        parm = parameter;
        ptab = new Hashtable <String, Vector <String>> ();
        if (parm != null)
            parseParameter ();
        next = null;
    }

    /** Return own type as string
     * @return its string representation
     */
    public String typeName () {
        return typeName (type);
    }

    /** Return own priority as string
     * @return its string representation
     */
    public String priorityName () {
        return priorityName (prio);
    }

    /** Return own status as string
     * @return its string representation
     */
    public String statusName () {
        return statusName (stat);
    }

    /** Get a list of all variable names
     * @return Vector containing all variable names
     */
    public Vector <String> getParameterVariables () {
        Vector <String> rc = new Vector <String> ();

        for (Enumeration <String> e = ptab.keys (); e.hasMoreElements (); )
            rc.addElement (e.nextElement ());
        return rc;
    }

    /** Find all values to a variable
     * @param id the name of the variable
     * @return Vector containing all values (or null)
     */
    public Vector <String> findParameterValues (String id) {
        return ptab.get (id);
    }

    /** Set values for a variable
     * @param id the name of the variable
     * @param val the vector of values
     */
    public void setParameter (String id, Vector <String> val) {
        ptab.put (id, val);
    }

    /** Set one value for a variable
     * @param id the name of the variable
     * @param val the value
     */
    public void setParameter (String id, String val) {
        Vector <String> v = new Vector <String> (1);

        v.addElement (val);
        setParameter (id, v);
    }

    /** Add values to an existing variable
     * @param id the name of the variable
     * @param val the values to add
     */
    public void addParameter (String id, Vector <String> val) {
        Vector <String> tmp;

        if ((tmp = ptab.get (id)) != null)
            for (int n = 0; n < val.size (); ++n)
                tmp.addElement (val.elementAt (n));
        else
            ptab.put (id, val);
    }

    /** Add a value to an existing variable
     * @param id the name of the variable
     * @param val the value to add
     */
    public void addParameter (String id, String val) {
        Vector <String> v = new Vector <String> (1);

        v.addElement (val);
        addParameter (id, v);
    }

    /** Find a parameter as string
     * @param id Variable to look for
     * @param dflt default, if variable is not found
     * @return the value, if available or the default
     */
    public String findString (String id, String dflt) {
        Vector  v = findParameterValues (id);

        if ((v != null) && (v.size () > 0))
            return (String) v.elementAt (0);
        return dflt;
    }

    /** Find a paramter as interger value
     * @param id Variable to look for
     * @param dflt default, if variable is not found
     * @return the value, if available and parsable or the default
     */
    public int findInteger (String id, int dflt) {
        String  tmp = findString (id, null);

        if (tmp != null)
            try {
                return Integer.parseInt (tmp);
            } catch (NumberFormatException e) {
                ;
            }
        return dflt;
    }
}
