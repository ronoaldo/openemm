package org.agnitas.emm.extension.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.emm.extension.ExtensionSystem;
import org.agnitas.emm.extension.exceptions.ExtensionException;
import org.agnitas.emm.extension.exceptions.PluginInstantiationException;
import org.agnitas.emm.extension.exceptions.UnknownPluginException;
import org.agnitas.emm.extension.util.ExtensionConstants;
import org.agnitas.emm.extension.util.ExtensionUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet for invoking EmmFeatureExtensions.
 * 
 * The servlet selects an EmmFeatureExtension and invokes its <i>invoke</i> method.
 * 
 * @author md
 */
public class ExtensionSetupServlet extends HttpServlet {

	/** Logger */
	private static final Logger logger = Logger.getLogger( ExtensionSetupServlet.class);
	
	@Override
	protected void service( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( this.getServletContext());
			
			WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext( this.getServletContext());
			
			String feature = request.getParameter( ExtensionConstants.FEATURE_REQUEST_PARAMETER); 

			extensionSystem.invokeFeatureSetupExtension( feature, applicationContext, request, response);
		} catch (PluginInstantiationException e) {
			logger.error( e);
		} catch (ExtensionException e) {
			logger.error( e);
		} catch (UnknownPluginException e) {
			logger.error( e);
		}
	}


}
