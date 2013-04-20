package org.agnitas.util.quartz;

import org.agnitas.dao.LoginTrackDao;
import org.agnitas.util.AgnUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class LoginTrackTableCleanerJob extends QuartzJobBean {
	/**
	 * Default value of retention time (in days) for old records.
	 */
	public static final int DEFAULT_RETENTION_TIME = 90;
	
	/**
	 * Number of records deleted with one statement.
	 */
	public static final int DEFAULT_DELETE_BLOCK_SIZE = 1000;
	
	/**
	 * Retention time for older record.
	 */
	protected int retentionTime = DEFAULT_RETENTION_TIME;
	
	/**
	 * Number of records deleted with one statement. 
	 */
	protected int deleteBlockSize = DEFAULT_DELETE_BLOCK_SIZE;
	
	/**
	 * DAO for login_track_tbl;
	 */
	protected LoginTrackDao loginTrackDao;
	
	/**
	 * Set new retention time for old records. 
	 * @param retentionTime new hold-back time in days
	 */
	public void setRetentionTime( int retentionTime) {
		this.retentionTime = retentionTime;
	}

	/**
	 * Set number of maximum records to be deleted with one statement.
	 * 
	 * @param size maximum number of deleted records
	 */
	public void setDeleteBlockSize( int size) {
		this.deleteBlockSize = size;
	}
	
	/**
	 * Set LoginTrackDao object for accessing login tracking records.
	 * 
	 * @param loginTrackDao LoginTrackDao instance
	 */
	public void setLoginTrackDao( LoginTrackDao loginTrackDao) {
		this.loginTrackDao = loginTrackDao;
	}
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		int affectedRows;
		
		if(this.loginTrackDao == null) {
			AgnUtils.logger().error("no LoginTrackDao object defined - job stopped");
			return;
		}
		
		// Delete in blocks 
		while((affectedRows = loginTrackDao.deleteOldRecords(this.retentionTime, this.deleteBlockSize)) > 0) {
			AgnUtils.logger().info("deleted " + affectedRows + " records");
		}
	}
}
