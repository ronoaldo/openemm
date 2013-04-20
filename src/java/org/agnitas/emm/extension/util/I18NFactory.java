package org.agnitas.emm.extension.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class I18NFactory {
	private static final transient Logger logger = Logger.getLogger( I18NFactory.class);
	
	private final ClassLoader classLoader;
	private final String bundleName;
	
	public I18NFactory( ClassLoader classLoader, String bundleName) {
		this.classLoader = classLoader;
		this.bundleName = bundleName;
	}

	public ResourceBundle getMessages( String i18nPrefix) {
		String fullName = (i18nPrefix.equals( "") ? bundleName : bundleName + "_" + i18nPrefix) + ".properties";
		
		InputStream stream = classLoader.getResourceAsStream( fullName);
		
		if( stream != null) {
    		try {
        		try {
        			PropertyResourceBundle bundle = new PropertyResourceBundle( stream);
        			return bundle;
        		} finally {				
        			stream.close();
        		}
    		} catch( IOException e) {
    			logger.error( "Error reading i18n bundle '" + fullName + "'", e);
    			
    			return null;
    		}
		} else {
			logger.info( "No i18n bundle '" + fullName + "'");
			
			return null;
		}
	}
}
