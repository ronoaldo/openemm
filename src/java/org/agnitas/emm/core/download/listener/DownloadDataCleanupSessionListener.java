package org.agnitas.emm.core.download.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.agnitas.emm.core.download.service.DownloadService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * HttpSessionListener to remove all download data and associated files
 * from the server.
 * 
 * @author md
 *
 */
public class DownloadDataCleanupSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated( HttpSessionEvent event) {
		// Nothing to be done here
	}

	@Override
	public void sessionDestroyed( HttpSessionEvent event) {
		// Get the application context
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext( event.getSession().getServletContext());

		// Get the DownloadService bean
		DownloadService downloadService = (DownloadService) ctx.getBean( "DownloadService");
		
		// Remove download data and associated files
		downloadService.removeAllDownloadData( event.getSession());
	}
}
