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

package org.agnitas.web;

import org.agnitas.beans.*;
import org.agnitas.beans.factory.DynamicTagContentFactory;
import org.agnitas.beans.factory.MailingComponentFactory;
import org.agnitas.beans.factory.MailingFactory;
import org.agnitas.dao.*;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Action that handles creation of mailing using mailing wizard.
 *
 * @author Martin Helff, Nicole Serek, Andreas Rehak
 *
 */

public class MailingWizardAction extends StrutsDispatchActionBase {

	public static final String ACTION_START = "start";

	public static final String ACTION_NAME = "name";

	public static final String ACTION_TEMPLATE = "template";

	public static final String ACTION_TYPE = "type";

	public static final String ACTION_TYPE_PREVIOUS = "type_previous";

	public static final String ACTION_SENDADDRESS = "sendaddress";

	public static final String ACTION_MAILTYPE = "mailtype";

	public static final String ACTION_SUBJECT = "subject";

	public static final String ACTION_TARGET = "target";

    public static final String ACTION_TARGET_FINISH = "target_finish";

	public static final String ACTION_TEXTMODULES = "textmodules";

	public static final String ACTION_TEXTMODULES_PREVIOUS = "textmodules_previous";

	public static final String ACTION_TEXTMODULE = "textmodule";

	public static final String ACTION_TEXTMODULE_ADD = "textmodule_add";

    public static final String ACTION_TEXTMODULE_SAVE = "textmodule_save";

	public static final String ACTION_MEASURELINKS = "links";

	public static final String ACTION_MEASURELINK = "link";

    public static final String ACTION_TO_ATTACHMENT = "to_attachment";

	public static final String ACTION_ATTACHMENT = "attachment";

	public static final String ACTION_FINISH = "finish";


    protected MailinglistDao mailinglistDao;
    protected MailingFactory mailingFactory;
    protected MailingDao mailingDao;
    protected MailingComponentFactory mailingComponentFactory;
    protected DynamicTagContentFactory dynamicTagContentFactory;
    protected CampaignDao campaignDao;
    protected TargetDao targetDao;
    protected EmmActionDao emmActionDao;


    // --------------------------------------------------------- Public Methods

