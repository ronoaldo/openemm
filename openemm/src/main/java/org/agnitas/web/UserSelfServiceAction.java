package org.agnitas.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.agnitas.beans.Admin;
import org.agnitas.dao.AdminDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.AdminForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

/**
 * Implementation of <strong>Action</strong> that lets an user change his password and other profiledata.
 *
 * @author Andreas Soderer (aso)
 * @version 14.03.2012
 */
public class UserSelfServiceAction extends DispatchAction {
	private static final transient Logger logger = Logger.getLogger(UserSelfServiceAction.class);
	
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	protected AdminDao adminDao;
	
	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

    /**
     * Loads admin data from database into form.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form   The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response  The HTTP response we are creating
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if some exception occurs
     */
	public ActionForward showChangeForm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {		
		if (form == null || !(form instanceof AdminForm)) {
			throw new RuntimeException("Invalid Form for showChangeForm in UserSelfServiceAction");
		}
		
		Admin admin = AgnUtils.getAdmin(request);
		if (admin == null) {
			return mapping.findForward("logon");
		}
		
		fillAdminFormWithOriginalValues((AdminForm) form, admin);

		return mapping.findForward("show");
	}

    /**
     * Validates admin full name and admin password form data.
     * If admin name is empty, shows error message of wrong admin name.
     * If admin password is empty or differs from the one stored in session, shows error message of password mismatch.
     * If admin name and admin password are ok, updates admin entry data in database and stores the updated data in
     * current session.
     * Forwards to admin view page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form   The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response  The HTTP response we are creating
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if some exception occurs
     */
	
	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    ActionMessages errors = new ActionMessages();
		
		if (form == null || !(form instanceof AdminForm)) {
			throw new RuntimeException("Invalid Form for showChangeForm in UserSelfServiceAction");
		}
		
		Admin admin = AgnUtils.getAdmin(request);
		if (admin == null) {
			return mapping.findForward("logon");
		}
		
		try {
			AdminForm adminForm = (AdminForm) form;
			
			// Set new Fullname
			if (StringUtils.isNotBlank(adminForm.getFullname())) {
				admin.setFullname(adminForm.getFullname());
			}
			else {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.invalid.username"));
			}
			
			// Set new Password
			if (StringUtils.isNotEmpty(adminForm.getPassword())) {
				// Only change if user entered a new password
				if (!adminForm.getPassword().equals(adminForm.getPasswordConfirm())) {
					adminForm.setPassword("");
					adminForm.setPasswordConfirm("");
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.password.mismatch"));
				}
				else {
					admin.setPassword(adminForm.getPassword());
				}
			}
			
			// Set new Language and Country
			admin.setAdminLang(adminForm.getAdminLocale().getLanguage());
			admin.setAdminCountry(adminForm.getAdminLocale().getCountry());
			
			// Set new Timezone
			admin.setAdminTimezone(adminForm.getAdminTimezone());
			
			// Set new default listlength
			admin.setPreferredListSize(adminForm.getNumberofRows());
		} catch (Exception e) {
			logger.error("UserSelfServiceAction.save: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
		}
		
		if (errors.isEmpty()) {
			adminDao.save(admin);
			
			// Set the new values for this session
			HttpSession session = request.getSession();
			session.setAttribute("emm.admin", admin);
			session.setAttribute("emm.locale", admin.getLocale());
			session.setAttribute(org.apache.struts.Globals.LOCALE_KEY, admin.getLocale());
			
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
			saveMessages(request, messages);
			return mapping.findForward("show");
		}
		else {
			// Revert Admin Data Changes
			AgnUtils.setAdmin(request, adminDao.getAdmin(admin.getAdminID(), admin.getCompanyID()));
			
			saveErrors(request, errors);
			
			return mapping.findForward("show");
		}
	}
	
    /**
     * Load an admin account.
     * Loads the data of the admin from the database and stores it in the
     * form.
     *
     * @param adminForm AdminForm object
     * @param admin  Admin bean object
     */
	protected void fillAdminFormWithOriginalValues(AdminForm adminForm, Admin admin) {        
        adminForm.setAdminID(admin.getAdminID());
        adminForm.setUsername(admin.getUsername());
        adminForm.setPassword("");
        adminForm.setPasswordConfirm("");
        adminForm.setCompanyID(admin.getCompanyID());
        adminForm.setFullname(admin.getFullname());
        adminForm.setAdminLocale(new Locale(admin.getAdminLang(), admin.getAdminCountry()));
        adminForm.setAdminTimezone(admin.getAdminTimezone());
        adminForm.setLayoutID(admin.getLayoutID());
        adminForm.setUserRights(admin.getAdminPermissions());
        adminForm.setGroupID(admin.getGroup().getGroupID());
        adminForm.setGroupRights(admin.getGroup().getGroupPermissions());
        adminForm.setNumberofRows(admin.getPreferredListSize());
		
		if (logger.isDebugEnabled()) logger.debug("loadAdmin: admin " + adminForm.getAdminID() + " loaded");
    }
}
