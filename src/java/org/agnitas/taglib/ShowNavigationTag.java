/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.taglib;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.agnitas.emm.extension.ExtensionSystem;
import org.agnitas.emm.extension.util.ExtensionUtils;
import org.apache.log4j.Logger;
import org.java.plugin.JpfException;
import org.java.plugin.registry.Extension;

/**
 *  Display the navigation for a page. the navigation is specified by a
 *  properties file in org.agnitas.util.properties.navigation in the format
 *  token_i, href_i, msg_i, where i determines the order in which navigation is
 *  displayed.
 *
 */

public class ShowNavigationTag extends BodyTagSupport {
	
	private static final Logger logger = Logger.getLogger( ShowNavigationTag.class);
	
	private String navigation;
	private String highlightKey;
	private String prefix;
	private String declaringPlugin;
	private String extension;

	private final List<NavigationData> navigationDataList = new Vector<NavigationData>();
	private Iterator<NavigationData> navigationDataIterator;
	private int navigationIndex;
	
	private static class NavigationData {
		private final String message;
		private final String token;
		private final String href;
		private final String plugin;
		private final String extension;
		
		public NavigationData( String message, String token, String href, String plugin, String extension) {
			this.message = message;
			this.token = token;
			this.href = href;
			this.plugin = plugin;
			this.extension = extension;
		}

		public String getMessage() { return message; }
		public String getToken() { return token; }
		public String getHref() { return href; }
		public String getPlugin() { return plugin; }
		public String getExtension() { return extension; }
		
		@Override
		public String toString() {
			return "message[" + getMessage() + "], token[" + getToken() + "], href[" + getHref() + "], plugin[" + ( plugin !=  null ? plugin : "") + "], extension[" + (extension != null ? extension : "") + "]";
		}
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}
	
	public void setHighlightKey(String highlightKey) {
		this.highlightKey = highlightKey;
	}

	public void setPlugin( String declaringPlugin) {
		this.declaringPlugin = declaringPlugin;
	}
	
	public void setExtension( String extension) {
		this.extension = extension;
	}
	
	@Override
	public int doStartTag() throws JspException {
		prepareNavigationData();
		
		this.navigationDataIterator = this.navigationDataList.iterator();
		this.navigationIndex = 0;

		if( this.prefix == null)
			this.prefix = "";
		
		if( this.navigationDataIterator.hasNext()) {
			setBodyAttributes();
			return EVAL_BODY_BUFFERED;
		} else
			return SKIP_BODY;
	}

	@Override
	public int doAfterBody() throws JspException {
		if( this.navigationDataIterator.hasNext()) {
			setBodyAttributes();
			return EVAL_BODY_BUFFERED; // EVAL_BODY_AGAIN;
		} else
			return SKIP_BODY;
	}
	
	@Override 
	public int doEndTag() throws JspException {
		 // Reset optional attribute value
		this.declaringPlugin = null;
		this.extension = null;
		
		// Emit body content
		try {
			BodyContent bodyContent = getBodyContent();

			if( bodyContent != null) {
				bodyContent.writeOut( getPreviousOut());
				bodyContent.clearBody();
			}			
		} catch( IOException e) {
			logger.error( e);
		}
		return EVAL_PAGE;
	}

	private void prepareNavigationData() {
		this.navigationDataList.clear();
		
		ResourceBundle resourceBundle = null;

		try {
    		if( declaringPlugin == null || declaringPlugin.equals( "")) {
    			String resNavPath = "navigation" + "." + navigation;
    			resourceBundle = ResourceBundle.getBundle( resNavPath);
    			prepareNavigationDataFromResourceBundle( resourceBundle, null, null);
    		} else {
				resourceBundle = getExtensionResourceBundle( declaringPlugin, navigation);
	    		prepareNavigationDataFromResourceBundle( resourceBundle, declaringPlugin, extension);
			}

    		prepareNavigationDataFromExtensionPoints( resourceBundle);
    		
		} catch( JpfException e) {
			logger.error( "Error preparing navigation data from extension", e);
		}
	}
	
	private ResourceBundle getExtensionResourceBundle( String extension, String resourceName) throws JpfException {
		ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( pageContext.getServletContext());
		
		return extensionSystem.getPluginResourceBundle( extension, resourceName);
	}
	
	private void prepareNavigationDataFromResourceBundle( ResourceBundle resourceBundle, String plugin, String extension) {
        String tokenKey;
        String hrefKey;
        String msgKey;
        
        NavigationData navigationData;
        
        if( logger.isDebugEnabled())
        	logger.debug( "Processing navigation resource bundle for plugin: " + (plugin != null ? plugin : "core system"));
        
		for( int i = 1;; i++) {
			tokenKey = "token_" + i;
			hrefKey = "href_" + i;
			msgKey = "msg_" + i;
			
			if( !resourceBundle.containsKey( tokenKey)) {
				break;
			}
			
			if( logger.isInfoEnabled()) {
				logger.info( "extension '" + extension + "' in plugin '" + plugin + "' added menu item. Label key is: " + msgKey);
			}
			
			navigationData = new NavigationData( resourceBundle.getString( msgKey), resourceBundle.getString( tokenKey), resourceBundle.getString( hrefKey), plugin, extension);
			this.navigationDataList.add( navigationData);			
		}
	}
	
	private void prepareNavigationDataFromExtensionPoints( ResourceBundle resourceBundle) {
		if( !resourceBundle.containsKey( "navigation.plugin") || !resourceBundle.containsKey( "navigation.extensionpoint")) 
			return;

		//getting data from navigation.property file for the extension point
		String plugin = resourceBundle.getString( "navigation.plugin");
		String extensionPoint = resourceBundle.getString( "navigation.extensionpoint");
		
		ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( pageContext.getServletContext());
		Collection<Extension> extensions = extensionSystem.getActiveExtensions( plugin, extensionPoint);
		
		for( Extension extension : extensions) {
			String resourceName = extension.getParameter( "navigation-bundle").valueAsString();
			
			ResourceBundle extensionBundle = extensionSystem.getPluginResourceBundle( extension.getDeclaringPluginDescriptor().getId(), resourceName);
			prepareNavigationDataFromResourceBundle(extensionBundle, extension.getDeclaringPluginDescriptor().getId(), extension.getId());
		}
	}
	
	private void setBodyAttributes() {
		NavigationData navigationData = this.navigationDataIterator.next();
		navigationIndex++;
		
		logger.info( "setting navigation attributes " + prefix + "_navigation_href = " + navigationData.getHref());
		
        if( navigationData.getMessage().equals(highlightKey) ){
            pageContext.setAttribute( prefix + "_navigation_switch", "on");
            pageContext.setAttribute( prefix + "_navigation_isHighlightKey", Boolean.TRUE);
        } else {
            pageContext.setAttribute( prefix + "_navigation_switch", "off");
            pageContext.setAttribute( prefix + "_navigation_isHighlightKey", Boolean.FALSE);
        }

        pageContext.setAttribute( prefix + "_navigation_token", navigationData.getToken().trim());
        pageContext.setAttribute( prefix + "_navigation_href",  navigationData.getHref().trim());
        pageContext.setAttribute( prefix + "_navigation_navMsg", navigationData.getMessage().trim());
        pageContext.setAttribute( prefix + "_navigation_index", Integer.valueOf(this.navigationIndex));
        pageContext.setAttribute( prefix + "_navigation_plugin", navigationData.getPlugin() != null ? navigationData.getPlugin().trim() : "");
        pageContext.setAttribute( prefix + "_navigation_extension", navigationData.getExtension() != null ? navigationData.getExtension().trim() : "");
	}
}


