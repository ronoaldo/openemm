package org.agnitas.emm.extension.taglib;

import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.agnitas.emm.extension.util.ExtensionUtils;
import org.agnitas.emm.extension.util.I18NResourceBundle;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;

/**
 * Tag to create a adapter layer between the plugin and the MVC framework (here Struts is used). 
 * @author md
 *
 */
public class ExtensionI18NTag extends TagSupport {

	private static final transient Logger logger = Logger.getLogger( ExtensionI18NTag.class);
	
	private String plugin;
	private String key;
	
	public void setPlugin( String plugin) {
		this.plugin = plugin;
	}
	
	public void setKey( String key) {
		this.key = key;
	}
	
	@Override
	public int doStartTag() throws JspException {

		try {
			I18NResourceBundle bundle = ExtensionUtils.getExtensionSystem( pageContext.getServletContext()).getPluginI18NResourceBundle( plugin);
			
			if( bundle != null) {
				// This code uses Struts
				String translation = bundle.getMessage( key, getUserLocale());
				
				if( translation != null) {
					pageContext.getOut().print( translation);
				} else {
					pageContext.getOut().print( "?? Missing key " + key + " for plugin " + plugin + " ??");
					logger.warn( "Key '" + key + "' not defined in i18n bundle for plugin '" + plugin + "'");
				}
			} else {
				logger.warn( "No i18n bundle for plugin '" + plugin + "' defined");
			}
		} catch( Exception e) {
			logger.error( "Error handling i18n for plugin '" + plugin + "', key '" + key + "'", e);
		}
		
		return TagSupport.SKIP_BODY;
	}
	
	private Locale getUserLocale() {
		// This is (currently) MVC-framework specific code.
		return TagUtils.getInstance().getUserLocale( pageContext, Globals.LOCALE_KEY);
	}
}
