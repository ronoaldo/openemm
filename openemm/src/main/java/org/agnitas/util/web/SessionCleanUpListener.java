package org.agnitas.util.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * in case of an error the webcontainer restarts itself. It could happen that the persisted sessions will move to an other user...
 * It will be better to cleanup the existing session before startup
 * @author ms
 *
 */
public class SessionCleanUpListener implements ServletContextListener {
	private static final transient Logger logger = Logger.getLogger(SessionCleanUpListener.class);

	private static final String SESSIONFILESTORE = "sessionfilestore";

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String sessionfilestoreLocation = event.getServletContext().getRealPath((String) event.getServletContext().getInitParameter(SESSIONFILESTORE));
		if (sessionfilestoreLocation != null) {
			File sessionfilestoreDirectory = new File(sessionfilestoreLocation);
			if (sessionfilestoreDirectory.exists() && sessionfilestoreDirectory.isDirectory()) {
				try {
					FileUtils.deleteDirectory(sessionfilestoreDirectory);
				} catch (IOException exception) {
					logger.fatal("Sessionfilestore Cleanup: Could not delete sessionfilestore: " + sessionfilestoreLocation, exception);
				}
			} else if (logger.isInfoEnabled()) {
				logger.info("Sessionfilestore Cleanup: The provided context-parameter 'sessionfilestore' <" + sessionfilestoreLocation + "> does not exist or is not a directory");
			}
		}
	}
}
