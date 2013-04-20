package org.agnitas.emm.extension.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.agnitas.emm.extension.ExtensionSystem;
import org.agnitas.emm.extension.util.ExtensionUtils;

public class JspExtensionPointTag extends TagSupport {

	/** Name of plugin providing extension point. */
	private String plugin;
	
	/** Name of extension point provided by plugin. */
	private String point;
	
	public void setPlugin( String plugin) {
		this.plugin = plugin;
	}
	
	public void setPoint( String point) {
		this.point = point;
	}
	
	@Override
	public int doEndTag() throws JspException {
		ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( pageContext.getServletContext());
		extensionSystem.invokeJspExtension( plugin, point, this.pageContext);

		return TagSupport.EVAL_PAGE;
	}

}