    /**
     * Initialization of mailing wizard. If current user is logged in - forwards to action input forward.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		return mapping.getInputForward();
	}

    /**
     * Forwards to mailing creation without wizard ("withoutWizard").
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward withoutWizard(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		return mapping.findForward("withoutWizard");
	}

    /**
     * If the user is not logged in - forwards to login page<br>
     * Gets list of mailinglists for current company. If there are no mailinglists existing - adds error message and
     * forwards to input forward ("mwStart").<br>
     * In other case creates mailing using mailingFactory; initializes mailing (creates default html- and text-template
     * components and mediatype); sets mailing template id if templateID parameter existed in request; sets mailing
     * default target mode; sets mailing into Form; forwards to "next" (currently that is the page for entering mailing
     * name and description)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward start(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		List mlists=mailinglistDao.getMailinglists(getCompanyID(req));

		if(mlists.size() <= 0) {
			ActionMessages	errors = new ActionMessages();

			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.mailing.noMailinglist"));
			saveErrors(req, errors);
			return mapping.getInputForward();
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = mailingFactory.newMailing();

		mailing.init(getCompanyID(req), getApplicationContext(req));
        HashMap map = AgnUtils.getReqParameters(req);
        String templateIDString = (String) map.get("templateID");
        if (templateIDString != null && !templateIDString.isEmpty()) {
            mailing.setMailTemplateID(Integer.parseInt(templateIDString));
        }
        mailing.setTargetMode(Mailing.TARGET_MODE_AND);
		aForm.setMailing(mailing);
		return mapping.findForward("next");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Sets "isTemplate" mailing property to false. Loads list of company's templates to request.
     * Forwards to "next" (currently the page for choosing mailing template)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward name(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

		if (mailing != null) {
//			FIXME: set(get()) What's the point?
//			mailing.setShortname(aForm.getMailing().getShortname());
//			mailing.setDescription(
//					aForm.getMailing().getDescription());
			mailing.setIsTemplate(false);
		}
        prepareTemplatePage(req);
        return mapping.findForward("next");
	}

    /**
     * If the user is not logged in - forwards to login page<br>
     * If template is selected: loads template from database and copies template data to mailing. <br>
     * If template is not selected - sets status active to first mailing mediatype (if first mailing mediatype is
     * null - creates it)<br>
     * Forwards to "next" (currently the page for selecting mailing type)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward template(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

		if (aForm.getMailing().getMailTemplateID() == 0) {
			mailing.setIsTemplate(false);

			Map mediatypes = mailing.getMediatypes();

			Mediatype type = (Mediatype) mediatypes.get(0);
			if (type != null) {
				type.setStatus(Mediatype.STATUS_ACTIVE);
			} else {
				// should not happen
				MediatypeEmail paramEmail = mailing.getEmailParam();

				paramEmail.setCharset("iso-8859-1");
				paramEmail.setMailFormat(1);
				paramEmail.setLinefeed(0);
				paramEmail.setPriority(1);
				paramEmail.setStatus(Mediatype.STATUS_ACTIVE);
				mediatypes.put(0, paramEmail);
			}
			aForm.clearEmailData();
		} else {
			Mailing template = mailingDao.getMailing(aForm.getMailing().getMailTemplateID(), getCompanyID(req));

			if (template != null) {
				Mailing newMailing = (Mailing) template
						.clone(getApplicationContext(req));
				newMailing.setId(0); // 0 for creating a new mailing and not
				// changing the template
				newMailing.setShortname(
					aForm.getMailing().getShortname());
				newMailing.setDescription(
					aForm.getMailing().getDescription());
				newMailing.setIsTemplate(false);
				newMailing.setMediatypes(template.getMediatypes());
				newMailing.setMailTemplateID(template.getId());
				newMailing.setCompanyID(aForm.getCompanyID(req));
				newMailing.setMailinglistID(template.getMailinglistID());
				newMailing.setArchived(template.getArchived());

                newMailing.setCampaignID(template.getCampaignID());
                newMailing.setMailingType(template.getMailingType());
                newMailing.setTargetID(template.getTargetID());
                newMailing.setTargetGroups(template.getTargetGroups());

				Map mediatypes = newMailing.getMediatypes();

				Mediatype type = (Mediatype) mediatypes.get(0);
				if (type != null) {
					type.setStatus(Mediatype.STATUS_ACTIVE);
				}
				aForm.setMailing(newMailing);

				MediatypeEmail param = newMailing.getEmailParam();
				// param.setStatus(Mediatype.STATUS_ACTIVE);
				aForm.setEmailSubject(param.getSubject());
				aForm.setEmailFormat(param.getMailFormat());
				aForm.setEmailOnepixel(param.getOnepixel());
				aForm.setSenderEmail(param.getFromEmail());
				aForm.setSenderFullname(param.getFromFullname());
				aForm.setReplyEmail(param.getReplyEmail());
				aForm.setReplyFullname(param.getReplyFullname());

				aForm.setMailing(newMailing);
			}
		}
		return mapping.findForward("next");
	}

    protected void prepareTemplatePage(HttpServletRequest req){
        List<Mailing> templates = mailingDao.getTemplates(getCompanyID(req));
        req.setAttribute("templates", templates);
    }

    /**
     * If the user is not logged in - forwards to login page.
     * Forwards to "next" (currently the page for entering sender-address, sender-name,
     * replyto-address and reply-to name)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */

	public ActionForward type(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
//		FIXME: set(get()) What's the point?
//		MailingWizardForm aForm = (MailingWizardForm) form;
//		Mailing mailing = aForm.getMailing();
//
//		mailing.setMailingType(aForm.getMailing().getMailingType());
		return mapping.findForward("next");
	}

