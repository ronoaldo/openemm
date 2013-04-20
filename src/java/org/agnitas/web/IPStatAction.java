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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.stat.IPStat;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.IPStatForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


public final class IPStatAction extends StrutsActionBase {

    public static final int ACTION_STAT = 1;
    public static final int ACTION_SPLASH = 2;

	private TargetDao targetDao;
	private MailinglistDao mailinglistDao;
	private IPStat ipStat;

	/**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
	 * <br><br>
	 * ACTION_STAT: starts loading of IP statistics. Requires information about the language. <br>
	 * 		While loading is running, destination is "splash". <br>
	 * 		After loading is finished destination is "stat".
	 * <br><br>
	 * ACTION_SPLASH: only forwards to "splash" or "stat" depending on statistics loading state.
	 * <br><br>
	 * Any other ACTION_* would cause loading of statistics and forwarding to "stat".
	 * <br>
	 * @param form data for the action filled by the jsp
	 * @param req request from jsp
	 * @param res response
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
        IPStatForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;


        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }


        if(form!=null) {
            AgnUtils.logger().info("execute: IPStatForm exists");
            aForm=(IPStatForm)form;
        } else {
            AgnUtils.logger().info("execute: IPStatForm new");
            aForm=new IPStatForm();
        }

        if(!allowed("stats.ip", req)) {
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
                            aForm.setStatReady(false);
                            break;

                        } else {

                            // display splash in browser
                            destination=mapping.findForward("splash");

                            // get stats
                            aForm.setStatInProgress(true);
                            loadIPStats(aForm, req);
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
                    aForm.setAction(IPStatAction.ACTION_STAT);
                    loadIPStats(aForm, req);
                    destination=mapping.findForward("stat");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

		if (destination != null && "stat".equals(destination.getName())) {
			List targets = targetDao.getTargets(getCompanyID(req));
			req.setAttribute("targets", targets);
			List mailinglists = mailinglistDao.getMailinglists(getCompanyID(req));
			req.setAttribute("mailinglists", mailinglists);
		}

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            AgnUtils.logger().info("execute: saving errors "+destination);
        }

        return destination;

    }

    /**
     * Loads IP statistics, requires information about the language.<br>
     * Additional parameters for statics: target group and mailing list.
     *
     * @param aForm a form
     * @param req request
     */
    protected void loadIPStats(IPStatForm aForm, HttpServletRequest req) {
        IPStat aIPStat=null;
        aIPStat = ipStat;

        aIPStat.setCompanyID(this.getCompanyID(req));
        aIPStat.setTargetID(aForm.getTargetID());
        aIPStat.setListID(aForm.getListID());
        aIPStat.setMaxIPs(aForm.getMaxIPs());


        if(aIPStat.getStatFromDB((Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) == true) {
            aForm.setIps(aIPStat.getIps());
            aForm.setSubscribers(aIPStat.getSubscribers());
            aForm.setTotal(aIPStat.getTotal());
            aForm.setBiggest(aIPStat.getBiggest());
            aForm.setLines(aIPStat.getLines());
            aForm.setRest(aIPStat.getRest());
            aForm.setCsvfile(aIPStat.getCsvfile());
            AgnUtils.logger().info("loadIPStats: loaded.");
        } else {
            AgnUtils.logger().warn("loadIPStats: could not load.");
        }
    }

	public void setTargetDao(TargetDao targetDao) {
		this.targetDao = targetDao;
	}

	public TargetDao getTargetDao() {
		return targetDao;
	}

	public void setMailinglistDao(MailinglistDao mailinglistDao) {
		this.mailinglistDao = mailinglistDao;
	}

	public MailinglistDao getMailinglistDao() {
		return mailinglistDao;
	}

	public void setIpStat(IPStat ipStat) {
		this.ipStat = ipStat;
	}

	public IPStat getIpStat() {
		return ipStat;
	}
}
