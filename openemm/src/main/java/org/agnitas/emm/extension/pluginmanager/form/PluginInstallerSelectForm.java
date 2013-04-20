package org.agnitas.emm.extension.pluginmanager.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

public class PluginInstallerSelectForm extends ActionForm {

	private FormFile pluginFile;
	
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if( pluginFile == null || pluginFile.getFileName() == null || pluginFile.getFileName().equals( "")) {
			errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.pluginmanager.installer.no_plugin_file"));
		}

		return errors;
	}

	public void setPluginFile( FormFile pluginFile) {
		this.pluginFile = pluginFile;
	}
	
	public FormFile getPluginFile() {
		return this.pluginFile;
	}
}
