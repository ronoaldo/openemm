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

import org.agnitas.beans.MailingStatView;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.factory.MailingStatEntryFactory;
import org.agnitas.beans.factory.MailingStatFactory;
import org.agnitas.beans.factory.URLStatEntryFactory;
import org.agnitas.beans.impl.MailingStatViewImpl;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.stat.MailingStat;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MailingStatAction extends StrutsActionBase {

    public static final int ACTION_MAILINGSTAT = ACTION_LAST+1;
    public static final int ACTION_WEEKSTAT = ACTION_LAST+2;
    public static final int ACTION_DAYSTAT = ACTION_LAST+3;
    public static final int ACTION_CLEAN_QUESTION = ACTION_LAST+4;
    public static final int ACTION_CLEAN = ACTION_LAST+5;
    public static final int ACTION_SPLASH = ACTION_LAST+6;
    public static final int ACTION_OPENEDSTAT = ACTION_LAST+7;
    public static final int ACTION_OPENEDSTAT_SPLASH = ACTION_LAST+8;
    public static final int ACTION_BOUNCESTAT = ACTION_LAST+9;
    public static final int ACTION_BOUNCESTAT_SPLASH = ACTION_LAST+10;
    public static final int ACTION_BOUNCE = ACTION_LAST + 11;
    public static final int ACTION_OPEN_TIME = ACTION_LAST + 12;
	public static final int ACTION_OPEN_DAYSTAT = ACTION_LAST + 13;
    public static final int ACTION_MAILING_STAT_LAST = ACTION_LAST+13;

    protected MailingStatFactory mailingStatFactory;
    protected DataSource dataSource;
    protected MailingDao mailingDao;
    protected TargetDao targetDao;
    protected MailingStatEntryFactory mailingStatEntryFactory;
    protected URLStatEntryFactory uRLStatEntryFactory;
    protected RecipientDao recipientDao;


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed. <br> <br>
     * ACTION_LIST: loads page of statistics for chosen mailing, forwards to mailing statistics page.
     * <br><br>
     * ACTION_MAILINGSTAT: loads statistics and list of target groups for chosen mailing into form, <br>
     *            create csv-file with statistics data for download, <br>
     *            forwards to mailing statistics page. <br>
     *            If statistics is not ready yet, forwards to splash page.
     * <br><br>
     * ACTION_SPLASH: if statistics is ready already, forwards to mailing statistics page. <br>
     *            If statistics is not ready yet, forwards to splash page.
     * <br><br>
     * ACTION_OPENEDSTAT_SPLASH: if statistics is ready already, forwards to opened mails statistics page. <br>
     *            If statistics is not ready yet, forwards to splash page.
     * <br><br>
     * ACTION_BOUNCESTAT_SPLASH: if statistics is ready already, forwards to bounces statistics page. <br>
     *            If statistics not ready yet, forwards to splash page.
     * <br><br>
     * ACTION_WEEKSTAT: requires "startdate" parameter in request for week statistics, <br>
     *            loads total clicks week statistics into form, forwards to total clicks week statistics page.
     * <br><br>
     * ACTION_DAYSTAT:  requires "startdate" parameter in request for day statistics, <br>
     *            loads total clicks day statistics into form, forwards to total clicks day statistics page.
     * <br><br>
     * ACTION_CLEAN_QUESTION: forwards to confirmation page of delete admin and test user actions.
     * <br><br>
     * ACTION_CLEAN: deletes admin and test user actions from database, <br>
     *            loads mailing statistics data to form, <br>
     *            forwards to mailing statistics page.
     * <br><br>
     * ACTION_OPENEDSTAT: loads opened mails statistics into form, <br>
     *            forwards to opened mails statistics page.<br>
     *            If statistics is not ready yet, forwards to splash page.
     * <br><br>
     * ACTION_BOUNCESTAT: loads bounces statistics into form, <br>
     *            forwards to bounces statistics page.<br>
     *            If statistics is not ready yet, forwards to splash page.
     * <br><br>
     * ACTION_BOUNCE: loads a list of bounced recipients into the request, <br>
     *             loads bounces statistics into form, <br>
     *            forwards to bounces statistics download page.
     * <br><br>
     * ACTION_OPEN_TIME: loads opened mails week statistics into form, <br>
     *            forwards to opened mails week statistics page.
     * <br><br>
     * ACTION_OPEN_DAYSTAT: loads opened mails day statistics into form, <br>
     *            forwards to opened mails day statistics page.
     * <br><br>
     * Any other ACTION_*: loads statistics and list of target groups for chosen mailing into form, <br>
     *            creates csv-file with statistics data for download, <br>
     *            forwards to mailing statistics page.
     * <br><br>
     * @param form   The optional ActionForm bean for this request (if any)
     * @param req  The HTTP request we are processing
     * @param res  The HTTP response we are creating
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */

    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        MailingStatForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        if(!AgnUtils.isUserLoggedIn(req))
            return mapping.findForward("logon");

        if(form!=null) {
            aForm=(MailingStatForm)form;
        } else {
            aForm=new MailingStatForm();
        }

        AgnUtils.logger().info("Action: " + aForm.getAction());

        if(!allowed("stats.mailing", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }


        try {
            switch(aForm.getAction()) {

                case ACTION_LIST:
                	if ( aForm.getColumnwidthsList() == null) {
                    	aForm.setColumnwidthsList(getInitializedColumnWidthList(2));
                    }	
                    destination=mapping.findForward("list");
                    break;

                case ACTION_MAILINGSTAT:
                    if(aForm.isStatInProgress()==false) {
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("mailing_stat");
                            aForm.setStatReady(false);
                            loadMailingStatFormData(req);
                            break;
                        } else {
                            // display splash in browser
                            destination=mapping.findForward("splash");
                            // get stats
                            aForm.setStatInProgress(true);
                            loadMailingStat(aForm, req);

                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }

                    }
                    break;

                case ACTION_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("mailing_stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;

                case ACTION_OPENEDSTAT_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("opened_stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;

                case ACTION_BOUNCESTAT_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("bounce_stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;


                case ACTION_WEEKSTAT:
                    loadWeekStat(aForm, req);
                    req.setAttribute("targets",targetDao.getTargets(getCompanyID(req),true));
                    destination=mapping.findForward("week_stat");
                    break;

                case ACTION_DAYSTAT:
                    loadDayStat(aForm, req);
                    req.setAttribute("targets",targetDao.getTargets(getCompanyID(req),true));
                    destination=mapping.findForward("day_stat");
                    break;

                case ACTION_CLEAN_QUESTION:
                    destination=mapping.findForward("clean_question");
                    break;

                case ACTION_CLEAN:
                    cleanAdminClicks(aForm, req);
                    loadMailingStat(aForm, req);
                    destination=mapping.findForward("mailing_stat");
                    break;

                case ACTION_OPENEDSTAT:
                    if(aForm.isStatInProgress()==false) {
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("opened_stat");
                            aForm.setStatReady(false);
                            break;
                        } else {
                            destination=mapping.findForward("splash");
                            // get stats
                            aForm.setStatInProgress(true);
                            loadOpenedStat(aForm, req);
                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }
                    }
                    break;

                case ACTION_BOUNCESTAT:
                    if(aForm.isStatInProgress()==false) {
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("bounce_stat");
                            aForm.setStatReady(false);
                            break;
                        } else {
                            destination=mapping.findForward("splash");
//                            // get stats
                            aForm.setStatInProgress(true);
                            loadBounceStat(aForm, req);
                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }
                    }
                    break;
                case ACTION_BOUNCE:
    				destination = mapping.findForward("bounce");
                    loadBounceMailingFormData(req, aForm);
    				break;
    				
                case ACTION_OPEN_TIME:
                    loadOpenWeekStat(aForm, req);
                    destination=mapping.findForward("open_week");
                    break;

                case ACTION_OPEN_DAYSTAT:
                    loadOpenDayStat(aForm, req);
                    destination=mapping.findForward("open_day");
                    break;

                default:
                    aForm.setAction(MailingStatAction.ACTION_MAILINGSTAT);
                    loadMailingStat(aForm, req);
                    destination=mapping.findForward("list");
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if(destination != null &&  "list".equals(destination.getName())) {
        	try {
				req.setAttribute("mailingStatlist", getMailingStats(req));
				setNumberOfRows(req, aForm);
			} catch(Exception e) {
				AgnUtils.logger().error("mailingStatlist: "+e+"\n"+AgnUtils.getStackTrace(e));
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			}        	
        }
        

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        return destination;
    }

    protected void loadMailingStatFormData(HttpServletRequest req){
        List<Target> targetList = targetDao.getTargets(getCompanyID(req), true);
        req.setAttribute("targetList", targetList);
    }

    protected void loadBounceMailingFormData(HttpServletRequest req, MailingStatForm aForm){
        List<Recipient> recipientList = recipientDao.getBouncedMailingRecipients(getCompanyID(req), aForm.getMailingID());
        req.setAttribute("recipientList", recipientList);
    }

    /**
     * Loads mailing statistics.
     */
    protected void loadMailingStat(MailingStatForm aForm, HttpServletRequest req) {
        //set variables from form:

        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        int tid = aForm.getTargetID();
        aMailStat.setTargetID(tid);
        int mid = aForm.getMailingID();
        aMailStat.setMailingID(mid);

        if(aForm.getTargetIDs()!=null) {
            LinkedList targets = aForm.getTargetIDs();
            int atid = aForm.getNextTargetID();
            if(targets.contains(new Integer(atid)) == false) {
                targets.add(new Integer(atid));
            }

            if(req.getParameter("delTargetID")!=null) {
                if( targets.contains(new Integer(req.getParameter("delTargetID"))) && targets.size()>1) {
                    targets.remove(new Integer(req.getParameter("delTargetID")));
                }
            }
            aMailStat.setTargetIDs(targets);
        } else {
            LinkedList targets = new LinkedList();
            targets.add(new Integer(0));
            aMailStat.setTargetIDs(targets);
        }



        // if we come from the mailstat page itself, pass statValues data:
        if(req.getParameter("add")!=null) {
            aMailStat.setStatValues(aForm.getStatValues());
        } else if(req.getParameter("delTargetID")!=null) {
            // delete MailingStatEntry for targetID to be deleted:
            Hashtable tmpStatVal = aForm.getStatValues();
            if(tmpStatVal.containsKey(new Integer(req.getParameter("delTargetID")))) {
                tmpStatVal.remove(new Integer(req.getParameter("delTargetID")));
            }
            // and put the statValues in the MailingStat class:
            aMailStat.setStatValues(tmpStatVal);
        } else {
            // delete all stat info:
            LinkedList targets = new LinkedList();
            targets.add(new Integer(0));
            aMailStat.setTargetIDs(targets);
            Hashtable tmpStatVal = new Hashtable();
            aMailStat.setStatValues(tmpStatVal);
        }

        if(aMailStat.getMailingStatFromDB((Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY))==true) {
            // write results back to form:
            aForm.setCsvfile(aMailStat.getCsvfile());
            aForm.setClickSubscribers( aMailStat.getClickSubscribers() );
            aForm.setClicks(aMailStat.getClicks());
            aForm.setOpenedMails(aMailStat.getOpenedMails());
            aForm.setOptOuts(aMailStat.getOptOuts());
            aForm.setBounces(aMailStat.getBounces());
            aForm.setTotalSubscribers(aMailStat.getTotalSubscribers());
            aForm.setValues(aMailStat.getValues());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
            aForm.setMailingID(mid);
            aForm.setStatValues(aMailStat.getStatValues());
            aForm.setTargetIDs(aMailStat.getTargetIDs());
            aForm.setUrlNames(aMailStat.getUrls());
            aForm.setUrlShortnames(aMailStat.getUrlShortnames());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMaxNRblue(aMailStat.getMaxNRblue());
            aForm.setMaxSubscribers(aMailStat.getMaxSubscribers());
            aForm.setClickedUrls(aMailStat.getClickedUrls());
            aForm.setNotRelevantUrls(aMailStat.getNotRelevantUrls());
        } else {
            AgnUtils.logger().error("loadMailingStat: could not load mailing stats.");
        }
    }

    /**
     * Loads opened statistics.
     */
    protected void loadOpenedStat(MailingStatForm aForm, HttpServletRequest req) {

        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());

        // write results back to form:
        if(aMailStat.getOpenedStatFromDB(req)==true) {
            aForm.setValues(aMailStat.getValues());
            aForm.setCsvfile(aMailStat.getCsvfile());

        } else {
            AgnUtils.logger().error("loadOpenedStat: could not load opened stats.");
        }
    }

    /**
     * Loads bounce statistics.
     */
    protected void loadBounceStat(MailingStatForm aForm, HttpServletRequest req) {

        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());

        // write results back to form:
        if(aMailStat.getBounceStatFromDB(req)==true) {
            aForm.setValues(aMailStat.getValues());
            aForm.setCsvfile(aMailStat.getCsvfile());

        } else {
            AgnUtils.logger().error("loadBounceStat: could not load bounce stats.");
        }
    }

    /**
     * Loads week statistics.
     */
    protected void loadWeekStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());
        aMailStat.setUrlID((new Integer(req.getParameter("urlID"))).intValue());

        if(aForm.isNetto())
            aMailStat.setNetto(true);
        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getWeekStatFromDB(req)==true) {
            aForm.setFirstdate(aMailStat.getFirstdate());
            aForm.setStartdate(aMailStat.getStartdate());
            aForm.setCsvfile(aMailStat.getCsvfile());
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setAktURL(aMailStat.getAktURL());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadWeekStat: could not load week stats.");
        }
    }

    /**
     * Loads day statiitcs.
     */
    protected void loadDayStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());
        aMailStat.setUrlID((new Integer(req.getParameter("urlID"))).intValue());
        if(aForm.isNetto())
            aMailStat.setNetto(true);
        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getDayStatFromDB(req)==true) {
            aForm.setAktURL(aMailStat.getAktURL());
            aForm.setCsvfile(aMailStat.getCsvfile());
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadDayStat: could not load day stats.");
        }
    }
    
    /**
     * Loads week statistics.
     */
    protected void loadOpenWeekStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setMailingID(aForm.getMailingID());

        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getOpenTimeStatFromDB(req)==true) {
            aForm.setFirstdate(aMailStat.getFirstdate());
            aForm.setStartdate(aMailStat.getStartdate());
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadWeekStat: could not load week stats.");
        }
    }
    
    /**
     * Loads day statiitcs.
     */
    protected void loadOpenDayStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setMailingID(aForm.getMailingID());

        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getOpenTimeDayStat(req)==true) {
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadDayStat: could not load day stats.");
        }
    }

    /**
     * Removes the admin clicks.
     */
    protected void cleanAdminClicks(MailingStatForm aForm, HttpServletRequest req) {
        MailingStat aMailStat=mailingStatFactory.newMailingStat();
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setMailingID(aForm.getMailingID());
        aMailStat.cleanAdminClicks();
    }

    /**
     * Get mailing statistic list from database
     *
     * @param request
     * @exception IllegalAccessException
     * @exception InstantiationException
     * @return mailing statistic list
     */
    public List<MailingStatView> getMailingStats(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
	    JdbcTemplate aTemplate=new JdbcTemplate(dataSource);
    	
    	String sqlStatement = "SELECT a.mailing_id, a.shortname, a.description, b.shortname AS listname " +
    			"FROM mailing_tbl a, mailinglist_tbl b WHERE a.company_id="+AgnUtils.getCompanyID(request)+ " " +
    			"AND a.mailinglist_id=b.mailinglist_id AND a.deleted=0 AND a.is_template=0 ORDER BY mailing_id DESC";
    	
    	
    	List<Map> tmpList = aTemplate.queryForList(sqlStatement);
    	List<MailingStatView> result = new ArrayList<MailingStatView>();
    	
    	
    	for(Map row: tmpList) {
    		 MailingStatView newBean = new MailingStatViewImpl();    	
	    	  newBean.setMailingid(((Number)row.get("MAILING_ID")).longValue());
	    	  newBean.setShortname((String) row.get("SHORTNAME"));
	    	  newBean.setDescription((String) row.get("DESCRIPTION"));
	    	  newBean.setListname((String) row.get("LISTNAME"));
	    	  result.add(newBean);
    	}
    	
    	return result;
    }

    public void setMailingStatFactory(MailingStatFactory mailingStatFactory) {
        this.mailingStatFactory = mailingStatFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setMailingStatEntryFactory(MailingStatEntryFactory mailingStatEntryFactory) {
        this.mailingStatEntryFactory = mailingStatEntryFactory;
    }

    public void setuRLStatEntryFactory(URLStatEntryFactory uRLStatEntryFactory) {
        this.uRLStatEntryFactory = uRLStatEntryFactory;
    }

    public void setRecipientDao(RecipientDao recipientDao) {
        this.recipientDao = recipientDao;
    }

    public WebApplicationContext getApplicationContext(HttpServletRequest request) {
        return null;
    }


}
