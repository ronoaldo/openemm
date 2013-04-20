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
import java.util.Date;
import java.sql.Timestamp;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.JdbcOperations;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Level;
import org.agnitas.util.Log;

/** Database abstraction layer
 */
public class DBase {
    public final int            DB_UNSET = 0;
    public final int            DB_MYSQL = 1;
    public final int            DB_ORACLE = 2;
    /** name for current date in database */
    public int              dbType = DB_UNSET;
    public String               sysdate = null;
    public String               timestamp = null;
    public String               measureType = null;
    public String               measureRepr = null;
    /** Reference to configuration */
    private Data                data = null;
    /** Reference to data source */
    protected DataSource            dataSource = null;
    /** Default jdbc access instance */
    private SimpleJdbcTemplate      jdbcTmpl = null;
    /** Collection of free connections */
    private ArrayList <SimpleJdbcTemplate>  pool = null;

    static class DBaseFilter extends Filter {
        @Override
        public int decide (LoggingEvent e) {
            Level   l = e.getLevel ();

            if ((l == Level.WARN) || (l == Level.ERROR) || (l == Level.FATAL)) {
                return Filter.ACCEPT;
            }
            return Filter.NEUTRAL;
        }
    }
    static class DBaseAppender extends AppenderSkeleton {
        private Log log;

        public DBaseAppender (Log nLog) {
            super ();
            log = nLog;
        }

        @Override
        public boolean requiresLayout () {
            return false;
        }

        @Override
        public void close () {
        }

        @Override
        protected void append (LoggingEvent e) {
            Level   l = e.getLevel ();
            int lvl = -1;

            if (l == Level.WARN) {
                lvl = Log.WARNING;
            } else if (l == Level.ERROR) {
                lvl = Log.ERROR;
            } else if (l == Level.FATAL) {
                lvl = Log.FATAL;
            }
            if (lvl != -1) {
                String  name = e.getLoggerName ();

                if ((name == null) || name.startsWith ("org.springframework.jdbc")) {
                    log.out (lvl, "jdbc", e.getRenderedMessage ());
                }
            }
        }
    }
    static class DBDatasource {
        private HashMap <String, DataSource>    cache;
        private HashSet <String>        seen;
        private Log             log;

        public DBDatasource () {
            cache = new HashMap <String, DataSource> ();
            seen = new HashSet <String> ();
            log = new Log ("jdbc", Log.INFO);

            Appender    app = new DBaseAppender (log);

            app.addFilter (new DBaseFilter ());
            BasicConfigurator.configure (app);
        }

        public DataSource newDataSource (String driver, String connect, String login, String password) {
            return new DriverManagerDataSource (connect, login, password);
        }
        public synchronized DataSource request (String driver, String connect, String login, String password) throws ClassNotFoundException {
            DataSource      rc;
            String          key = driver + ";" + connect + ";" + login + ";*";
            ArrayList <DataSource>  cur;

            if (cache.containsKey (key)) {
                rc = cache.get (key);
                log.out (Log.DEBUG, "rq", "Got exitsing DS for " +key);
            } else {
                if (! seen.contains (driver)) {
                    try {
                        Class.forName (driver);
                        seen.add (driver);
                        log.out (Log.DEBUG, "rq", "Installed new driver for " + driver);
                    } catch (ClassNotFoundException e) {
                        log.out (Log.ERROR, "rq", "Failed to install driver " + driver);
                        throw e;
                    }
                }
                rc = newDataSource (driver, connect, login, password);
                cache.put (key, rc);
                log.out (Log.DEBUG, "rq", "Created new DS for " + key);
            }
            return rc;
        }
    }
    static class DBDatasourcePooled extends DBDatasource {
        private int dsPoolsize = 12;
        private boolean dsPoolgrow = true;

        public void setup (int poolsize, boolean poolgrow) {
            dsPoolsize = poolsize;
            dsPoolgrow = poolgrow;
        }
        public DataSource newDataSource (String driver, String connect, String login, String password) {
            ObjectPool          connectionPool = new GenericObjectPool (null, dsPoolsize, (dsPoolgrow ? GenericObjectPool.WHEN_EXHAUSTED_GROW : GenericObjectPool.WHEN_EXHAUSTED_BLOCK), 0);
            ConnectionFactory       connectionFactory = new DriverManagerConnectionFactory (connect, login, password);
//            KeyedObjectPoolFactory      statementPoolFactory = new GenericKeyedObjectPoolFactory (null);
            PoolableConnectionFactory   poolableConnectionFactory = new PoolableConnectionFactory (connectionFactory, connectionPool, null /*statementPoolFactory*/, null, false, true);

            return new PoolingDataSource (connectionPool);
        }
    }

