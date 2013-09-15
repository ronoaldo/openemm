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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.DeliveryStatFactory;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.TrackableLink;
import org.agnitas.beans.factory.MailingFactory;
import org.agnitas.cms.utils.CmsUtils;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.target.service.TargetService;
import org.agnitas.emm.core.commons.util.DateUtil;
import org.agnitas.mailing.beans.MaildropEntryFactory;
import org.agnitas.preview.AgnTagException;
import org.agnitas.preview.Page;
import org.agnitas.preview.Preview;
import org.agnitas.preview.PreviewFactory;
import org.agnitas.preview.PreviewHelper;
import org.agnitas.preview.TAGCheck;
import org.agnitas.preview.TAGCheckFactory;
import org.agnitas.service.LinkcheckService;
import org.agnitas.stat.DeliveryStat;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public class MailingSendAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(MailingSendAction.class);

	public static final int ACTION_VIEW_SEND = ACTION_LAST + 1;
	public static final int ACTION_SEND_ADMIN = ACTION_LAST + 2;
	public static final int ACTION_SEND_TEST = ACTION_LAST + 3;
	public static final int ACTION_SEND_WORLD = ACTION_LAST + 4;
	public static final int ACTION_VIEW_SEND2 = ACTION_LAST + 5;
	public static final int ACTION_VIEW_DELSTATBOX = ACTION_LAST + 6;
	public static final int ACTION_ACTIVATE_CAMPAIGN = ACTION_LAST + 7;
	public static final int ACTION_ACTIVATE_RULEBASED = ACTION_LAST + 8;
	public static final int ACTION_PREVIEW_SELECT = ACTION_LAST + 9;
	public static final int ACTION_PREVIEW = ACTION_LAST + 10;
	public static final int ACTION_PREVIEW_HEADER = ACTION_LAST + 13;
	public static final int ACTION_DEACTIVATE_MAILING = ACTION_LAST + 14;
	public static final int ACTION_CHANGE_SENDDATE = ACTION_LAST + 15;
	public static final int ACTION_CANCEL_MAILING_REQUEST = ACTION_LAST + 16;
	public static final int ACTION_CANCEL_MAILING = ACTION_LAST + 17;
	public static final int ACTION_CONFIRM_SEND_WORLD = ACTION_LAST + 18;
	public static final int ACTION_CHECK_LINKS = ACTION_LAST + 19;
	public static final int ACTION_SEND_LAST = ACTION_LAST + 19;

    public static final int PREVIEW_MODE_HEADER = 1;
    public static final int PREVIEW_MODE_TEXT = 2;
    public static final int PREVIEW_MODE_HTML = 3;
    public static final int PREVIEW_MODE_OFFLINE = 4;

    // tag errors
    public static final String TEMPLATE = "__TEMPLATE__";
	public static final String SUBJECT = "__SUBJECT__";
	public static final String FROM = "__FROM__";

    protected TargetDao targetDao;
    protected MailingDao mailingDao;
    protected RecipientDao recipientDao;
    private MailingComponentDao mailingComponentDao;
    private LinkcheckService linkcheckService;
    private MailingFactory mailingFactory;
    private DeliveryStatFactory deliveryStatFactory;
    private MaildropEntryFactory maildropEntryFactory;
    private MailinglistDao mailinglistDao;
    private TAGCheckFactory tagCheckFactory;
    private DataSource dataSource;
    protected PreviewFactory previewFactory;
    private TargetService targetService;

    protected int maxAdminMails;

    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
     * ACTION_VIEW_SEND: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     loads delivery statistic data from db into the form;<br>
     *     loads names of mailing target groups into a form;<br>
     *     calculates statistics-frame size according to number of target groups selected for mailing;<br>
     *     sets the flag for displaying of send test- or admin-mails buttons;<br>
     *     sets destination="send".
     * <br><br>
     * ACTION_VIEW_DELSTATBOX: loads delivery statistic data from db into the form;<br>
     *     loads names of mailing target groups into a form;<br>
     *     calculates statistics-frame size according to number of target groups selected for mailing;<br>
     *     sets the flag for displaying of send test- or admin-mails buttons;<br>
     *     sets destination="view_delstatbox".
     * <br><br>
     * ACTION_CANCEL_MAILING_REQUEST: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     forwards to jsp with question to cancel mailing generation.
     * <br><br>
     * ACTION_CANCEL_MAILING: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     if the request parameter "kill" is set - cancels mailing generation; if the mailing generation was
     *     canceled successfully - loads delivery statistic to form, loads mailing targets to request, calculates
     *     statistics-frame size according to number of target groups selected for mailing, sets the flag for
     *     displaying of send test- or admin-mails buttons;<br>
     *     forwards to mailing send page ("send");
     * <br><br>
     * ACTION_VIEW_SEND2: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     loads send statistic data from db into the form (number of sent html-mails, text-mails, offline-mails,
     *     total number of sent mails);<br>
     *     loads target group names into the form<br>
     *     sets destination="send2".
     * <br><br>
     * ACTION_SEND_ADMIN: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     creates maildrop-entry with type <code>MaildropEntry.STATUS_ADMIN</code> ('A');<br>
     *     performs mailing validation before sending: checks that there's at least 1 recipient, checks that subject and
     *     sender address are not empty, checks that html and text versions are not empty;<br>
     *     if it is CMS-mailing - regenerates mailing content from CMS data;<br>
     *     triggers mailing sending with a mailgun and a newly created maildrop-entry; mailiing will be sent only to
     *     admin-recipients.<br>
     *     loads delivery statistic data from db into the form;<br>
     *     loads names of mailing target groups into a form;<br>
     *     Sets destination="send".
     * <br><br>
     * ACTION_SEND_TEST: do exactly the same things as ACTION_SEND_ADMIN with the only difference that the type
     *     of created maidrop-entry is <code>MaildropEntry.STATUS_TEST</code> ('T'). Mailing will be sent to admin- and
     *     test-recipients
     * <br><br>
     * ACTION_DEACTIVATE_MAILING: removes maildrop status entries of mailing with status
     *     <code>MaildropEntry.STATUS_ACTIONBASED</code> and <code>MaildropEntry.STATUS_DATEBASED</code> ('E' and 'R');
     *     In fact that means that the sending of actionbased/datebased mailing will be stopped<br>
     *     loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     sets destination="send".
     * <br><br>
     * ACTION_CONFIRM_SEND_WORLD: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     forwards to jsp with question to send the mailing.
     * <br><br>
     * ACTION_ACTIVATE_RULEBASED:
     * ACTION_ACTIVATE_CAMPAIGN:
     * ACTION_SEND_WORLD: creates maildrop-entry with appropriate type depending on action:
     *     <code>MaildropEntry.STATUS_WORLD</code>, <code>MaildropEntry.STATUS_ACTIONBASED</code> or
     *     <code>MaildropEntry.STATUS_DATEBASED</code> ('W', 'E' or 'R');<br>
     *     performs mailing validation before sending: checks that there's at least 1 recipient, checks that subject and
     *     sender address are not empty, checks that html and text versions are not empty;<br>
     *     If the send should be performed now - regenerates mailing content from CMS data (if CMS mailing), triggers
     *     mailing sending with a mailgun and a newly created maildrop-entry; mailiing will be sent to all active
     *     recipients (admin, test and world).<br>
     *     loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     loads delivery statistic data from db into the form;<br>
     *     loads names of mailing target groups into a form;<br>
     *     Sets destination="send".
     * <br><br>
     * ACTION_PREVIEW_SELECT: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     loads list of mailing admin and test recipients into the request;<br>
     *     checks if the mailing has at least one admin or test recipient and stores the check result in the form;
     *     if the check result is "true" and there wasn't preview-recipient selected by user - stores the
     *     smallest value of recipient id in the form as a preview recipient. (Preview recipient is a recipient
     *     the mailing preview will be generated for)<br>
     *     Sets destination="preview_select".
     * <br><br>
     * ACTION_PREVIEW_HEADER: performs tag-check for mailing subject and from-address, if the check is ok - generates
     *     backend preview. If the preview header is not null - takes subject and from-address from backend preview and
     *     sets that to form, in other case sets subject and from-address taken from emailParam of mailing into form.
     *     Sets mail format and mailinglist ID to form.<br>
     *     If preview header generation fails, loads dynamic tag error report into request and forwards to error
     *     report page;<br>
     *     in case of successful preview header generation, loads mailing attachments and personalized attachments into
     *     request and sets destination="preview_header".
     * <br><br>
     * ACTION_PREVIEW: Checks if preview customer ID is set and sets the result of check to form property<br>
     *     If the preview customer ID is set in form - generates mailing preview according to format selected by user
     *     and sets it to form; sets mail format and mailinglist ID to form. Forwards to preview page. If preview
     *     generation failed - stores error report to request and forwards to error report page<br>
     *     If the preview customer id is not set in form forwards to error report page
     * <br><br>
     * ACTION_CHECK_LINKS: loads mailing into the form;<br>
     *     loads list of target groups into request;<br>
     *     loads delivery statistic data from db into the form;<br>
     *     checks if the mailing links are available (the url-request ), if there's at least one link invalid - creates
     *     error message with the list of invalid links; if all links are ok - adds success message<br>
     *     Forwards to "send".
     * <br><br>
     * @param form ActionForm object
     * @param req request
     * @param res response
     * @param mapping the ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */
	@Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        MailingSendForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;

        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        aForm=(MailingSendForm)form;
        if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case ACTION_VIEW_SEND:
                    if(allowed("mailing.send.show", req)) {
                        loadMailing(aForm, req);
                        // TODO Remove this quick-hack and replace it with some more sophisticated code

                        loadDeliveryStats(aForm, req);

                        destination=mapping.findForward("send");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_VIEW_DELSTATBOX:
                    if(allowed("mailing.send.show", req)) {
                        loadDeliveryStats(aForm, req);
                        destination=mapping.findForward("view_delstatbox");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_CANCEL_MAILING_REQUEST:
                    loadMailing(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_CANCEL_MAILING);
                    destination=mapping.findForward("cancel_generation_question");
                    break;

                case ACTION_CANCEL_MAILING:
                    loadMailing(aForm, req);
                    if(req.getParameter("kill")!=null) {
                        if(cancelMailingDelivery(aForm, req)) {
                            loadDeliveryStats(aForm, req);
                        }
                        destination=mapping.findForward("send");
                    }
                    break;

                case MailingSendAction.ACTION_VIEW_SEND2:
                    if(allowed("mailing.send.show", req)) {
                        loadMailing(aForm, req);
                        loadSendStats(aForm, req);
                        aForm.setAction(MailingSendAction.ACTION_CONFIRM_SEND_WORLD);
                        Set<Integer> targetGroups = (Set<Integer>) aForm.getTargetGroups();
                        if(targetGroups == null){
                            targetGroups = new HashSet<Integer>();
                        }
                        List<String> targetGroupNames = getTargetDao().getTargetNamesByIds(AgnUtils.getAdmin(req).getCompanyID(), targetGroups);
                        req.setAttribute("targetGroupNames", targetGroupNames);
                        destination=mapping.findForward("send2");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailingSendAction.ACTION_SEND_ADMIN:
                    if(allowed("mailing.send.admin", req)) {
                        loadMailing(aForm, req);
                        sendMailing(aForm, req);
                        loadDeliveryStats(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;

                case MailingSendAction.ACTION_SEND_TEST:
                    if(allowed("mailing.send.test", req)) {
                        loadMailing(aForm, req);
                        sendMailing(aForm, req);
                        loadDeliveryStats(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;

                case MailingSendAction.ACTION_DEACTIVATE_MAILING:
                    if(allowed("mailing.send.world", req)) {
                        deactivateMailing(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    loadMailing(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;

                case ACTION_CONFIRM_SEND_WORLD:
                    loadMailing(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_SEND_WORLD);
                    destination=mapping.findForward("send_confirm");
                    break;

                case MailingSendAction.ACTION_ACTIVATE_RULEBASED:
                case MailingSendAction.ACTION_ACTIVATE_CAMPAIGN:
                case MailingSendAction.ACTION_SEND_WORLD:
                    if(allowed("mailing.send.world", req)) {
                    	try {
                    		sendMailing(aForm, req);
                    	} finally {
                            loadMailing(aForm, req);
                    	}
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        loadMailing(aForm, req);
                    }
//                    loadMailing(aForm, req);
                    loadDeliveryStats(aForm, req);
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination=mapping.findForward("send");
                    break;

                case MailingSendAction.ACTION_PREVIEW_SELECT:
                    loadMailing(aForm, req);

                    Map<Integer, String> recipientList = putPreviewRecipientsInRequest(req, aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID(), getRecipientDao());
                    if(hasPreviewRecipient(aForm, req)) {
                		aForm.setHasPreviewRecipient(true);

                		if( aForm.getPreviewCustomerID() == 0 && recipientList.size() > 0) {
                			int minId = Collections.min(recipientList.keySet());
                			aForm.setPreviewCustomerID(minId);
                		}
                    } else {
                		aForm.setHasPreviewRecipient(false);
                	}
                    destination=mapping.findForward("preview_select");
                    break;

                case MailingSendAction.ACTION_PREVIEW_HEADER:

                	try {
                		getHeaderPreview(aForm, req);

                        List<MailingComponent> components = mailingComponentDao.getPreviewHeaderComponents(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());
                        req.setAttribute("components", components);
                        destination=mapping.findForward("preview_header");
                	} catch(AgnTagException e) {
                		req.setAttribute("errorReport", e.getReport());
                		destination=mapping.findForward("preview_errors");
                	}
                    break;

                case MailingSendAction.ACTION_PREVIEW:
                    if (aForm.getPreviewCustomerID() > 0) {
                        aForm.setHasPreviewRecipient(true);
                        try {
                        	destination = getPreview(mapping, aForm, req);
                        } catch (AgnTagException agnTagException) {
                            req.setAttribute("errorReport", agnTagException.getReport());
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.template.dyntags"));
                            destination = mapping.findForward("preview_errors");
                        }
                    } else {
                        aForm.setHasPreviewRecipient(false);
                        destination = mapping.findForward("preview_errors");
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.preview.no_recipient"));
                    }
                    break;

                case MailingSendAction.ACTION_CHECK_LINKS:
                    loadMailing(aForm, req);
                    loadDeliveryStats(aForm, req);

                    List<String> listInvalidUrl = checkForInvalidLinks(aForm, req);
                    if (listInvalidUrl.size() > 0) {
                        for (String invalidUrl : listInvalidUrl) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.invalid.link", invalidUrl + " <br>"));
                        }
                    } else {
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("link.check.success"));
                    }
                    aForm.setAction(MailingSendAction.ACTION_VIEW_SEND);
                    destination = mapping.findForward("send");
                    break;
            }
        } catch (Exception e) {
            logger.error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            if(e.getMessage().equals("error.mailing.send.admin.maxMails")) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), maxAdminMails));
			} else {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage()));
			}
            destination=mapping.findForward("send");
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            this.saveErrors(req, errors);
            if(aForm.getAction()==MailingSendAction.ACTION_SEND_ADMIN || aForm.getAction()==MailingSendAction.ACTION_SEND_TEST || aForm.getAction()==MailingSendAction.ACTION_SEND_WORLD) {
                return (new ActionForward(mapping.getInput()));
            }
        }

        if (!messages.isEmpty()){
            this.saveMessages(req,messages);
        }

        return destination;
    }

    /**
     * Returns a list of invalid links
     * @param aForm MailingSendForm object
     * @param req  HTTP request
     * @return  list of invalid links
     */
    protected List<String> checkForInvalidLinks(MailingSendForm aForm, HttpServletRequest req) {
    	ArrayList<String> invalidlinks = new ArrayList<String>();

		Collection<TrackableLink> links;

    	// retrieve the list of links
		Mailing aMailing = mailingDao.getMailing(aForm.getMailingID(),
				AgnUtils.getAdmin(req).getCompanyID());
		try {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext());
            aMailing.buildDependencies(false, webApplicationContext);
			links = aMailing.getTrackableLinks().values();

		} catch (Exception e) {
			logger.error("checkForInvalidLinks: "+e+"\n"+AgnUtils.getStackTrace(e));
			return invalidlinks;
		}

		invalidlinks.addAll(linkcheckService.checkAvailability(links));
    	return invalidlinks;
    }

    /**
     * Check if there is at list one admin or test recipient binding for the mailing list of the mailing.
     * @param aForm MailingSendForm object
     * @param req HTTP request
     * @return  true==success
     *          false==the mailing list has no admin or test recipient bindings
     */
    protected boolean hasPreviewRecipient(MailingSendForm aForm, HttpServletRequest req) {

    	return mailingDao.hasPreviewRecipients(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());
    }

    protected boolean hasPreviewRecipientPossiblyOtherClient(MailingSendForm aForm, HttpServletRequest req) {
        int companyId = AgnUtils.getAdmin(req).getCompanyID();
        try {
            final Object bulkGenerate = req.getSession().getAttribute("bulkGenerate");
            if (bulkGenerate != null){
                String companyIdString = req.getParameter("previewCompanyId");
                companyId = Integer.valueOf(companyIdString).intValue();
            }
        } catch (Exception e) {
            // do nothing, will take company id from action
        }
    	return mailingDao.hasPreviewRecipients(aForm.getMailingID(), companyId);
    }

    /**
     * Loads mailing data from db into form; also gets list of target groups from db and stores it in the request.
     * @param aForm MailingSendForm object
     * @param req HTTP request
     */
    protected void loadMailing(MailingSendForm aForm, HttpServletRequest req) {
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());

        if(aMailing==null) {
            aMailing = mailingFactory.newMailing();
            aMailing.init(AgnUtils.getAdmin(req).getCompanyID(), getApplicationContext(req));
            aMailing.setId(0);
            aForm.setMailingID(0);
        }

        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setMailingtype(aMailing.getMailingType());
        aForm.setWorldMailingSend(aMailing.isWorldMailingSend());
        aForm.setTargetGroups(aMailing.getTargetGroups());
        aForm.setEmailFormat(aMailing.getEmailParam().getMailFormat());
        aForm.setMailinglistID(aMailing.getMailinglistID());
        aForm.setMailing(aMailing);
        aForm.setHasDeletedTargetGroups( this.targetService.hasMailingDeletedTargetGroups(aMailing));
        String entityName = aMailing.isIsTemplate() ? "template" : "mailing";
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load " + entityName + " " + aMailing.getShortname());
        req.setAttribute("targetGroups", targetDao.getTargets(AgnUtils.getAdmin(req).getCompanyID()));
    }

    /**
     * Loads delivery statistic data from db into the form;
     * adjusts delivery stats frame height and stores the height value in the form;
     * sets flag for displaying admin and test send buttons.
     * @param aForm MailingSendForm object
     * @param req  HTTP request
     * @throws Exception
     */
    protected void loadDeliveryStats(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        DeliveryStat deliveryStat = deliveryStatFactory.createDeliveryStat();
        deliveryStat.setCompanyID(AgnUtils.getAdmin(req).getCompanyID());
        deliveryStat.setMailingID(aForm.getMailingID());
        deliveryStat.getDeliveryStatsFromDB(aForm.getMailingtype());
        aForm.setDeliveryStat(deliveryStat);

        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());
		List<String> targetNames = targetDao.getTargetNamesByIds(AgnUtils.getAdmin(req).getCompanyID(), this.targetService.getTargetIdsFromExpression(aMailing));
		aForm.setTargetGroupsNames(targetNames);

		int frameHeight = 201;
		if (targetNames.size() > 1) {
			frameHeight += (targetNames.size() - 1) * 13;
		}
		aForm.setFrameHeight(frameHeight);

		// set flag for displaying admin- and test-send-buttons
		aForm.setTransmissionRunning(mailingDao.isTransmissionRunning(aForm.getMailingID()));

	}

    /**
     * Tries to cancel mailing delivery and returns true if delivery is canceled, or false - if the delivery could not be
     * canceled or some error occurred on execution of delivery canceling.
     * @param aForm MailingSendForm object
     * @param req HTTP request
     * @return  true=success
     *          false=delivery could not be canceled
     */
    protected boolean cancelMailingDelivery(MailingSendForm aForm, HttpServletRequest req) {
        DeliveryStat deliveryStat = deliveryStatFactory.createDeliveryStat();

        deliveryStat.setCompanyID(AgnUtils.getAdmin(req).getCompanyID());
        deliveryStat.setMailingID(aForm.getMailingID());
        if(deliveryStat.cancelDelivery()) {
            aForm.setWorldMailingSend(false);
            aForm.setMailingtype(Mailing.TYPE_NORMAL);
            return true;
        }
        return false;
    }

    /**
     * Creates maildrop entry object for storing mailing send data (status of send mailing, mailing send date and time,
     * mailing content); checks syntax of mailing content by generating dummy preview; loads maildrop entry into mailing,
     * saves mailing in database; checks send date and time, and sends mailing if it should be sent immediately.
     * @param aForm MailingSendForm object
     * @param req HTTP request
     * @throws Exception
     */
    protected void sendMailing(MailingSendForm aForm, HttpServletRequest req) throws Exception {
    	int stepping, blocksize;
        boolean admin=false;
        boolean test=false;
        boolean world=false;
        java.util.Date sendDate=new java.util.Date();
        java.util.Date genDate=new java.util.Date();
        int startGen=1;
        MaildropEntry maildropEntry = maildropEntryFactory.createMaildropEntry();

        switch(aForm.getAction()) {
            case MailingSendAction.ACTION_SEND_ADMIN:
                maildropEntry.setStatus(MaildropEntry.STATUS_ADMIN);
                admin=true;
                break;

            case MailingSendAction.ACTION_SEND_TEST:
                maildropEntry.setStatus(MaildropEntry.STATUS_TEST);
                admin=true;
                test=true;
                break;

            case MailingSendAction.ACTION_SEND_WORLD:
                maildropEntry.setStatus(MaildropEntry.STATUS_WORLD);
                admin=true;
                test=true;
                world=true;
                break;

            case MailingSendAction.ACTION_ACTIVATE_RULEBASED:
                maildropEntry.setStatus(MaildropEntry.STATUS_DATEBASED);
                world=true;
                break;

            case MailingSendAction.ACTION_ACTIVATE_CAMPAIGN:
                maildropEntry.setStatus(MaildropEntry.STATUS_ACTIONBASED);
                world=true;
        }

        if(aForm.getSendDate()!=null) {
            GregorianCalendar aCal=new GregorianCalendar(TimeZone.getTimeZone(AgnUtils.getAdmin(req).getAdminTimezone()));

            aCal.set(Integer.parseInt(aForm.getSendDate().substring(0, 4)), Integer.parseInt(aForm.getSendDate().substring(4, 6))-1, Integer.parseInt(aForm.getSendDate().substring(6, 8)), aForm.getSendHour(), aForm.getSendMinute());
            sendDate=aCal.getTime();
        }

        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());

        if(aMailing==null) {
            return;
        }
        stepping = 0;
        blocksize = 0;
        try {
			stepping = aForm.getStepping();
			blocksize = aForm.getBlocksize();
		} catch (Exception e) {
			stepping = 0;
			blocksize = 0;
		}

        Mailinglist aList=mailinglistDao.getMailinglist(aMailing.getMailinglistID(), AgnUtils.getAdmin(req).getCompanyID());
        String preview=null;

        if(mailinglistDao.getNumberOfActiveSubscribers(admin, test, world, aMailing.getTargetID(), aList.getCompanyID(), aList.getId())==0) {
            throw new Exception("error.mailing.no_subscribers");
        }

        // check syntax of mailing by generating dummy preview
        preview=aMailing.getPreview(aMailing.getTextTemplate().getEmmBlock(), Mailing.INPUT_TYPE_TEXT, aForm.getPreviewCustomerID(), this.getApplicationContext(req));
        if(preview.trim().length()==0) {
            throw new Exception("error.mailing.no_text_version");
        }
        preview=aMailing.getPreview(aMailing.getHtmlTemplate().getEmmBlock(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getApplicationContext(req));
        if(aForm.getEmailFormat()>0 && preview.trim().length()==0) {
            throw new Exception("error.mailing.no_html_version");
        }
        preview=aMailing.getPreview(aMailing.getEmailParam().getSubject(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getApplicationContext(req));
        if(preview.trim().length()==0) {
            throw new Exception("error.mailing.subject.too_short");
        }
        preview=aMailing.getPreview(aMailing.getEmailParam().getFromAdr(), Mailing.INPUT_TYPE_HTML, aForm.getPreviewCustomerID(), this.getApplicationContext(req));
        if(preview.trim().length()==0) {
            throw new Exception("error.mailing.sender_adress");
        }

        maildropEntry.setSendDate(sendDate);

        if(!DateUtil.isSendDateForImmediateDelivery(sendDate)) {
            // sent gendate if senddate is in future
            GregorianCalendar tmpGen=new GregorianCalendar();
            GregorianCalendar now=new GregorianCalendar();

            tmpGen.setTime(sendDate);
            tmpGen.add(Calendar.HOUR_OF_DAY, -3);
            if(tmpGen.before(now)) {
                tmpGen=now;
            }
            genDate=tmpGen.getTime();
        }

        if(!DateUtil.isDateForImmediateGeneration(genDate) && ((aMailing.getMailingType() == Mailing.TYPE_NORMAL) || (aMailing.getMailingType() == Mailing.TYPE_FOLLOWUP))) {
            startGen=0;
        }

        if(world && aMailing.isWorldMailingSend()) {
            return;
        }

        maildropEntry.setGenStatus(startGen);
        maildropEntry.setGenDate(genDate);
        maildropEntry.setGenChangeDate(new java.util.Date());
        maildropEntry.setMailingID(aMailing.getId());
        maildropEntry.setCompanyID(aMailing.getCompanyID());
        maildropEntry.setStepping(stepping);
		maildropEntry.setBlocksize(blocksize);

        aMailing.getMaildropStatus().add(maildropEntry);

        mailingDao.saveMailing(aMailing);
        if (startGen==1 && maildropEntry.getStatus() != MaildropEntry.STATUS_ACTIONBASED && maildropEntry.getStatus() != MaildropEntry.STATUS_DATEBASED) {
			CmsUtils.generateClassicTemplate(aForm.getMailingID(), req, getApplicationContext(req));
            aMailing.triggerMailing(maildropEntry.getId(), new Hashtable<String, Object>(), this.getApplicationContext(req));
        }
        if (logger.isInfoEnabled()) logger.info("send mailing id: "+aMailing.getId()+" type: "+maildropEntry.getStatus());
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": send mailing " + aMailing.getShortname() + " type: " + maildropEntry.getStatus());
    }

    /**
     * Gets mailing from database, removes maildrop entry of the mailing and save the mailing in database.
     * @param aForm  MailingSendForm object
     * @param req  HTTP request
     * @throws Exception
     */
    protected void deactivateMailing(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());

        if(aMailing==null) {
            return;
        }

        aMailing.cleanupMaildrop(getApplicationContext(req));

        mailingDao.saveMailing(aMailing);
    }

    /**
     * Generates mailing preview by given mailing format. Calls generate preview method; for mailings of HTML or text
     * format, if the generated preview string is empty, analyzes preview content blocks and throws AgnTagException contains
     * report with data about invalid content.
     * Forwards to preview page according to mailing format.
     * @param mapping the ActionMapping used to select this instance
     * @param aForm MailingSendForm object
     * @param req HTTP request
     * @return action forward for displaying page with preview
     * @throws Exception
     */
    protected ActionForward getPreview(ActionMapping mapping, MailingSendForm aForm, HttpServletRequest req) throws Exception {
		Mailing aMailing = mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());
		if (aMailing == null)
			return mapping.findForward("preview." + aForm.getPreviewFormat());
		String[] tmplNames = { "Text", "Html", "FAX", "PRINT", "MMS", "SMS" };

		if (aForm.getPreviewFormat() == Mailing.INPUT_TYPE_HTML || aForm.getPreviewFormat() == Mailing.INPUT_TYPE_TEXT) {
			Preview preview = previewFactory.createPreview();
    		Page output = preview.makePreview (aMailing.getId(), aForm.getPreviewCustomerID(), false);
    		preview.done();

			if (aForm.getPreviewFormat() == Mailing.INPUT_TYPE_HTML) {
				String previewAsString = output.getHTML();

				if (previewAsString == null) {
					String htmlTemplate = aMailing.getHtmlTemplate().getEmmBlock();

					Map<String, DynamicTag> dynTagsMap = aMailing.getDynTags();
					int mailingID = aForm.getMailingID();

					analyzePreview(htmlTemplate, dynTagsMap, mailingID);
				} else {
					aForm.setPreview(previewAsString);
				}
			} else if (aForm.getPreviewFormat() == Mailing.INPUT_TYPE_TEXT) {
				String previewString = output.getText();
				if (previewString == null) {
					String htmlTemplate = aMailing.getTextTemplate().getEmmBlock();

					Map<String, DynamicTag> dynTagsMap = aMailing.getDynTags();
					int mailingID = aForm.getMailingID();

					analyzePreview(htmlTemplate, dynTagsMap, mailingID);
				} else {

					if (previewString.indexOf("<pre>") > -1) {
						previewString = previewString.substring(previewString.indexOf("<pre>") + 5, previewString.length());
					}
					if (previewString.lastIndexOf("</pre>") > -1) {
						previewString = previewString.substring(0, previewString.lastIndexOf("</pre>"));
					}
					aForm.setPreview(previewString);
				}
			}

			aForm.setEmailFormat(aMailing.getEmailParam().getMailFormat());
			aForm.setMailinglistID(aMailing.getMailinglistID());

			return mapping.findForward("preview." + aForm.getPreviewFormat());
		} else {
			aForm.setPreview(aMailing.getPreview(aMailing.getTemplate(tmplNames[aForm.getPreviewFormat()]).getEmmBlock(), aForm.getPreviewFormat(), aForm.getPreviewCustomerID(),
					true, this.getApplicationContext(req)));
			aForm.setEmailFormat(aMailing.getEmailParam().getMailFormat());
			aForm.setMailinglistID(aMailing.getMailinglistID());
			return mapping.findForward("preview." + aForm.getPreviewFormat());
		}
	}

    /**
     * Gets mailing from database, validates mailing subject and from address, if the  subject and the from address is ok,
     * generates preview, parses preview header to get values for sender and subject and loads parsed values into the form.
     * If the validation fails, the method throws AgnTagException contains the report about invalid characters.
     * @param aForm MailingSendForm object
     * @param req HTTP request
     * @throws Exception
     */
    protected void getHeaderPreview(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());

        if(aMailing!=null) {

            TAGCheck tagCheck = tagCheckFactory.createTAGCheck(aForm.getMailingID());
        	String fromParameter = aMailing.getEmailParam().getFromAdr();
        	String subjectParameter = aMailing.getEmailParam().getSubject();
        	List<String[]> errorReport = new ArrayList<String[]>();

        	Vector<String> failures = new Vector<String>();
        	StringBuffer fromReportBuffer = new StringBuffer();
        	boolean fromIsOK = tagCheck.checkContent(fromParameter, fromReportBuffer,failures);
        	if(!fromIsOK) {
        		appendErrorsToList(FROM, errorReport, fromReportBuffer);
        	}

        	StringBuffer subjectReportBuffer = new StringBuffer();
        	boolean subjectIsOK = tagCheck.checkContent(subjectParameter,subjectReportBuffer, failures);
        	if(!subjectIsOK) {
        		appendErrorsToList(SUBJECT, errorReport, subjectReportBuffer);
        	}
        	tagCheck.done();

        	if( fromIsOK && subjectIsOK) {
        		Hashtable<String, Object> output = generateBackEndPreview(aMailing.getId(), aForm.getPreviewCustomerID());
            	String header = (String) output.get(Preview.ID_HEAD);
        		if( header != null) {
        			aForm.setSenderPreview(PreviewHelper.getFrom(header));
        			aForm.setSubjectPreview(PreviewHelper.getSubject(header));
        		} else {
        			aForm.setSenderPreview(fromParameter);
        			aForm.setSubjectPreview(subjectParameter);
        		}

            	// don't know why we need this here, but no time to figure it out
            	aForm.setEmailFormat(aMailing.getEmailParam().getMailFormat());
                aForm.setMailinglistID(aMailing.getMailinglistID());
        	} else {
        		throw new AgnTagException("error.template.dyntags", errorReport);
        	}

        }
    }

    /**
     * Creates report about errors in dynamic tags.
     * @param blockName name of content block with invalid content
     * @param errorReports  list of messages about parsing errors (is changing inside the method)
     * @param templateReport content with errors
     */
    protected void appendErrorsToList(String blockName, List<String[]> errorReports,
			StringBuffer templateReport) {
		Map<String,String> tagsWithErrors = PreviewHelper.getTagsWithErrors(templateReport);
		for(Entry<String,String> entry:tagsWithErrors.entrySet()) {
			String[] errorRow = new String[3];
			errorRow[0] = blockName; // block
			errorRow[1] =  entry.getKey(); // tag
			errorRow[2] =  entry.getValue(); // value

			errorReports.add(errorRow);
		}
		List<String> errorsWithoutATag = PreviewHelper.getErrorsWithoutATag(templateReport);
		for(String error:errorsWithoutATag){
			String[] errorRow = new String[3];
			errorRow[0] = blockName;
			errorRow[1] = "";
			errorRow[2] = error;
			errorReports.add(errorRow);
		}
	}


    /**
     * Loads sent mailing statistics from database.
     * @param aForm MailingSendForm object
     * @param req HTTP request
     * @throws Exception
     */
    protected void loadSendStats(MailingSendForm aForm, HttpServletRequest req) throws Exception {
        int numText=0;
        int numHtml=0;
        int numOffline=0;
        int numTotal=0;
        StringBuffer sqlSelection=new StringBuffer(" ");
        Target aTarget=null;
        boolean isFirst=true;
        int numTargets=0;
        String tmpOp = "AND ";

        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getAdmin(req).getCompanyID());

        if(aMailing.getTargetMode()==Mailing.TARGET_MODE_OR) {
            tmpOp = "OR ";
        }

        if(aForm.getTargetGroups()!=null && aForm.getTargetGroups().size() > 0) {
            Iterator<Integer> aIt=aForm.getTargetGroups().iterator();

            while(aIt.hasNext()) {
                aTarget = targetDao.getTarget((aIt.next()).intValue(), AgnUtils.getAdmin(req).getCompanyID());
                if(aTarget!=null) {
                    if(isFirst) {
                        isFirst=false;
                    } else {
                        sqlSelection.append(tmpOp);
                    }
                    sqlSelection.append("("  + aTarget.getTargetSQL() + ") ");
                    numTargets++;
                }
            }
            if(numTargets>1) {
                sqlSelection.insert(0, " AND (");
            } else {
                sqlSelection.insert(0, " AND ");
            }
            if(!isFirst && numTargets>1) {
                sqlSelection.append(") ");
            }
        }

        String sqlStatement="SELECT count(*), bind.mediatype, cust.mailtype FROM customer_" + AgnUtils.getAdmin(req).getCompanyID() + "_tbl cust, customer_" +
                AgnUtils.getAdmin(req).getCompanyID() + "_binding_tbl bind WHERE bind.mailinglist_id=" + aMailing.getMailinglistID() +
                " AND cust.customer_id=bind.customer_id" + sqlSelection.toString() + " AND bind.user_status=1 GROUP BY bind.mediatype, cust.mailtype";

        Connection con=DataSourceUtils.getConnection(dataSource);

        try {
            Statement stmt=con.createStatement();
            ResultSet rset=stmt.executeQuery(sqlStatement);

            while(rset.next()){
                switch(rset.getInt(2)) {
                    case 0:
                        switch(rset.getInt(3)) {
                            case 0: // nur Text
                                numText+=rset.getInt(1);
                                break;

                            case 1: // Online-HTML
                                if(aMailing.getEmailParam().getMailFormat()==0) { // nur Text-Mailing
                                    numText+=rset.getInt(1);
                                } else {
                                    numHtml+=rset.getInt(1);
                                }
                                break;

                            case 2: // Offline-HTML
                                if(aMailing.getEmailParam().getMailFormat()==0) { // nur Text-Mailing
                                    numText+=rset.getInt(1);
                                }
                                if(aMailing.getEmailParam().getMailFormat()==1) { // nur Text/Online-HTML-Mailing
                                    numHtml+=rset.getInt(1);
                                }
                                if(aMailing.getEmailParam().getMailFormat()==2) { // alle Formate
                                    numOffline+=rset.getInt(1);
                                }
                                break;
                        }
                        break;
                    default:
                        aForm.setSendStat(rset.getInt(2), rset.getInt(1));
                }
            }
            rset.close();
            stmt.close();
        } catch ( Exception e) {
            DataSourceUtils.releaseConnection(con, dataSource);
            logger.error("loadSendStats: " + e);
            logger.error("SQL: " + sqlStatement);
            throw new Exception("SQL-Error: " + e);
        }
        DataSourceUtils.releaseConnection(con, dataSource);

        numTotal+=numText;
        numTotal+=numHtml;
        numTotal+=numOffline;

        aForm.setSendStatText(numText);
        aForm.setSendStatHtml(numHtml);
        aForm.setSendStatOffline(numOffline);
        aForm.setSendStat(0, numTotal);
    }

    /**
     * Generates mailing preview.
     * @param mailingID  id of mailing
     * @param customerID id of customer
     * @return  Hashtable object contains mailing preview
     */
    public Hashtable<String, Object> generateBackEndPreview(int mailingID,int customerID) {
		Preview preview = previewFactory.createPreview();
		Hashtable<String,Object> output = preview.createPreview (mailingID,customerID, false);
		preview.done();
		return output;
	}

    /**
     * Checks content of dynamic tags; prepares error report and raises AgnTagException.
     * @param template mailing template with dynamic tags
     * @param dynTagsMap map of dynamic tags
     * @param mailingID id of mailing
     * @throws Exception
     */
    protected void analyzePreview(String template, Map<String, DynamicTag> dynTagsMap,
			int mailingID) throws Exception {
		Set<String> dynTagsKeys = dynTagsMap.keySet();
		List<String[]> errorReports = new ArrayList<String[]>();
		Vector<String> outFailures = new Vector<String>();
		TAGCheck tagCheck =tagCheckFactory.createTAGCheck(mailingID);

		StringBuffer templateReport = new StringBuffer();
		if( !tagCheck.checkContent(template,templateReport,outFailures)) {
			appendErrorsToList(TEMPLATE,errorReports, templateReport);
		}

		for(String dynTagKey:dynTagsKeys) {

			DynamicTag tag = dynTagsMap.get(dynTagKey);
		    Map<String, DynamicTagContent> tagContentMap = tag.getDynContent();
		    Set<String> tagContentKeys = tagContentMap.keySet();
		    for(String dynContentKey:tagContentKeys) {
		    	DynamicTagContent content = tagContentMap.get(dynContentKey);
		    	{
		    		StringBuffer contentOutReport = new StringBuffer();
		    		if(!tagCheck.checkContent(content.getDynContent(), contentOutReport, outFailures)) {

		    			appendErrorsToList(tag.getDynName(), errorReports, contentOutReport);
		    		}
		    	}
		     }

		}
		throw new AgnTagException("error.template.dyntags", errorReports);
	}

    public TargetDao getTargetDao() {
        return targetDao;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public MailingDao getMailingDao() {
        return mailingDao;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public MailingComponentDao getMailingComponentDao() {
        return mailingComponentDao;
    }

    public void setMailingComponentDao(MailingComponentDao mailingComponentDao) {
        this.mailingComponentDao = mailingComponentDao;
    }

    public LinkcheckService getLinkcheckService() {
        return linkcheckService;
    }

    public void setLinkcheckService(LinkcheckService linkcheckService) {
        this.linkcheckService = linkcheckService;
    }

    public void setMailingFactory(MailingFactory mailingFactory) {
        this.mailingFactory = mailingFactory;
    }

    public DeliveryStatFactory getDeliveryStatFactory() {
        return deliveryStatFactory;
    }

    public void setDeliveryStatFactory(DeliveryStatFactory deliveryStatFactory) {
        this.deliveryStatFactory = deliveryStatFactory;
    }

    public MaildropEntryFactory getMaildropEntryFactory() {
        return maildropEntryFactory;
    }

    public void setMaildropEntryFactory(MaildropEntryFactory maildropEntryFactory) {
        this.maildropEntryFactory = maildropEntryFactory;
    }

    public MailinglistDao getMailinglistDao() {
        return mailinglistDao;
    }

    public void setMailinglistDao(MailinglistDao mailinglistDao) {
        this.mailinglistDao = mailinglistDao;
    }

    public TAGCheckFactory getTagCheckFactory() {
        return tagCheckFactory;
    }

    public void setTagCheckFactory(TAGCheckFactory tagCheckFactory) {
        this.tagCheckFactory = tagCheckFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PreviewFactory getPreviewFactory() {
        return previewFactory;
    }

    public void setPreviewFactory(PreviewFactory previewFactory) {
        this.previewFactory = previewFactory;
    }

    public RecipientDao getRecipientDao() {
        return recipientDao;
    }

    public void setRecipientDao(RecipientDao recipientDao) {
        this.recipientDao = recipientDao;
    }

    public org.springframework.web.context.WebApplicationContext getApplicationContext(HttpServletRequest request) {
        WebApplicationContext webApplicationContext = getWebApplicationContext();
        if (webApplicationContext == null) {
            webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
        }
        return webApplicationContext;
    }

    public void setTargetService( TargetService targetService) {
    	this.targetService = targetService;
    }
}
