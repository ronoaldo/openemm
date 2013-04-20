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
package org.agnitas.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.SQLNestedException;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class LoggingEnhBasicDataSource extends EnhBasicDataSource {
	private static final transient Logger logger = Logger.getLogger(LoggingEnhBasicDataSource.class);
	
	private static long	last=0;

	public static String	trace2string(StackTraceElement[] st) {
		String trace="";
		int	i=0;

		for(int c=0; c < st.length && i < 5; c++) {
			while(c < st.length) {
				if(st[c].toString().substring(4, 11).equals("agnitas")) {
					trace+=st[c].toString()+"\t";
					i++;
					break;
				}
				c++;
			}
		}
		return trace;
	}

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public class	LoggingObjectPool	extends	GenericObjectPool	{
		private Map	active=new HashMap();

		public void	dumpMap()	{
			GregorianCalendar	cal=new GregorianCalendar();
			long	cur=cal.getTimeInMillis();

			if(cur > last+3000 || this.getNumActive() > 30) {
				HashMap	act=new HashMap(active);
				Iterator i=act.keySet().iterator();
				logger.error("Connections: " + this.getNumActive() + " class " + this.getClass());
				logger.error("+++++++++++++++++++++++++++++");
				while(i.hasNext()) {
					Object con=(Object) i.next();
					StackTraceElement[] st=(StackTraceElement[])act.get(con);
					String trace=LoggingEnhBasicDataSource.trace2string(st);

					logger.error("Connection " + con.toString() + "(" + con.hashCode() + ") " + trace);
				}
				last=cur;
			}
		}

		public Object	borrowObject() throws Exception	{
			Exception	e=new Exception();
			dumpMap();
			Object	ret=super.borrowObject();

			active.put(ret, e.getStackTrace());
			return ret;
		}

		public void	invalidateObject(Object obj) throws Exception {
			super.invalidateObject(obj);
		}

		public void	returnObject(Object obj) throws Exception {
			active.remove(obj);
			super.returnObject(obj);
		}
	}

	private static void validateConnectionFactory(PoolableConnectionFactory connectionFactory) throws Exception {
         Connection conn = null;
         try {
             conn = (Connection) connectionFactory.makeObject();
             connectionFactory.activateObject(conn);
             connectionFactory.validateConnection(conn);
             connectionFactory.passivateObject(conn);
         }
         finally {
             connectionFactory.destroyObject(conn);
         }
     }

    /**
      * <p>Create (if necessary) and return the internal data source we are
      * using to manage our connections.</p>
      *
      * <p><strong>IMPLEMENTATION NOTE</strong> - It is tempting to use the
      * "double checked locking" idiom in an attempt to avoid synchronizing
      * on every single call to this method.  However, this idiom fails to
      * work correctly in the face of some optimizations that are legal for
      * a JVM to perform.</p>
      *
      * @exception SQLException if the object pool cannot be created.
      */
     protected synchronized DataSource createDataSource()
         throws SQLException {
 
         // Return the pool if we have already created it
         if (dataSource != null) {
             return (dataSource);
         }

logger.error("-------------------------------------------- Create new datasource");
         // Load the JDBC driver class
         if (driverClassName != null) {
             try {
                 Class.forName(driverClassName);
             } catch (Throwable t) {
                 String message = "Cannot load JDBC driver class '" +
                     driverClassName + "'";
                 logWriter.println(message);
                 t.printStackTrace(logWriter);
                 throw new SQLNestedException(message, t);
             }
         }
 
         // Create a JDBC driver instance
         Driver driver = null;
         try {
             driver = DriverManager.getDriver(url);
         } catch (Throwable t) {
             String message = "Cannot create JDBC driver of class '" +
                 (driverClassName != null ? driverClassName : "") + 
                 "' for connect URL '" + url + "'";
             logWriter.println(message);
             t.printStackTrace(logWriter);
             throw new SQLNestedException(message, t);
         }
 
         // Can't test without a validationQuery
         if (validationQuery == null) {
             setTestOnBorrow(false);
             setTestOnReturn(false);
             setTestWhileIdle(false);
         }
 
         // Create an object pool to contain our active connections
         connectionPool = new LoggingObjectPool();
         connectionPool.setMaxActive(maxActive);
         connectionPool.setMaxIdle(maxIdle);
         connectionPool.setMinIdle(minIdle);
         connectionPool.setMaxWait(maxWait);
         connectionPool.setTestOnBorrow(testOnBorrow);
         connectionPool.setTestOnReturn(testOnReturn);
         connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
         connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
         connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
         connectionPool.setTestWhileIdle(testWhileIdle);
         
         // Set up statement pool, if desired
         GenericKeyedObjectPoolFactory statementPoolFactory = null;
         if (isPoolPreparedStatements()) {
             statementPoolFactory = new GenericKeyedObjectPoolFactory(null, 
                         -1, // unlimited maxActive (per key)
                         GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL, 
                         0, // maxWait
                         1, // maxIdle (per key) 
                         maxOpenPreparedStatements); 
         }
 
         // Set up the driver connection factory we will use
         if (username != null) {
             connectionProperties.put("user", username);
         } else {
             logger.error("DBCP DataSource configured without a 'username'");
         }
         
         if (password != null) {
             connectionProperties.put("password", password);
         } else {
             logger.error("DBCP DataSource configured without a 'password'");
         }
         
         DriverConnectionFactory driverConnectionFactory =
             new DriverConnectionFactory(driver, url, connectionProperties);
 
         // Set up the poolable connection factory we will use
         PoolableConnectionFactory connectionFactory = null;
         try {
             connectionFactory =
                 new PoolableConnectionFactory(driverConnectionFactory,
                                               connectionPool,
                                               statementPoolFactory,
                                               validationQuery,
                                               defaultReadOnly,
                                               defaultAutoCommit,
                                               defaultTransactionIsolation,
                                               defaultCatalog,
                                               null);
             if (connectionFactory == null) {
                 throw new SQLException("Cannot create PoolableConnectionFactory");
             }
             validateConnectionFactory(connectionFactory);
         } catch (RuntimeException e) {
             throw e;
         } catch (Exception e) {
             throw new SQLNestedException("Cannot create PoolableConnectionFactory (" + e.getMessage() + ")", e);
         }
 
         // Create and return the pooling data source to manage the connections
         dataSource = new PoolingDataSource(connectionPool);
         ((PoolingDataSource) dataSource).setAccessToUnderlyingConnectionAllowed(isAccessToUnderlyingConnectionAllowed());
         dataSource.setLogWriter(logWriter);
         
         try {
             for (int i = 0 ; i < initialSize ; i++) {
                 connectionPool.addObject();
             }
         } catch (Exception e) {
             throw new SQLNestedException("Error preloading the connection pool", e);
         }
         
         return dataSource;
     }
}
