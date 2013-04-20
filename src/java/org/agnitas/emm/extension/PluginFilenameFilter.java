package org.agnitas.emm.extension;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filename filter for locating plugins.
 * 
 * @author md
 */
public class PluginFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept( File dir, String name) {
		File file = new File( dir + File.separator + name);
		
		if( file.isFile()) {
			String name0 = name.toLowerCase();
			
			// Accept all standard archives (".jar" and ".zip") and plugin archives (".par", same as ".jar")
			return name0.endsWith(".par") || name0.endsWith(".jar") || name0.endsWith( ".zip");
		} else {
			// Accept all directories
			return true;
		}
	}

}