    private static DBDatasourcePooled dsPool = new DBDatasourcePooled ();
    public DBase (Data nData) throws Exception {
        data = nData;

        dsPool.setup (data.dbPoolsize (), data.dbPoolgrow ());
        dataSource = dsPool.request (data.dbDriver (), data.dbConnect (), data.dbLogin (), data.dbPassword ());
        jdbcTmpl = new SimpleJdbcTemplate (dataSource);
        pool = new ArrayList <SimpleJdbcTemplate> ();
    }

    public void setup () throws Exception {
        dbType = DB_MYSQL;
        sysdate = "current_timestamp";
        timestamp = "change_date";
        measureType = "MEASURE_TYPE";
        measureRepr = "MEASURE_TYPE";
    }

    /**
     * Cleanup, close open statements and database connection
     */
    public void done () throws Exception {
        pool.clear ();
        pool = null;
        jdbcTmpl = null;
        dataSource = null;
    }

    private void show (String what, String query, HashMap <String, Object> param) {
        if ((query != null) && data.islog (Log.DEBUG)) {
            String m =  what + ": " + query;

            if ((param != null) && (param.size () > 0)) {
                String  sep = " { ";

                for (String key : param.keySet ()) {
                    Object  val = param.get (key);
                    String  disp;

                    if (val == null) {
                        disp = "null";
                    } else {
                        try {
                            Class cls = val.getClass ();

                            if ((cls == String.class) || (cls == StringBuffer.class)) {
                                disp = "\"" + val.toString () + "\"";
                            } else if (cls == Character.class) {
                                disp = "'" + val.toString () + "'";
                            } else if (cls == Boolean.class) {
                                disp = ((Boolean) val).booleanValue () ? "true" : "false";
                            } else {
                                disp = val.toString ();
                            }
                        } catch (Exception e) {
                            disp = "???";
                        }
                    }
                    m += sep + key + "=" + disp;
                    sep = ", ";
                }
                m += " }";
            }
            data.logging (Log.DEBUG, "dbase", m);
        }
    }

    public SimpleJdbcTemplate jdbc () {
        return jdbcTmpl;
    }
    public SimpleJdbcTemplate jdbc (String query, HashMap <String, Object> param) {
        show ("DB", query, param);
        return jdbc ();
    }
    public SimpleJdbcTemplate jdbc (String query) {
        return jdbc (query, null);
    }

    public JdbcOperations op () {
        return jdbc ().getJdbcOperations ();
    }
    public JdbcOperations op (String query, HashMap <String, Object> param) {
        show ("OP", query, param);
        return op ();
    }
    public JdbcOperations op (String query) {
        return op (query, null);
    }

    public SimpleJdbcTemplate request () {
        SimpleJdbcTemplate  temp;

        if (pool.isEmpty ()) {
            temp = new SimpleJdbcTemplate (dataSource);
        } else {
            temp = pool.remove (0);
        }
        return temp;
    }
    public SimpleJdbcTemplate request (String query, HashMap <String, Object> param) {
        show ("RQ", query, param);
        return request ();
    }
    public SimpleJdbcTemplate request (String query) {
        return request (query, null);
    }

    public SimpleJdbcTemplate release (SimpleJdbcTemplate temp) {
        if ((temp != null) && (! pool.contains (temp))) {
            pool.add (temp);
        }
        return null;
    }
    public SimpleJdbcTemplate release (SimpleJdbcTemplate temp, String query, HashMap <String, Object> param) {
        show ("RL", query, param);
        return release (temp);
    }
    public SimpleJdbcTemplate release (SimpleJdbcTemplate temp, String query) {
        return release (temp, query, null);
    }

    private HashMap <String, Object> pack (Object[] param) {
        HashMap <String, Object>    input = new HashMap <String, Object> (param.length / 2);

        for (int n = 0; n < param.length; n += 2) {
            input.put ((String) param[n], param[n + 1]);
        }
        return input;
    }

    private Exception failure (String q, Exception e) {
        data.logging (Log.ERROR, "dbase", "DB Failed: " + q + ": " + e.toString ());
        return e;
    }

    private int doQueryInt (SimpleJdbcTemplate jdbc, String q, HashMap <String, Object> packed) throws Exception {
        try {
            return jdbc.queryForInt (q, packed);
        } catch (Exception e) {
            throw failure (q, e);
        }
    }
    public int queryInt (SimpleJdbcTemplate jdbc, String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQueryInt (jdbc, q, packed);
    }
    public int queryInt (String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQueryInt (jdbc (q, packed), q, packed);
    }

    private long doQueryLong (SimpleJdbcTemplate jdbc, String q, HashMap <String, Object> packed) throws Exception {
        try {
            return jdbc.queryForLong (q, packed);
        } catch (Exception e) {
            throw failure (q, e);
        }
    }
    public long queryLong (SimpleJdbcTemplate jdbc, String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQueryLong (jdbc, q, packed);
    }
    public long queryLong (String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQueryLong (jdbc (q, packed), q, packed);
    }

