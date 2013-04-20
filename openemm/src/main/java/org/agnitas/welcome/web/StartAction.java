package org.agnitas.welcome.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

public class StartAction extends DispatchAction {

	public ActionForward start(ActionMapping mapping , ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return mapping.findForward("startpage");
	}
	
	@Override
	protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return start(mapping, form, request, response);
	}
}
