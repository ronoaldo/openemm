package org.agnitas.emm.extension.pluginmanager.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.agnitas.emm.extension.ExtensionSystem;
import org.agnitas.emm.extension.exceptions.RemovingSystemPluginNotAllowedException;
import org.agnitas.emm.extension.util.ExtensionUtils;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.BaseDispatchAction;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class PluginManagerAction extends BaseDispatchAction {
	
	private static final transient Logger logger = Logger.getLogger( PluginManagerAction.class);
	
	public ActionForward list( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!AgnUtils.isUserLoggedIn(request)) {
        	if( logger.isInfoEnabled())
        		logger.info( "Not authentication information. Forwarding to login");
        	
        	return mapping.findForward("logon");
        }

        ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( request);
		
		request.setAttribute( "pluginStatusReport", extensionSystem.getPluginStatusReport());

        initTableParams(7, (StrutsFormBase) form);
		
		return mapping.findForward( "list");
	}

	public ActionForward detail( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!AgnUtils.isUserLoggedIn(request)) {
        	if( logger.isInfoEnabled())
        		logger.info( "Not authentication information. Forwarding to login");
        	
        	return mapping.findForward("logon");
        }

        String pluginID = request.getParameter( "plugin");
		if( pluginID == null || pluginID.equals( "")) {
			ActionMessages messages = new ActionMessages();
			
			messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.missing_plugin_id"));
			saveErrors( request, messages);
			
			return list( mapping, form, request, response);
		} else {
			ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( request);
			request.setAttribute( "pluginDetail", extensionSystem.getPluginDetails( pluginID));

			return mapping.findForward( "detail");
		}
	}
	
	public ActionForward deactivate( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!AgnUtils.isUserLoggedIn(request)) {
        	if( logger.isInfoEnabled())
        		logger.info( "Not authentication information. Forwarding to login");
        	
        	return mapping.findForward("logon");
        }

        String pluginID = request.getParameter( "plugin");
		if( pluginID == null || pluginID.equals( "")) {
			ActionMessages messages = new ActionMessages();
			
			messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.missing_plugin_id"));
			saveErrors( request, messages);
			
			return list( mapping, form, request, response);
		} else {
			ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( request);
			extensionSystem.deactivatePluginForStartup( pluginID);
            showSavedMessage(request);

			return detail( mapping, form, request, response);
		}		
	}

    public ActionForward activate( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!AgnUtils.isUserLoggedIn(request)) {
        	if( logger.isInfoEnabled())
        		logger.info( "Not authentication information. Forwarding to login");
        	
        	return mapping.findForward("logon");
        }

        String pluginID = request.getParameter( "plugin");
		if( pluginID == null || pluginID.equals( "")) {
			ActionMessages messages = new ActionMessages();
			
			messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.missing_plugin_id"));
			saveErrors( request, messages);
			
			return list( mapping, form, request, response);
		} else {
			ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( request);
			extensionSystem.activatePluginForStartup( pluginID);
			showSavedMessage(request);

			return detail( mapping, form, request, response);
		}		
	}
	
	public ActionForward uninstall( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!AgnUtils.isUserLoggedIn(request)) {
        	if( logger.isInfoEnabled())
        		logger.info( "Not authentication information. Forwarding to login");
        	
        	return mapping.findForward("logon");
        }

        String pluginID = request.getParameter( "plugin");
		if( pluginID == null || pluginID.equals( "")) {
			ActionMessages messages = new ActionMessages();
			
			messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.missing_plugin_id"));
			saveErrors( request, messages);
			
			return list( mapping, form, request, response);
		} else {
			ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( request);
			
			try {
				extensionSystem.uninstallPlugin( pluginID);

				ActionMessages messages = new ActionMessages();
				messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "pluginmanager.plugin.uninstalled"));
				saveMessages( request, messages);
			} catch( RemovingSystemPluginNotAllowedException e) {
				ActionMessages messages = new ActionMessages();
				messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.uninstall.systemplugin"));
				saveErrors( request, messages);
			} catch( Exception e) {
				ActionMessages messages = new ActionMessages();
				messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.uninstall"));
				saveErrors( request, messages);
			} 
			
			return list( mapping, form, request, response);
		}		
		
	}
}
