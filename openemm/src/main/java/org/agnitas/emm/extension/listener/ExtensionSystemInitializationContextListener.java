package org.agnitas.emm.extension.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.agnitas.emm.extension.dao.PluginDao;
import org.agnitas.emm.extension.impl.ExtensionSystemBuilder;
import org.agnitas.emm.extension.impl.ExtensionSystemImpl;
import org.agnitas.emm.extension.util.ExtensionConstants;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This context listener instantiates the extension system for
 * a signle web application. 
 * 
 * @author md
 */
public class ExtensionSystemInitializationContextListener implements ServletContextListener {

	private static final transient Logger logger = Logger.getLogger( ExtensionSystemInitializationContextListener.class);

	@Override
	public void contextInitialized( ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		
		if( servletContext.getAttribute( ExtensionConstants.EXTENSION_SYSTEM_APPLICATION_SCOPE_ATTRIBUTE) != null) {
			logger.fatal( "Extension system already initialized for the application context");
		} else {
			try {
				ExtensionSystemImpl extensionSystem = createExtensionSystem( servletContext);
				servletContext.setAttribute( ExtensionConstants.EXTENSION_SYSTEM_APPLICATION_SCOPE_ATTRIBUTE, extensionSystem);
				
				extensionSystem.startUp();
			} catch( Exception e) {
				logger.fatal( "Error initializing extension system", e);				
			}
		}
	}

	protected ExtensionSystemImpl createExtensionSystem( ServletContext servletContext) throws Exception {
		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext( servletContext);
		
		ExtensionSystemBuilder extensionSystemBuilder = createExtensionSystemBuilder();
		setBuilderProperties( extensionSystemBuilder, servletContext, springContext);

		return extensionSystemBuilder.createExtensionSystem();
	}
	
	protected void setBuilderProperties( ExtensionSystemBuilder extensionSystemBuilder, ServletContext servletContext, WebApplicationContext springContext) throws Exception {
		// Directories
		extensionSystemBuilder.setJspBaseDirectory( servletContext.getRealPath( "plugins"));
		extensionSystemBuilder.setPluginBaseDirectory( AgnUtils.getDefaultValue( "plugins.home"));
		extensionSystemBuilder.setSystemPluginBaseDirectory( servletContext.getRealPath( "/WEB-INF/system-plugins"));
		
		// Misc. values
		extensionSystemBuilder.setDatabaseName( determineDatabaseName());
		
		// Used components
		extensionSystemBuilder.setDataSource( (DataSource) springContext.getBean( "dataSource"));
		extensionSystemBuilder.setPluginDao( (PluginDao) springContext.getBean( "pluginDao"));
	}
	
	protected String determineDatabaseName() {
		if( AgnUtils.isMySQLDB()) {
			return "mysql";
		} else if( AgnUtils.isOracleDB()) {
			return "oracle";
		} else {
			// TODO: Fix this by providing other database formats as backends
			logger.warn("Unable to determine database backend. Make sure you're using a MySQL compatible database.");
			return "mysql";
		}
	}
	
	@Override
	public void contextDestroyed( ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		ExtensionSystemImpl extensionSystem = (ExtensionSystemImpl) servletContext.getAttribute( ExtensionConstants.EXTENSION_SYSTEM_APPLICATION_SCOPE_ATTRIBUTE);
		
		if( extensionSystem != null) {
			logger.info( "Shutting down ExtensionSystem");
			extensionSystem.shutdown();
			logger.info( "ExtensionSystem is shut down");
		}
	}
	
	protected ExtensionSystemBuilder createExtensionSystemBuilder() {
		return new ExtensionSystemBuilder();
	}
}
