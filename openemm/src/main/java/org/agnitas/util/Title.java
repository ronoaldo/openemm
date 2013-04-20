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

import  java.util.Enumeration;
import  java.util.HashSet;
import  java.util.Hashtable;
import  java.util.Vector;

/** Collection of titles
 */
public class Title {
    class Entry {
        String      title;
        CustomTitle custom;
        protected Entry (String t) {
            if (t.startsWith (CustomTitle.SCRIPT_ID)) {
                title = null;
                custom = new CustomTitle (t);
            } else {
                title = t;
                custom = null;
            }
        }
    }

    public final static int TITLE_ALL = -1;
    public final static int TITLE_DEFAULT = 0;
    public final static int TITLE_FULL = 1;
    public final static int TITLE_FIRST = 2;
    public final static int TITLE_MAX = 3;
    /** the unique ID of this title */
    protected Long      id;
    /** The titles for each gender */
    private Vector <Entry> titles;

    /* Constructor
     * @param nID new unique id
     */
    public Title (Long nID) {
        id = nID;
        titles = new Vector <Entry> ();
    }

    /* Constructor without id, use null in this case */
    public Title () {
        this (null);
    }

    /** Set/Add a title for a gender
     * @param gender numeric representation for the gender
     * @param title title for this gender
     */
    public void setTitle (int gender, String title) {
        if (gender >= 0) {
            if ((title != null) && title.endsWith (" ")) {
                title = title.substring (0, title.length () - 1);
            }

            int size = titles.size ();

            if (size <= gender) {
                while (size < gender) {
                    titles.add (size, null);
                    ++size;
                }
                titles.add (gender, title == null ? null : new Entry (title));
            } else {
                titles.set (gender, title == null ? null : new Entry (title));
            }
        }
    }

    /** Set required database fields
     * @param datap context
     * @param predef collection of required db names
     * @param ttype title type
     */;
    public void requestFields (HashSet <String> predef, int ttype) {
        predef.add ("gender");
        if ((ttype == TITLE_ALL) || (ttype == TITLE_FIRST) || (ttype == TITLE_FULL))
            predef.add ("firstname");
        if ((ttype == TITLE_ALL) || (ttype == TITLE_DEFAULT) || (ttype == TITLE_FULL)) {
            predef.add ("title");
            predef.add ("lastname");
        }
        for (int n = 0; n < titles.size (); ++n) {
            Entry   e = titles.elementAt (n);

            if ((e != null) && (e.custom != null)) {
                e.custom.requestFields (predef);
            }
        }
    }

    /** Check for valid input strings
     * @param s the input string
     * @return true if string is not empty, false otherwise
     */
    private boolean isValid (String s) {
        return (s != null) && (s.length () > 0);
    }

    /** Create the title string using customer related data
     * @param titleType type of title to create
     * @param gender the gender from the db
     * @param title the title from the db
     * @param firstname you guess it
     * @param lastname again
     * @param columns other required columns from the database
     * @param error optional buffer to put error messages to
     * @return the title string
     */
    public String makeTitle (int titleType, int gender, String title, String firstname, String lastname, Hashtable <String, String> columns, StringBuffer error) {
        String  s = "";
        Entry   e;
        String  name = null;

        if ((gender < 0) || (gender >= titles.size ())) {
            gender = 2;
        }
        if (gender < titles.size ()) {
            e = titles.elementAt (gender);
        } else {
            e = null;
        }
        if ((e == null) && (gender != 2) && (2 < titles.size ())) {
            gender = 2;
            e = titles.elementAt (gender);
        }
        if (e != null) {
            if (e.custom == null) {
                if (gender != 2) {
                    switch (titleType) {
                    case TITLE_DEFAULT:
                    case TITLE_FULL:
                        String  custtitle = "";

                        if (isValid (title)) {
                            custtitle = title + " ";
                        }
                        if (titleType == TITLE_FULL) {
                            if (isValid (firstname)) {
                                if (isValid (lastname)) {
                                    name = firstname + " " + lastname;
                                }
                            } else if (isValid (lastname)) {
                                name = lastname;
                            }
                            if (name != null) {
                                name = custtitle + name;
                            }
                        } else if (isValid (lastname)) {
                            name = custtitle + lastname;
                        }
                        break;
                    case TITLE_FIRST:
                        if (isValid (firstname)) {
                            name = firstname;
                        }
                        break;
                    }
                    if (name == null) {
                        gender = 2;
                        if (gender < titles.size ()) {
                            e = titles.elementAt (gender);
                        } else {
                            e = null;
                        }
                    }
                }
            }
            if (e != null) {
                if (e.custom == null) {
                    if (e.title != null) {
                        if (name != null) {
                            s = e.title + " " + name;
                        } else {
                            s = e.title;
                        }
                    }
                } else {
                    s = e.custom.makeTitle (titleType, gender, title, firstname, lastname, columns, error);
                }
            }
        }
        return s;
    }

    /** Create all title strings using customer related data
     * Beware, use this only for single cases as this is slow
     * @param columns all required columns from the database
     * @param error optional buffer to put error messages to
     * @return all title strings
     */
    public String[][] makeTitles (Hashtable <String, String> columns) {
        int     genders = titles.size () < 3 ? 2 : titles.size () - 1;
        String[][]  rc = new String[genders + 1][TITLE_MAX];
        String      title, firstname, lastname;
        StringBuffer    error = new StringBuffer ();
        Hashtable <String, String>
                lcColumns = new Hashtable <String, String> (columns.size ());

        for (Enumeration <String> e = columns.keys (); e.hasMoreElements (); ) {
            String  colname = e.nextElement ();

            lcColumns.put (colname.toLowerCase (), columns.get (colname));
        }

        title = lcColumns.get ("title");
        firstname = lcColumns.get ("firstname");
        lastname = lcColumns.get ("lastname");
        for (int gender = 0; gender <= genders; ++gender) {
            for (int n = 0; n < TITLE_MAX; ++n) {
                rc[gender][n] = makeTitle (n, gender, title, firstname, lastname, lcColumns, error);
                if (error.length () > 0) {
                    error.insert (0, "**: ");
                    rc[gender][n] = error.toString ();
                    error.setLength (0);
                }
            }
        }
        return rc;
    }
}
