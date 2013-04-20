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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.impl.ImportProfileImpl;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.web.forms.ImportProfileForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Action that handles import profile actions: view, edit, remove, list,
 * manage gender mappings.
 *
 * @author Vyacheslav Stepanov
 */
public class ImportProfileAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(ImportProfileAction.class);

    public static final int ACTION_NEW_GENDER = ACTION_LAST + 1;
    public static final int ACTION_REMOVE_GENDER = ACTION_LAST + 2;
    public static final int ACTION_SET_DEFAULT = ACTION_LAST + 3;

    public static final String IMPORT_PROFILE_ERRORS_KEY = "import-profile-errors";
    public static final String IMPORT_PROFILE_ID_KEY = "import-profile-id";

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
	 * ACTION_LIST: loads list of profile fields and default profile field into request and forwards to profile
     *     field list page.
	 * <br><br>
     * ACTION_NEW_GENDER: saves gender mappings converting that from sequence if new gender is not empty and
     *     forwards to profile field view page.
	 * <br><br>
     * ACTION_REMOVE_GENDER: removes gender mapping from profile field and forwards to profile field view page.
	 * <br><br>
     * ACTION_VIEW: loads data of chosen profile field into form and forwards to profile field view page.
	 * <br><br>
	 * ACTION_SAVE: checks, if profile field with entered name already exists. Saves profile field data or creates
     *     a new one and forwards to profile field view page.
	 * <br><br>
     * ACTION_NEW: creates empty instance of profile field and forwards to profile field view page.<br>
     * Supported modes for import: <br>
     *      ImportMode.ADD (adds only new recipients)
     *          requires "import.mode.add" permission<br>
     *      ImportMode.ADD_AND_UPDATE (adds new recipients and update existing recipients)
     *          requires "import.mode.add_update" permission<br>
     *      ImportMode.UPDATE (only updates existing recipients)
     *          requires "import.mode.only_update" permission<br>
     *      ImportMode.MARK_OPT_OUT (marks recipients as Opt Out)
     *          requires "import.mode.unsubscribe" permission<br>
     *      ImportMode.MARK_BOUNCED (marks recipients as Bounced)
     *          requires "import.mode.bounce" permission<br>
     *      ImportMode.TO_BLACKLIST (adds recipients to blacklist)
     *          requires "import.mode.blacklist" permission
	 * <br><br>
     * ACTION_SET_DEFAULT: saves the default profile id for admin in database and in session.
     *     Forwards to profile field list page.
	 * <br><br>
	 * ACTION_CONFIRM_DELETE: loads profile field data and forwards to jsp with question to confirm deletion.
	 * <br><br>
	 * ACITON_DELETE: deletes profile field and forwards to profile field list page.
	 * <br><br>
     * @param mapping The ActionMapping used to select this instance
     * @param form    The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing<br>
     *     If the request parameter "addGender" is set - changes action to ACTION_REMOVE_GENDER.<br>
     *     If the request parameter "removeGender" is set - changes action to ACTION_REMOVE_GENDER.<br>
     *     If the request parameter "setDefault" is set - changes action to ACTION_SET_DEFAULT.<br>
     * @param res     The HTTP response we are creating
     * @throws java.io.IOException            if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        ImportProfileForm aForm;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;

        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        if (form != null) {
            aForm = (ImportProfileForm) form;
        } else {
            aForm = new ImportProfileForm();
        }

        if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());

        if (request.getSession().getAttribute(IMPORT_PROFILE_ID_KEY) != null) {
            errors = (ActionMessages) request.getSession().getAttribute(IMPORT_PROFILE_ERRORS_KEY);
            int profileId = (Integer) request.getSession().getAttribute(IMPORT_PROFILE_ID_KEY);
            aForm.setProfileId(profileId);
            aForm.setAction(ACTION_VIEW);
            request.getSession().removeAttribute(IMPORT_PROFILE_ERRORS_KEY);
            request.getSession().removeAttribute(IMPORT_PROFILE_ID_KEY);
        }

        if (AgnUtils.parameterNotEmpty(request, "addGender")) {
            aForm.setAction(ACTION_NEW_GENDER);
        }

        if (ImportUtils.hasNoEmptyParameterStartsWith(request, "removeGender")) {
            aForm.setAction(ACTION_REMOVE_GENDER);
        }

        if (AgnUtils.parameterNotEmpty(request, "setDefault")) {
            aForm.setAction(ACTION_SET_DEFAULT);
        }

        try {
            switch (aForm.getAction()) {
                case ImportProfileAction.ACTION_LIST:
                    destination = mapping.findForward("list");
                    aForm.reset(mapping, request);
                    aForm.setAction(ImportProfileAction.ACTION_LIST);
                    break;

                case ImportProfileAction.ACTION_VIEW:
                    aForm.reset(mapping, request);
                    loadImportProfile(aForm);
                    setAvailableImportModes(aForm, request);
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_NEW_GENDER:
                	if (StringUtils.isEmpty(aForm.getAddedGender())) {
                		errors.add("newGender", new ActionMessage("error.import.gender.empty"));
                	} else {
	                	boolean success = aForm.getProfile().addGenderMappingSequence(aForm.getAddedGender(), aForm.getAddedGenderInt());
	                    if (success) {
	                    	aForm.setAddedGender("");
	                    	aForm.setAddedGenderInt(0);
	                    } else {
	                    	errors.add("newGender", new ActionMessage("error.import.gender.number.duplicate"));
	                    }
                	}
                    
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_REMOVE_GENDER:
                    String gender = ImportUtils.getNotEmptyValueFromParameter(request, "removeGender_");
                    int genderValue = 0;
                    StringTokenizer stringTokenizerNewGender = new StringTokenizer(gender, ",");
                    while (stringTokenizerNewGender.hasMoreTokens()) {
                    	String genderToken = stringTokenizerNewGender.nextToken().trim();
                    	genderValue = aForm.getProfile().getGenderMapping().get(genderToken);
                        aForm.getProfile().getGenderMapping().remove(genderToken);
                    }
                	aForm.setAddedGender(gender);
                	aForm.setAddedGenderInt(genderValue);
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_SAVE:
                    if (!checkErrorsOnSave(aForm, request, errors)) {
                        saveImportProfile(aForm);
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    }
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_NEW:
                    aForm.reset(mapping, request);
                    aForm.setAction(ImportProfileAction.ACTION_SAVE);
                    createEmptyProfile(aForm, request);
                    setAvailableImportModes(aForm, request);
                    destination = mapping.findForward("view");
                    break;

                case ImportProfileAction.ACTION_SET_DEFAULT:
                    setDefaultProfile(aForm, request);
                    aForm.setAction(ImportProfileAction.ACTION_LIST);
                    destination = mapping.findForward("list");

                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    break;

                case ImportProfileAction.ACTION_CONFIRM_DELETE:
                    loadImportProfile(aForm);
                    aForm.setAction(ImportProfileAction.ACTION_DELETE);
                    destination = mapping.findForward("delete");
                    break;

                case ImportProfileAction.ACTION_DELETE:
                    if (request.getParameter("kill") != null) {
                        removeProfile(aForm);
                        aForm.setAction(ImportProfileAction.ACTION_LIST);

                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    }
                    destination = mapping.findForward("list");
                    break;

                default:
                    aForm.setAction(ImportProfileAction.ACTION_LIST);
                    destination = mapping.findForward("list");
            }

        } catch (Exception e) {
            logger.error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        if (destination != null && "list".equals(destination.getName())) {
            try {
                // if we will go to list page we need to load profiles list
                // and load the default profile for current admin to show
                // that on list-page
                setNumberOfRows(request, (StrutsFormBase) form);
                request.setAttribute("profileList", getProfileList(request));
                aForm.setDefaultProfileId(AgnUtils.getAdmin(request).getDefaultImportProfileID());
            } catch (Exception e) {
                logger.error("getCampaignList: " + e + "\n" + AgnUtils.getStackTrace(e));
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            }
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        if (!messages.isEmpty()) {
            saveMessages(request, messages);
        }

        return destination;
    }

    protected boolean checkErrorsOnSave(ImportProfileForm aForm, HttpServletRequest request, ActionMessages errors) throws InstantiationException, IllegalAccessException {
        boolean hasErrors = false;
        if (profileNameIsDuplicate(aForm, request)) {
            errors.add("shortname", new ActionMessage("error.import.duplicate_profile_name"));
            hasErrors = true;
        }
        return hasErrors;
    }

    /**
     * Loads the list of allowed import modes for current user.
     *
     * @param aForm   form
     * @param request request
     */
    private void setAvailableImportModes(ImportProfileForm aForm, HttpServletRequest request) {
        List<ImportMode> allowedModes = new ArrayList<ImportMode>();
        for (ImportMode mode : ImportMode.values()) {
            if (allowed(mode.getPublicValue().substring("UserRight.Import.".length()), request)) {
                allowedModes.add(mode);
            }
        }
        aForm.setImportModes(allowedModes.toArray(new ImportMode[0]));
    }

    /**
     * Method checks if there is already a profile with a name user entered creating new profile
     *
     * @param aForm   form
     * @param request request
     * @return true if the profile with such name alreay exists, false if not
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private boolean profileNameIsDuplicate(ImportProfileForm aForm, HttpServletRequest request)
            throws IllegalAccessException, InstantiationException {
        String profileName = aForm.getProfile().getName();
        int profileId = aForm.getProfileId();
        List<ImportProfile> importProfileList = getProfileList(request);
        for (ImportProfile importProfile : importProfileList) {
            if (importProfile.getName().equals(profileName) && importProfile.getId() != profileId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves the default profile id for admin that is set on overview-page
     *
     * @param aForm   a form
     * @param request request
     */
    private void setDefaultProfile(ImportProfileForm aForm, HttpServletRequest request) {
        int defaultProfileId = aForm.getDefaultProfileId();
        Admin admin = AgnUtils.getAdmin(request);
        admin.setDefaultImportProfileID(defaultProfileId);
        AdminDao adminDao = (AdminDao) getWebApplicationContext().getBean("AdminDao");

        Admin adminFromDao = adminDao.getAdmin(admin.getAdminID(), admin.getCompanyID());
        adminFromDao.setDefaultImportProfileID(defaultProfileId);
        adminDao.save(adminFromDao);
    }

    /**
     * Creates empty profile for displaying on view-page when user wants to
     * create new import profile
     *
     * @param aForm   a form
     * @param request request
     */
    private void createEmptyProfile(ImportProfileForm aForm, HttpServletRequest request) {
        ImportProfileImpl newProfile = new ImportProfileImpl();
        newProfile.setAdminId(AgnUtils.getAdmin(request).getAdminID());
        newProfile.setCompanyId(AgnUtils.getCompanyID(request));
        newProfile.setKeyColumn("email");
        newProfile.setCheckForDuplicates(1);
        newProfile.setDefaultMailType(1);
        aForm.setProfile(newProfile);
        aForm.setProfileId(0);
    }

    /**
     * Removes profile from system with all its data using Dao
     *
     * @param aForm a form
     */
    public void removeProfile(ImportProfileForm aForm) {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        profileDao.deleteImportProfileById(aForm.getProfileId());
    }

    /**
     * @param request request
     * @return list of import profiles for overview page with current company id
     */
    private List<ImportProfile> getProfileList(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        return profileDao.getImportProfilesByCompanyId(AgnUtils.getCompanyID(request));
    }

    /**
     * Saves import profile (or creats new if its' "New import profile" page)
     *
     * @param aForm a form
     */
    private void saveImportProfile(ImportProfileForm aForm) {
        if (aForm.getProfile().getKeyColumn() != null) {
            aForm.getProfile().setKeyColumn(aForm.getProfile().getKeyColumn().toLowerCase());
        }
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        if (aForm.getProfileId() != 0) {
            profileDao.updateImportProfile(aForm.getProfile());
        } else {
            profileDao.insertImportProfile(aForm.getProfile());
            aForm.setProfileId(aForm.getProfile().getId());
        }
    }

    /**
     * Loads import profile to show it on profile view-page
     *
     * @param aForm a form
     */
    private void loadImportProfile(ImportProfileForm aForm) {
        ImportProfileDao profileDao = (ImportProfileDao) getWebApplicationContext().getBean("ImportProfileDao");
        aForm.setProfile(profileDao.getImportProfileById(aForm.getProfileId()));
    }
}