    /**
     * Loads campaigns, mailinglists and target-groups to request. Forwards to "previous" (currently the page where user
     * sets mailinglist, targets, campaign etc.)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward textmodules_previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
        prepareTargetPage(req);
		return mapping.findForward("previous");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Saves the address information(sender-address, sender-name, replyto-address and reply-to name) into the mailing
     * email param. Forwards to "next" (currently the page for selecting mail format)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward sendaddress(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

		MediatypeEmail param = mailing.getEmailParam();
		param.setFromEmail(aForm.getSenderEmail());
		param.setFromFullname(aForm.getSenderFullname());
		param.setReplyEmail(aForm.getReplyEmail());
		param.setReplyFullname(aForm.getReplyFullname());

		return mapping.findForward("next");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Saves the subject into mailing email param. Builds mailing dependencies.
     * Loads campaigns, mailinglists and target-groups to request. Forwards to "next" (currently to the page for
     * setting mailinglist, target groups etc.)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward subject(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		MediatypeEmail param = aForm.getMailing().getEmailParam();

		param.setSubject(aForm.getEmailSubject());
		aForm.getMailing().buildDependencies(true,
				getApplicationContext(req));

        prepareTargetPage(req);
		return mapping.findForward("next");

	}

    /**
     * If the user is not logged in - forwards to login page.
     * Loads campaigns, mailinglists and target-groups to request. Forwards to "targetView"
     * (currently the page for setting mailinglist, target groups etc.)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward targetView(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
        prepareTargetPage(req);
		return mapping.findForward("targetView");

	}

    /**
     * If the user is not logged in - forwards to login page.
     * Saves the mail format to mailing email param. Resets html template if mailing format is "Text". Forwards to
     * "next" (the page for entering mailing subject)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward mailtype(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		MediatypeEmail param = aForm.getMailing().getEmailParam();

		int mailFormat = aForm.getEmailFormat();
		param.setMailFormat(mailFormat);
		if (mailFormat == 0) {
			param.setHtmlTemplate("");
			aForm.getMailing().getHtmlTemplate().setEmmBlock("");
		}

		return mapping.findForward("next");
	}

    /**
     * Loads required data for target page (Mailinglists, Campaigns and Target groups) into request.
     *
     * @param request request from jsp
     */
    public void prepareTargetPage(HttpServletRequest request) {
        List<Mailinglist> mailinglists = mailinglistDao.getMailinglists(getCompanyID(request));
        List<Campaign>    campaings    = campaignDao.getCampaignList(getCompanyID(request), "lower(shortname)", 1);
        List<Target>      targets      = targetDao.getTargets(getCompanyID(request));
        request.setAttribute("mailinglists",mailinglists);
        request.setAttribute("campaigns", campaings);
        request.setAttribute("targets", targets);
    }

