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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.factory.DomainStatFactory;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.stat.DomainStat;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.DomainStatForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


public class DomainStatAction extends StrutsActionBase {

    public static final int ACTION_STAT = 1;
    public static final int ACTION_SPLASH = 2;

    private TargetDao targetDao;
    private MailinglistDao mailinglistDao;
    private DataSource dataSource;
    private DomainStatFactory domainStatFactory;


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * ACTION_STAT: loads domain statistic.
     *          While loading process is running, destination is set to "splash".
     *          When the statistic data is ready, destination is set to "stat"
     * <br><br>
     * ACTION_SPLASH: shows splash page while the domain statistic is loading.
     * <br><br>
     * Any other ACTION_* would cause a forward to "stat"
     * <br><br>
     * @param form  ActionForm object, data for the action filled by the jsp
     * @param req HTTP request
     * @param res HTTP response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */

    public ActionForward execute(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest req,
    HttpServletResponse res)
    throws IOException, ServletException {

        // Validate the request parameters specified by the user

        DomainStatForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;


        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }


        if(form!=null) {
            AgnUtils.logger().debug("execute: DomainStatForm exists");
            aForm=(DomainStatForm)form;
        } else {
            AgnUtils.logger().debug("execute: DomainStatForm new");
            aForm=new DomainStatForm();
        }

        if(!allowed("stats.domains", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }

        try {
            switch(aForm.getAction()) {

                case IPStatAction.ACTION_STAT:
                    if(aForm.isStatInProgress()==false) {
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("stat");
                            loadDomainStatFormData(req);
                            aForm.setStatReady(false);
                            break;
                        } else {
                            // display splash in browser
							// RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("splash").getPath());
							//dp.forward(req, res);
							//res.flushBuffer();
							//destination=null;
							destination = mapping.findForward("splash");

                            // get stats
                            aForm.setStatInProgress(true);
                            loadDomainStats(aForm, req);
                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }
                    }
                    break;


                case IPStatAction.ACTION_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;


                default:
                    aForm.setAction(DomainStatAction.ACTION_STAT);
                    loadDomainStats(aForm, req);
                    destination=mapping.findForward("stat");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            AgnUtils.logger().error("execute: errors "+destination);
        }

        return destination;

    }

    /**
     * Loads lists of target groups and mailing lists into request.
     *
     * @param req HTTP request
     */
    protected void loadDomainStatFormData(HttpServletRequest req){
        List<Target> targetList = targetDao.getTargets(getCompanyID(req), true);
        req.setAttribute("targetList", targetList);
        List mailinglists = mailinglistDao.getMailinglists(getCompanyID(req));
        req.setAttribute("mailinglists", mailinglists);
    }

    /**
     * Loads domain statistics from database into the form.
     *
     * @param aForm DomainStatForm object
     * @param req HTTP request
     */
    protected void loadDomainStats(DomainStatForm aForm, HttpServletRequest req) {
        DomainStat aDomStat=domainStatFactory.newDomainStat();

        aForm.setLoaded(false);

        aDomStat.setCompanyID(this.getCompanyID(req));
        aDomStat.setTargetID(aForm.getTargetID());
        aDomStat.setListID(aForm.getListID());
        aDomStat.setMaxDomains(aForm.getMaxDomains());

        if(aDomStat.getStatFromDB(targetDao, dataSource, req)==true) {
            aForm.setDomains(aDomStat.getDomains());
            aForm.setSubscribers(aDomStat.getSubscribers());
            aForm.setTotal(aDomStat.getTotal());
            aForm.setLines(aDomStat.getLines());
            aForm.setRest(aDomStat.getRest());
            aForm.setCsvfile(aDomStat.getCsvfile());
            aForm.setLoaded(true);
            AgnUtils.logger().debug("loadDomainStats: domain stats loaded");
        } else {
            AgnUtils.logger().debug("loadDomainStats: could not load domain stats");
        }
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDomainStatFactory(DomainStatFactory domainStatFactory) {
        this.domainStatFactory = domainStatFactory;
    }

    public void setMailinglistDao(MailinglistDao mailinglistDao) {
        this.mailinglistDao = mailinglistDao;
    }
}
