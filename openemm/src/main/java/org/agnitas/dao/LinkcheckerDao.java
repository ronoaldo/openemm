package org.agnitas.dao;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextAware;

public interface LinkcheckerDao extends ApplicationContextAware {

	/**
	 * This method returns the timeout for ONE Link, not all together.
	 * @return timeout in mls.
	 */
	public int getLinkTimeout();
	
	/**
	 * returns the amount of parallel threads which are started to check
	 * if a link is valid. If the value is 50, then up to 50 links are parallel checked.
	 * @return amount of parallel threads.
	 */
	public int getThreadCount();

    /**
     * Getter for property dataSource.
     *
     * @return Value of property dataSource.
     */
	public DataSource getDataSource();

    /**
     * Setter for property dataSource.
     *
     * @param dataSource
     *          New value of property dataSource.
     */
	public void setDataSource(DataSource dataSource);
}