    /**
     * If the user is not logged in - forwards to login page.
     * Saves openrate-measure property to mailing email param. Loads campaigns, mailinglists and target-groups to
     * request. Adds target to mailing targets list if needed. Removes target from mailing target list if needed.
     * Updates target expression (re-generates String representing selected targets IDs of mailing). Forwards to
     * "next" (currently the page for managing textmodules)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward target(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();

        // Do we really need to do that? This will have no effect
		mailing.setMailinglistID(mailing.getMailinglistID());
		mailing.setCampaignID(mailing.getCampaignID());
        // --------------------------

		MediatypeEmail param = mailing.getEmailParam();
		param.setOnepixel(aForm.getEmailOnepixel());
        prepareTargetPage(req);
		if (aForm.getTargetID() != 0) {
			Collection aList = mailing.getTargetGroups();

			if (aList == null) {
				aList = new HashSet();
			}
			if (!aList.contains(new Integer(aForm.getTargetID()))) {
				aList.add(new Integer(aForm.getTargetID()));
			}
			mailing.setTargetGroups(aList);
			return mapping.getInputForward();
		}

		if (aForm.getRemoveTargetID() != 0) {
			Collection aList = aForm.getMailing().getTargetGroups();

			if (aList != null) {
				aList.remove(new Integer(aForm.getRemoveTargetID()));
			}
            aForm.getMailing().setTargetGroups(aList);
			return mapping.getInputForward();
		}
        // for the case if the target mode was changed we need to re-generate target expression
        updateTargetExpression(aForm);
        return mapping.findForward("next");
	}

    private void updateTargetExpression(MailingWizardForm aForm) {
        if (aForm.getTargetID() == 0 && aForm.getRemoveTargetID() == 0 &&
                aForm.getMailing().getTargetGroups() != null && aForm.getMailing().getTargetGroups().size() > 0) {
            aForm.getMailing().updateTargetExpression();
        }
    }

    /**
     * Updates target expression (re-generates String representing selected targets IDs of mailing).
     * Saves mailing to database. Forwards to finish page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
    public ActionForward target_finish(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
        this.updateTargetExpression((MailingWizardForm) form);
        return this.finish(mapping, form, req, res);
    }

    /**
     * If the user is not logged in - forwards to login page.<br>
     * Loads target groups list into request.<br>
     * Gets mailing dyntag by name taken from form property dynName. Sets mailing ID and companyID for that dynTag.
     * Cleans up trackable links of mailing and rebuilds dependencies. Finds next dynTag in list of mailings dynTags.
     * (If it already was the last tag - forwards to "skip"). Sets next dynTag name to form. Updates property that
     * indicates if we need to show HTML editor for editing content of the tag (that basically dependant on is it
     * HTML-version tag or Text-version tag). Forwards to input forward (text module editing page)<br>
     * If the dynName property of form is not set and mailing doesn't have dyntags - forwards to "skip". If the dynName
     * property of form is not set and mailing does have dyntags - takes the first one and sets its name to form;
     * updates property that indicates if we need to show HTML editor for editing content; forwards to "next".
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward textmodule(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();
		DynamicTag dynTag = null;
        prepareAttachmentPage(req);
		dynTag = (DynamicTag) mailing.getDynTags().get(aForm.getDynName());
		if (dynTag != null) {
			dynTag.setMailingID(mailing.getId());
			dynTag.setCompanyID(mailing.getCompanyID());

			mailing.cleanupTrackableLinks(new Vector());
			mailing.buildDependencies(true, getApplicationContext(req));
			if (aForm.getDynName() != null
					&& aForm.getDynName().trim().length() != 0) {
				Iterator it = mailing.getDynTags().keySet().iterator();
				while (it.hasNext()) {
					if (it.next().equals(aForm.getDynName())) {
						break;
					}
				}
				if(!it.hasNext()) {
					return mapping.findForward("skip");
				}
                String dynName = (String) it.next();
                aForm.setDynName(dynName);
                aForm.setShowHTMLEditorForDynTag(allowHTMLEditor(dynName, mailing, req));
			}
			return mapping.getInputForward();
		}

		if (aForm.getDynName() == null
				|| aForm.getDynName().trim().length() == 0) {
            if (!mailing.getDynTags().keySet()
                    .iterator().hasNext()) {
                return mapping.findForward("skip");
            }
            String dynName = mailing.getDynTags().keySet().iterator().next();
            aForm.setDynName(dynName);
            aForm.setShowHTMLEditorForDynTag(allowHTMLEditor(dynName, mailing, req));
		}
		return mapping.findForward("next");
	}

    /**
     * Loads target groups list into request and forwards to "previous".
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward type_previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
        prepareTemplatePage(req);
		return mapping.findForward("previous");
	}

    /**
     * Checks if HTMLEditor is allowed for dynTag (checks if the text module comes from HTML-version or Text-version).
     *
     * @param dynTargetName dynTag name to check.
     * @param aMailing new mailing
     * @param req request from jsp
     * @return true if HTMLEditor is allowed, otherwise false
     * @throws Exception  if a exception occurs
     */
    public boolean allowHTMLEditor(String dynTargetName, Mailing aMailing, HttpServletRequest req) throws Exception {
        String htmlEmmBlock = aMailing.getTextTemplate().getEmmBlock();
        Vector<String> tagsInTextTemplate = aMailing.findDynTagsInTemplates(htmlEmmBlock, getApplicationContext(req));
        return !tagsInTextTemplate.contains(dynTargetName);
    }