    private String doQueryString (SimpleJdbcTemplate jdbc, String q, HashMap <String, Object> packed) throws Exception {
        try {
            return jdbc.queryForObject (q, String.class, packed);
        } catch (Exception e) {
            throw failure (q, e);
        }
    }
    public String queryString (SimpleJdbcTemplate jdbc, String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQueryString (jdbc, q, packed);
    }
    public String queryString (String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQueryString (jdbc (q, packed), q, packed);
    }

    private Map <String, Object> doQuerys (SimpleJdbcTemplate jdbc, String q, HashMap <String, Object> packed) throws Exception {
        try {
            return jdbc.queryForMap (q, packed);
        } catch (Exception e) {
            throw failure (q, e);
        }
    }
    public Map <String, Object> querys (SimpleJdbcTemplate jdbc, String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQuerys (jdbc, q, packed);
    }
    public Map <String, Object> querys (String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQuerys (jdbc (q, packed), q, packed);
    }

    private List <Map <String, Object>> doQuery (SimpleJdbcTemplate jdbc, String q, HashMap <String, Object> packed) throws Exception {
        try {
            return jdbc.queryForList (q, packed);
        } catch (Exception e) {
            throw failure (q, e);
        }
    }
    public List <Map <String, Object>> query (SimpleJdbcTemplate jdbc, String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQuery (jdbc, q, packed);
    }
    public List <Map <String, Object>> query (String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doQuery (jdbc (q, packed), q, packed);
    }

    private int doUpdate (SimpleJdbcTemplate jdbc, String q, HashMap <String, Object> packed) throws Exception {
        try {
            return jdbc.update (q, packed);
        } catch (Exception e) {
            throw failure (q, e);
        }
    }
    public int update (SimpleJdbcTemplate jdbc, String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doUpdate (jdbc, q, packed);
    }
    public int update (String q, Object ... param) throws Exception {
        HashMap <String, Object>    packed = pack (param);

        return doUpdate (jdbc (q, packed), q, packed);
    }

    private void doExecute (SimpleJdbcTemplate jdbc, String q) throws Exception {
        try {
            jdbc.getJdbcOperations ().execute (q);
        } catch (Exception e) {
            throw failure (q, e);
        }
    }
    public void execute (SimpleJdbcTemplate jdbc, String q) throws Exception {
        doExecute (jdbc, q);
    }
    public void execute (String q) throws Exception {
        doExecute (jdbc (q), q);
    }

    /**
     * Check the string for a minimum length or not all spaces,
     * otherwise set it to null
     * @param s the string to validate
     * @param minLength the minimal length required for the string
     * @return the modified string
     */
    public String validate (String s, int minLength) {
        if (s != null) {
            int len = s.length ();

            if (len < minLength) {
                s = null;
            } else {
                int n;

                for (n = 0; n < len; ++n) {
                    if (s.charAt (n) != ' ') {
                        break;
                    }
                }
                if (n == len) {
                    s = null;
                }
            }
        }
        return s;
    }
    public String validate (String s) {
        return validate (s, 1);
    }

    public int asInt (Object o, int ifNull) {
        return o != null ? ((Number) o).intValue () : ifNull;
    }
    public int asInt (Object o) {
        return asInt (o, 0);
    }
    public long asLong (Object o, long ifNull) {
        return o != null ? ((Number) o).longValue () : ifNull;
    }
    public long asLong (Object o) {
        return asLong (o, 0L);
    }
    public String asString (Object o, int minLength, String ifNull) {
        String  s = validate ((String) o, minLength);

        return s != null ? s : ifNull;
    }
    public String asString (Object o, int minLength) {
        return asString (o, minLength, null);
    }
    public String asString (Object o, String ifNull) {
        return asString (o, 1, ifNull);
    }
    public String asString (Object o) {
        return asString (o, 1, null);
    }
    public String asClob (Object o) {
        if (o == null) {
            return null;
        } else if (o.getClass () == String.class) {
            return (String) o;
        } else {
            Clob    clob = (Clob) o;

            try {
                return clob == null ? null : clob.getSubString (1, (int) clob.length ());
            } catch (SQLException e) {
                failure ("clob parse", e);
            }
            return null;
        }
    }
    public byte[] asBlob (Object o) {
        if (o == null) {
            return null;
        } else if (o.getClass ().getName ().equals ("[B")) {
            return (byte[]) o;
        } else {
            Blob    blob = (Blob) o;

            try {
                return blob == null ? null : blob.getBytes (1, (int) blob.length ());
            } catch (SQLException e) {
                failure ("blob parse", e);
            }
            return null;
        }
    }
    public Date asDate (Object o, Date ifNull) {
        return o != null ? (Date) o : ifNull;
    }
    public Date asDate (Object o) {
        return asDate (o, null);
    }
    public Timestamp asTimestamp (Object o, Timestamp ifNull) {
        return o != null ? (Timestamp) o : ifNull;
    }
    public Timestamp asTimestamp (Object o) {
        return asTimestamp (o, null);
    }
}
