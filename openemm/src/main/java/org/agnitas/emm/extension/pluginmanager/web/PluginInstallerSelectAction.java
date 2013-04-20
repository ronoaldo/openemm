package org.agnitas.emm.extension.pluginmanager.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class PluginInstallerSelectAction extends Action { // TODO: Can this Action be replaced by a simple ForwardAction??? (-> struts-config.xml)

	private static final transient Logger logger = Logger.getLogger( PluginInstallerSelectAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if( logger.isDebugEnabled())
			logger.debug( "Entering select()");

        if(!AgnUtils.isUserLoggedIn(request)) {
        	if( logger.isInfoEnabled())
        		logger.info( "Not authentication information. Forwarding to login");
        	
        	return mapping.findForward("logon");
        }

		
		return mapping.findForward( "select");
	}

}
