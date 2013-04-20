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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Representation of a single column
 */
public class Column {
    /**
     * Name of this column
     */
    String      name;

    /**
     * An optional alias name for this column
     */
    String      alias;

    /**
     * Reference to table to access
     */
    String      ref;
    
    /**
     * Qualified name
     */
    String      qname;

    /**
     * Data type of this column
     */
    int     type;

    /**
     * True if DB has NULL value
     */
    boolean     isnull;

    /**
     * True if column is in use
     */
    boolean     inuse;

    /**
     * Its numeric version
     */
    long        ival;

    /**
     * its float version
     */
    double      fval;

    /**
     * Its string version
     */
    String      sval;

    /**
     * Its date version
     */
    Date        dval;

    /**
     * Its time version
     */
    Time        tval;

    /**
     * Its timestamp version
     */
    Timestamp   tsval;

    /**
     * Constructor
     */
    protected Column () {
        name = null;
        alias = null;
        ref = null;
        qname = null;
        type = -1;
        isnull = false;
        inuse = true;
        ival = -1;
        fval = -1.0;
        sval = null;
        dval = null;
        tval = null;
        tsval = null;
    }

    /**
     * Constructor setting name and type
     *
     * @param cName name of column
     * @param cType type of column
     */
    protected Column (String cName, int cType) {
        this ();
        name = cName;
        qname = name == null ? null : name.toLowerCase ();
        type = cType;
    }

    /**
     * Set an alias name
     *
     * @param nAlias the new alias
     */
    protected void setAlias (String nAlias) {
        alias = nAlias;
    }

    protected void setRef (String nRef) {
        ref = nRef;
        if (name != null) {
            if (ref == null) {
                qname = name.toLowerCase ();
            } else {
                qname = ref.toLowerCase () + "." + name.toLowerCase ();
            }
        }
    }

    /**
     * Set value from a result set
     *
     * @param rset the result set to use
     * @param index the index into the result set
     */
    protected void set (ResultSet rset, int index) {
        switch (type) {
        default:
            return;
        case Types.DECIMAL:
        case Types.NUMERIC:
        case Types.DOUBLE:
            try {
                fval = rset.getDouble (index);
            } catch (SQLException e) {
                fval = -1.0;
            }
            ival = (long) fval;
            break;
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
            try {
                ival = rset.getLong (index);
            } catch (SQLException e) {
                ival = -1;
            }
            break;
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.BLOB:
        case Types.CLOB:
            try {
                if ((type == Types.CHAR) || (type == Types.VARCHAR)) {
                    sval = rset.getString (index);
                } else if (type == Types.BLOB) {
                    Blob    tmp = rset.getBlob (index);

                    sval = tmp == null ? null : StringOps.blob2string (tmp, "UTF-8");
                } else if (type == Types.CLOB) {
                    Clob    tmp = rset.getClob (index);

                    sval = tmp == null ? null : StringOps.clob2string (tmp);
                }
            } catch (SQLException e) {
                sval = null;
            }
            break;
        case Types.DATE:
            try {
                dval = rset.getDate (index);
            } catch (SQLException e) {
                dval = null;
            }
            break;
        case Types.TIME:
            try {
                tval = rset.getTime (index);
            } catch (SQLException e) {
                tval = null;
            }
            break;
        case Types.TIMESTAMP:
            try {
                tsval = rset.getTimestamp (index);
            } catch (SQLException e) {
                tsval = null;
            }
            break;
        }
        try {
            isnull = rset.wasNull ();
        } catch (SQLException e) {
            isnull = false;
        }
    }

    /**
     * Get a column value as string
     *
     * @return string version of column content
     */
    public String get () {
        String  str;

        switch (type) {
        case Types.DECIMAL:
        case Types.NUMERIC:
        case Types.DOUBLE:
            if ((double) ival != fval)
                return Double.toString (fval);
            return Long.toString (ival);
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
            return Long.toString (ival);
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.BLOB:
        case Types.CLOB:
            return sval != null ? sval : "";
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
            SimpleDateFormat    fmt = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", new Locale ("en"));
//          fmt.setTimeZone (TimeZone.getTimeZone ("GMT"));
            if ((type == Types.DATE) && (dval != null)) {
                str = fmt.format (dval);
            } else if ((type == Types.TIME) && (tval != null)) {
                str = fmt.format (tval);
            } else if ((type == Types.TIMESTAMP) && (tsval != null)) {
                str = fmt.format (tsval);
            } else {
                str = "0000-00-00 00:00:00";
            }
            return str;
        }
        return null;
    }

    public String get (Format format) {
        String  rc = null;

        if (format != null) {
            switch (type) {
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.DOUBLE:
                rc = format.formatFloat (fval);
                break;
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                rc = format.formatInteger (ival);
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.BLOB:
            case Types.CLOB:
                rc = format.formatString (sval != null ? sval : "");
                break;
            case Types.DATE:
                rc = format.formatDate (dval);
                break;
            case Types.TIME:
                rc = format.formatDate (tval);
                break;
            case Types.TIMESTAMP:
                rc = format.formatDate (tsval);
                break;
            }
        }
        return rc != null ? rc : get ();
    }

    protected String validate (Format format) {
        get (format);
        return format.error;
    }

    /**
     * Checks for NULL value
     *
     * @return true, if value is NULL
     */
    protected boolean isNull () {
        return isnull;
    }

    /**
     * Checks wether column is in use
     *
     * @return true, if column is in use
     */
    protected boolean inUse () {
        return inuse;
    }

    /**
     * Returns the type of the given type as simple
     * string representation, either "i" for intergers,
     * "s" for strings and "d" for date types
     *
     * @param cType the column type
     * @return the simple type string represenation
     */
    static protected String typeStr (int cType) {
        switch (cType) {
        case Types.DECIMAL:
        case Types.NUMERIC:
        case Types.DOUBLE:
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
            return "n";
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.BLOB:
        case Types.CLOB:
            return "s";
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
            return "d";
        }
        return null;
    }

    /**
     * Returns the type as string
     *
     * @return the string representation
     */
    protected String typeStr () {
        return typeStr (type);
    }
}
