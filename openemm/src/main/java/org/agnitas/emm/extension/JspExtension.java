package org.agnitas.emm.extension;

import javax.servlet.jsp.PageContext;

import org.agnitas.emm.extension.exceptions.JspExtensionException;
import org.java.plugin.registry.Extension;

/**
 * Interface for an plugin registered at an JSP extension point.
 * 
 * JSP extension points allow multiple extension to register and are normally used
 * for additional output of an existing JSP.
 * 
 * @author md
 */
public interface JspExtension {
	/**
	 * Entry point. 
	 * 
	 * @param extension the Extension instance for the JspExtension
	 * @param pageContext pageContext from JSP tag
	 * 
	 * @throws JspExtensionException on errors
	 */
	public void invoke( Extension extension, PageContext pageContext) throws JspExtensionException;
}
