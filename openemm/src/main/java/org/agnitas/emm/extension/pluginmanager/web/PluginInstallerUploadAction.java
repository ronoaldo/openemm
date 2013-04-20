package org.agnitas.emm.extension.pluginmanager.web;

import java.io.File;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.emm.extension.ExtensionSystem;
import org.agnitas.emm.extension.exceptions.MissingPluginManifestException;
import org.agnitas.emm.extension.pluginmanager.form.PluginInstallerSelectForm;
import org.agnitas.emm.extension.util.ExtensionUtils;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

public class PluginInstallerUploadAction extends DispatchAction {

	private static final transient Logger logger = Logger.getLogger( PluginInstallerUploadAction.class);
	
	public ActionForward upload( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if( logger.isInfoEnabled()) {
			logger.info( "Handling uploaded plugin file");
		}

        if(!AgnUtils.isUserLoggedIn(request)) {
        	if( logger.isInfoEnabled())
        		logger.info( "Not authentication information. Forwarding to login");
        	
        	return mapping.findForward("logon");
        }

		PluginInstallerSelectForm pluginSelectForm = (PluginInstallerSelectForm) form;
		ExtensionSystem extensionSystem = ExtensionUtils.getExtensionSystem( request);
		
		try {
			InputStream inputStream = pluginSelectForm.getPluginFile().getInputStream();
			
			try {
				File temporaryFile = FileUtils.streamToTemporaryFile( inputStream, pluginSelectForm.getPluginFile().getFileSize(), "emm-jpf-plugin-");
			
				try {
					if( logger.isInfoEnabled()) {
						logger.info( "Installing plugin from file: " + temporaryFile.getAbsolutePath());
					}
	
					extensionSystem.installPlugin( temporaryFile.getAbsolutePath());
					
					ActionMessages messages = new ActionMessages();
					messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "pluginmanager.installer.installed"));
					saveMessages( request, messages);
				} finally {
					temporaryFile.delete();
				}
			} finally {
				inputStream.close();
			}
			
		} catch( MissingPluginManifestException e) {
			ActionMessages errors = new ActionMessages();
			errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.installer.manifest"));
			saveErrors( request, errors);
			
			logger.warn( "Cannot install plugin - missing manifest?", e);
		} catch( Exception e) {
			ActionMessages errors = new ActionMessages();
			errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.installer.general"));
			saveErrors( request, errors);
			
			logger.warn( "Cannot install plugin", e);
		} finally {
			logger.info( "Releasing temporary file");
			// Release resources used by the temporary file
			pluginSelectForm.getPluginFile().destroy();
		}
		
		return mapping.findForward("list");
	}
}
