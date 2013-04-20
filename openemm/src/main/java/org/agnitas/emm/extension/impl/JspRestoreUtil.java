package org.agnitas.emm.extension.impl;

import java.io.File;
import java.io.IOException;

import org.agnitas.util.FileUtils;
import org.apache.log4j.Logger;

public class JspRestoreUtil {
	
	private static final transient Logger logger = Logger.getLogger( JspRestoreUtil.class);
	
	private final ExtensionSystemConfiguration configuration;
	
	JspRestoreUtil( ExtensionSystemConfiguration configuration) {
		this.configuration = configuration;
	}
	
	void createWorkingJspsFromBackupDirectory( String pluginId) throws IOException {
		if( logger.isInfoEnabled()) {
			logger.info( "creating working JSP for plugin " + pluginId);
		}

		// Check, if directory exists
		File jspBackupDir = new File( this.configuration.getJspBackupDirectory( pluginId));
		if( !jspBackupDir.exists()) {
			// Directory does not exist, so exit
			if( logger.isInfoEnabled())
				logger.info( "No backup directory found for JSPs of plugin " + pluginId);
			
			return;
		}
			
		
		copyDirectoryContentRecursively( this.configuration.getJspBackupDirectory( pluginId), this.configuration.getJspWorkingDirectory(pluginId));
	}
	
	private void copyDirectoryContentRecursively( String sourceBaseDirectory, String destinationBaseDirectory) throws IOException {
		copyDirectoryContentRecursively( new File( sourceBaseDirectory), new File( destinationBaseDirectory));
	}
	
	private void copyDirectoryContentRecursively( File sourceDirectory, File destinationDirectory) throws IOException {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "copy recursivly from " + sourceDirectory.getAbsolutePath() + " to " + destinationDirectory.getAbsolutePath());
		}
		
		destinationDirectory.mkdirs();
		
		File[] content = sourceDirectory.listFiles();
	
		for( File file : content) {
			if( file.isFile()) {
				if( logger.isDebugEnabled()) {
					logger.debug( "copying file " + file.getAbsolutePath());
				}
				
				FileUtils.copyFileToDirectory( file, destinationDirectory);
			} else if( file.isDirectory()) {
				if( logger.isDebugEnabled()) {
					logger.debug( "visiting directory " + file.getAbsolutePath());
				}
				
				copyDirectoryContentRecursively( file.getAbsolutePath(), destinationDirectory.getAbsolutePath() + File.separator + file.getName());
				
				if( logger.isDebugEnabled()) {
					logger.debug( "left directory " + file.getAbsolutePath());
				}
			}
		}
	}
	
}