    /**
     * If the user is not logged in - forwards to login page.
     * Creates new dynTag content. Sets its properties. Adds it to current dynTag. Resets targetID and newContent
     * properties of form. Loads target groups list into request. Forwards to "add" (text module editing page)
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward textmodule_add(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		Mailing mailing = aForm.getMailing();
		DynamicTag dynTag = (DynamicTag) mailing.getDynTags().get(aForm.getDynName());
		DynamicTagContent content = dynamicTagContentFactory.newDynamicTagContent();

		dynTag.setMailingID(mailing.getId());
		dynTag.setCompanyID(mailing.getCompanyID());

		content.setCompanyID(mailing.getCompanyID());
		content.setDynContent(aForm.getNewContent());
		content.setTargetID(aForm.getTargetID());
		content.setDynNameID(dynTag.getId());
		content.setMailingID(dynTag.getMailingID());
		content.setDynOrder(dynTag.getMaxOrder()+1);
		dynTag.addContent(content);
		aForm.setTargetID(0);
		aForm.setNewContent("");
        prepareAttachmentPage(req);
		return mapping.findForward("add");

	}

    /**
     * If the user is not logged in - forwards to login page.<br>
     * Saves existing textmodule.<br>
     * Calls method <code>textmodule</code> of this class. Sets dynName property of form. Updates property that
     * indicates if we need to show HTML editor for editing content. Loads target groups list into request. Forwards to
     * textmodule editing page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward textmodule_save(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
        MailingWizardForm aForm = (MailingWizardForm) form;
        String dynName = aForm.getDynName();
        Mailing mailing = aForm.getMailing();
        textmodule(mapping, form, req, res);
        aForm.setDynName(dynName);
        aForm.setShowHTMLEditorForDynTag(allowHTMLEditor(dynName, mailing, req));
        prepareAttachmentPage(req);
        return mapping.getInputForward();
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Changes the order, moves the current dynContent one position up. Loads target groups list into request. Forwards
     * to textmodule editing page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward textmodule_move_up(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
        MailingWizardForm aForm = (MailingWizardForm) form;
        Mailing mailing = aForm.getMailing();
        DynamicTag dynTag = mailing.getDynTags().get(aForm.getDynName());
        dynTag.moveContentDown(aForm.getContentID(), -1, true);
        prepareAttachmentPage(req);
        return mapping.findForward("add");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Changes the order, moves the current dynContent one position down. Loads target groups list into request. Forwards
     * to textmodule editing page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward textmodule_move_down(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
        MailingWizardForm aForm = (MailingWizardForm) form;
        Mailing mailing = aForm.getMailing();
        DynamicTag dynTag = mailing.getDynTags().get(aForm.getDynName());
        dynTag.moveContentDown(aForm.getContentID(), 1, true);
        prepareAttachmentPage(req);
        return mapping.findForward("add");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Resets form's trackable link iterator. Loads list of none-form actions into request. Forwards to links editing
     * page (forward is "next")
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward links(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		aForm.clearAktTracklink();
        prepareLinkPage(req);
		return mapping.findForward("next");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Gets next link, loads list of none-form actions into request, forwards to link editing page.
     * If no more links found - loads list of target groups to request, forwards to attachments page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward link(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;

		if(aForm.nextTracklink()) {
            prepareLinkPage(req);
			return mapping.findForward("next");
		}
        prepareAttachmentPage(req);
		return mapping.findForward("skip");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Loads list of none-form actions into request, forwards to link editing page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward link_save_only(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
        prepareLinkPage(req);
		return mapping.findForward("next");
	}

    protected void prepareLinkPage(HttpServletRequest req) {
        List linkActions = emmActionDao.getEmmNotFormActions(getCompanyID(req));
        req.setAttribute("linkActions",linkActions);
    }

    /**
     * If the user is not logged in - forwards to login page.
     * Loads list of target groups into request, forwards to attachments editing page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
    public ActionForward to_attachment(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
        prepareAttachmentPage(req);
		return mapping.findForward("skip");
	}

    protected void prepareAttachmentPage (HttpServletRequest req) {
        List<Target> targets = targetDao.getTargets(getCompanyID(req), false);
        req.setAttribute("targets",targets);
    }

    /**
     * If the user is not logged in - forwards to login page.
     * Checks if the size of new attachment exceeds the max allowed size for attachment. If the size is over the limit
     * adds appropriate error messages. If the size is ok - creates a new mailing component for attachment and fills it
     * with data and adds that component to mailing. Loads list of target groups into request. Forwards to attachments
     * page
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward attachment(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		FormFile newAttachment=aForm.getNewAttachment();
        try	{
            int fileSize = newAttachment.getFileSize();
            boolean maxSizeOverflow = false;
            if (AgnUtils.isMySQLDB()) {
                // check for MySQL parameter max_allowed_packet
                String maxSize = AgnUtils.getDefaultValue("attachment.maxSize");
                if (fileSize != 0 && maxSize != null && fileSize > Integer.parseInt(maxSize)){
                    maxSizeOverflow = true;
                }
            }
            if (fileSize != 0 && !maxSizeOverflow) {
				MailingComponent comp=mailingComponentFactory.newMailingComponent();

				comp.setCompanyID(this.getCompanyID(req));
				comp.setMailingID(aForm.getMailing().getId());
				if(aForm.getNewAttachmentType() == 0) {
					comp.setType(MailingComponent.TYPE_ATTACHMENT);
				} else {
					comp.setType(MailingComponent.TYPE_PERSONALIZED_ATTACHMENT);
				}
				comp.setComponentName(aForm.getNewAttachmentName());
				comp.setBinaryBlock(newAttachment.getFileData());
				comp.setEmmBlock(comp.makeEMMBlock());
				comp.setMimeType(newAttachment.getContentType());
				comp.setTargetID(aForm.getAttachmentTargetID());
				aForm.getMailing().addComponent(comp);
            } else if (maxSizeOverflow) {
                ActionMessages errors = new ActionMessages();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.attachment", AgnUtils.getDefaultValue("attachment.maxSize")));
                saveErrors(req, errors);
            }

		} catch (Exception e) {
			AgnUtils.logger().error("saveAttachment: "+e);
		}
        prepareAttachmentPage(req);
		return mapping.findForward("next");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Saves the mailing into database and forwards to finish page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward finish(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		MailingWizardForm aForm = (MailingWizardForm) form;
		mailingDao.saveMailing(aForm.getMailing());

		return mapping.findForward("finish");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Forwards to "previous" (used for handling Previous button click).
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}
         
		return mapping.findForward("previous");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Forwards to "next" (used for handling Next button click).
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward next(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		return mapping.findForward("next");
	}

    /**
     * If the user is not logged in - forwards to login page.
     * Forwards to "skip" (used for handling Skip button click).
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form data for the action filled by the jsp
     * @param req request from jsp
     * @param res response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws Exception if a exception occurs
     */
	public ActionForward skip(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		return mapping.findForward("skip");
	}

    protected ApplicationContext getApplicationContext(HttpServletRequest req){
        // this method should be removed after bean Mailing will be refactored
        return WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext());
    }

    public void setMailinglistDao(MailinglistDao mailinglistDao) {
        this.mailinglistDao = mailinglistDao;
    }

    public void setMailingFactory(MailingFactory mailingFactory) {
        this.mailingFactory = mailingFactory;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setMailingComponentFactory(MailingComponentFactory mailingComponentFactory) {
        this.mailingComponentFactory = mailingComponentFactory;
    }

    public void setDynamicTagContentFactory(DynamicTagContentFactory dynamicTagContentFactory) {
        this.dynamicTagContentFactory = dynamicTagContentFactory;
    }

    public CampaignDao getCampaignDao() {
        return campaignDao;
    }

    public void setCampaignDao(CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
    }

    public TargetDao getTargetDao() {
        return targetDao;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public EmmActionDao getEmmActionDao() {
        return emmActionDao;
    }

    public void setEmmActionDao(EmmActionDao emmActionDao) {
        this.emmActionDao = emmActionDao;
    }
}
