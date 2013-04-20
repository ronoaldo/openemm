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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.MailingBase;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.target.impl.TargetImpl;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.CompareMailingForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:46 $
 */

public class CompareMailingAction extends StrutsActionBase {
    
    public static final int ACTION_COMPARE = ACTION_LAST+1;

    protected TargetDao targetDao;
    protected MailingDao mailingDao;
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * ACTION_LIST: loads lists of sent mailings (world and action-based; not deleted) into request,
     *     also loads list of target groups into request; forwards to mailing compare page.
     * <br><br>
     * ACTION_COMPARE: loads statistic data of chosen mailings into form; creates csv-file content with
     *     comparison statistics and sets it into form for further use on jsp-page; forwards to comparison
     *     statistics page.
     * <br><br>
	 * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param req The HTTP request we are processing
     * @param res The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */ 
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest req, HttpServletResponse res)
                         throws IOException, ServletException {
        CompareMailingForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        
        if(form==null) {
            aForm=new CompareMailingForm();
        } else {
            aForm=(CompareMailingForm) form;
        }
        
        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        AgnUtils.logger().info("Action: "+aForm.getAction());        
        // "read" action; if none is set, set default action.
        // senseless in this particular case because we have only one action
        try {
            switch(aForm.getAction()) {
                case ACTION_LIST:
                    if(allowed("stats.mailing", req)) {
                        aForm.setAction(ACTION_COMPARE);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    List<Target> targetList = targetDao.getTargets(AgnUtils.getCompanyID(req), false);
                    List<MailingBase> mailingsForComparation = mailingDao.getMailingsForComparation(AgnUtils.getCompanyID(req));
                    aForm.resetForNewCompare();
                    req.setAttribute("targetGroups", targetList);
                    req.setAttribute("mailings", mailingsForComparation);
                    destination=mapping.findForward("list");
                    break;
                case ACTION_COMPARE:
                    if(allowed("stats.mailing", req)) {
                        aForm.setAction(ACTION_COMPARE);
                        compareMailings(aForm, req);
                        List<Target> targetGroupsList = targetDao.getTargets(AgnUtils.getCompanyID(req), false);
                        req.setAttribute("targetGroups", targetGroupsList);
                        destination=mapping.findForward("compare");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("list");
                    }
                    break;
                default:
                    destination=mapping.findForward("list");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return(new ActionForward(mapping.getInput()));
        }
        
        return destination;
    }

    /**
     * Checks which Mailings were chosen for comparison (read from form),
     * for those mailings gets requested statistic information from DB and puts it into the form,
     * displays mailing_compare_struts, in which we display the results from the form
     *
     * @param aForm CompareNailingForm object
     * @param req request
     */
    protected void compareMailings(CompareMailingForm aForm, HttpServletRequest req) {
        Target aTarget = null;
        int companyID = AgnUtils.getCompanyID(req);
        if (aForm.getTargetID() != 0) {
            aTarget = targetDao.getTarget(aForm.getTargetID(), getCompanyID(req));
        } else {
            // just empty default target implementation
            aTarget = new TargetImpl();
            aTarget.setCompanyID(this.getCompanyID(req));
        }
        Locale locale = (Locale) req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        // first reset results that we might have stored in session-form-bean
        aForm.resetResults();
        AgnUtils.logger().info("Loading target: " + aForm.getTargetID() + "/" + companyID);
        String csv_file = "";
        try {
            csv_file = SafeString.getLocaleString("Mailing", locale);
            csv_file += " " + SafeString.getLocaleString("statistic.comparison", locale);
            csv_file += "\r\n\r\n" + SafeString.getLocaleString("target.Target", locale);
            csv_file += ": ;";
            if (aTarget.getId() != 0) {
                csv_file += aTarget.getTargetName();
            } else {
                csv_file += SafeString.getLocaleString("statistic.All_Subscribers", locale);
            }

            csv_file += "\r\n\r\n" + SafeString.getLocaleString("Mailing", locale)
                    + ";" + SafeString.getLocaleString("Recipients", locale)
                    + ";" + SafeString.getLocaleString("statistic.Clicks", locale)
                    + ";" + SafeString.getLocaleString("statistic.opened", locale)
                    + ";" + SafeString.getLocaleString("statistic.Bounces", locale)
                    + ";" + SafeString.getLocaleString("statistic.Opt_Outs", locale)
                    + "\r\n";
        } catch (Exception e) {
            AgnUtils.logger().error("while creating csv header: " + e);
            csv_file = "";
        }

        long timeA = 0;
        timeA = System.currentTimeMillis();
        String mailingIDList = "";
        mailingIDList = StringUtils.join(aForm.getMailings().toArray(), ", ");
        csv_file = mailingDao.compareMailingsNameAndDesc(mailingIDList, aForm.getMailingName(), aForm.getMailingDescription(), companyID);
        aForm.setCvsfile(csv_file);

        int biggestRecipients = mailingDao.compareMailingsSendMailings(mailingIDList, aForm.getNumRecipients(), aForm.getBiggestRecipients(), companyID, aTarget);
        aForm.setBiggestRecipients(biggestRecipients);

        int biggestOpened = mailingDao.compareMailingsOpened(mailingIDList, companyID, aForm.getNumOpen(), aForm.getBiggestOpened(), aTarget);
        aForm.setBiggestOpened(biggestOpened);

        int biggestClicks = mailingDao.compareMailingsTotalClicks(mailingIDList, aForm.getNumClicks(), aForm.getBiggestClicks(), companyID, aTarget);
        aForm.setBiggestClicks(biggestClicks);

        Map optoutBounce = mailingDao.compareMailingsOptoutAndBounce(mailingIDList, aForm.getNumOptout(), aForm.getNumBounce(), aForm.getBiggestOptouts(), aForm.getBiggestBounce(), companyID, aTarget);

        aForm.setBiggestBounce((Integer) optoutBounce.get("biggestBounce"));
        aForm.setBiggestOptouts((Integer) optoutBounce.get("biggestOptout"));


        AgnUtils.logger().info("sendquerytime: " + (System.currentTimeMillis() - timeA));
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }
}
