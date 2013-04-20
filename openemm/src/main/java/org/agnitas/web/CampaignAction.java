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

import org.agnitas.beans.Campaign;
import org.agnitas.beans.CampaignStats;
import org.agnitas.beans.Company;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.factory.CampaignFactory;
import org.agnitas.dao.CampaignDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.service.CampaignQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.CampaignForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public class CampaignAction extends StrutsActionBase { 
    
	public static final String FUTURE_TASK = "GET_CAMPAIGN_LIST";
    public static final int ACTION_STAT = ACTION_LAST+1;
    public static final int ACTION_SPLASH = ACTION_LAST+2;
    public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST + 3;
    public static final int ACTION_SECOND_LAST = ACTION_LAST + 3;

    protected CampaignDao campaignDao;
    protected CompanyDao companyDao;
    protected ExecutorService executorService;
    protected MailingDao mailingDao;
    protected AbstractMap<String,Future> futureHolder;
    protected CampaignFactory campaignFactory;
    protected TargetDao targetDao;

    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
	 * ACTION_LIST: loads list of campaigns into request and forwards to campaign list page.
	 * <br><br>
	 * ACTION_SAVE: saves campaign entry and forwards to the campaign list page.
	 * <br><br>
     * ACTION_VIEW: resets campaign form, loads data of chosen campaign into form,
     *     loads list of campaign mailings into request, forwards to campaign view page
     * <br><br>
     * ACTION_NEW: creates new campaign db entry; reloads form data; loads list of mailings into request;
     *     forwards to campaign list page.
     * <br><br>
     * ACTION_STAT: loads campaign data into form;<br>
     *     calls a FutureHolder to get the statistic data for sent mailings of the campaign.<br>
     *     If the Future object is not ready, increases the page refresh time by 50ms until it reaches 1 second.
     *     (The page refresh time - is the wait-time before calling the action again while the FutureHolder is
     *     running; the initial value is 250ms)<br>
     *     If the Future object is ready - loads campaign stats into the form; loads list of current
     *     campaign mailings-ids sorted by send date (starting from latest to the earliest); loads list of target
     *     groups into request.<br>
     *     While FutureHolder is running, destination is "splash". <br>
	 * 	   After FutureHolder is finished destination is "stat".
     * <br><br>
     * ACTION_SPLASH: checks the FutureHolder is finished; if the future process is done, forwards to page with
     *     statistic data, otherwise forwards to splash page.
     * <br><br>
     * ACTION_VIEW_WITHOUT_LOAD: is used after failing form validation for loading essential data into request
     *     before returning to the view page. Does not reload form data.
     * <br><br>
	 * ACTION_CONFIRM_DELETE: loads campaign data into form; forwards to jsp with question to confirm deletion
	 * <br><br>
	 * ACITON_DELETE: deletes the entry of certain campaign, forwards to campaign list page.
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
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
    	
        // Validate the request parameters specified by the user        
        CampaignForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;        
        
        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }       
        if(form!=null) {
            aForm=(CampaignForm)form;
        } else {
            aForm=new CampaignForm();
        }       
        
        AgnUtils.logger().info("Action: "+aForm.getAction());
        
        try {
            switch(aForm.getAction()) {
                case CampaignAction.ACTION_LIST:
                    if(allowed("campaign.show", req)) {
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
                    	}
                        destination=mapping.findForward("list");
                        aForm.reset(mapping, req);                       
                        aForm.setAction(CampaignAction.ACTION_LIST);	// reset Action!
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case CampaignAction.ACTION_VIEW:
                    if(allowed("campaign.show", req)) {
                    	aForm.reset(mapping, req);
                        loadCampaign(aForm, req);
                        loadCampaignFormData(aForm, req);
                        aForm.setAction(CampaignAction.ACTION_SAVE);                        
                        destination=mapping.findForward("view");
                        saveToken(req);
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                        aForm.setNumberofRows(50);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case CampaignAction.ACTION_SAVE:
                    if (allowed("campaign.change", req)) {
                        if (isTokenValid(req, true)) {
                            saveCampaign(aForm, req);
                            resetToken(req);
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("list");
                    aForm.setAction(CampaignAction.ACTION_LIST);
                    break;
                    
                case CampaignAction.ACTION_NEW:
                    if(allowed("campaign.show", req)) {
                        aForm.reset(mapping, req);
                        aForm.setAction(CampaignAction.ACTION_SAVE);
                        aForm.setCampaignID(0);
                        saveToken(req);
                        loadCampaignFormData(aForm, req);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("list");
                    }
                    break;
                    
                case CampaignAction.ACTION_CONFIRM_DELETE:
                    loadCampaign(aForm, req);
                    aForm.setAction(CampaignAction.ACTION_DELETE);
                    destination=mapping.findForward("delete");
                    break;
                    
                case CampaignAction.ACTION_DELETE:
                    if(allowed("campaign.show", req)) {
                        if(AgnUtils.parameterNotEmpty(req, "kill")) {
                            this.deleteCampaign(aForm, req);
                            aForm.setAction(CampaignAction.ACTION_LIST);
                            
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("list");
                    break;
                    
                case CampaignAction.ACTION_STAT:
                	destination = mapping.findForward("splash");	// default is splash-screen.
            		aForm.setAction(CampaignAction.ACTION_SPLASH);
            		setNumberOfRows(req,(StrutsFormBase)form);   // could change till next call on this variable is done.
            		loadCampaign(aForm, req);
            		try {
           			    String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
                		// look if we have a Future, if not, get one. 
                		if (!futureHolder.containsKey(key)) { 
                			 Future campaignFuture = getCampaignListFuture(req, aForm);
          				     futureHolder.put(key,campaignFuture);
                		}     
                		// look if we are already done. 
                		if (futureHolder.containsKey(key)  && futureHolder.get(key).isDone()) {
                            CampaignStats stats = campaignFactory.newCampaign().getCampaignStats();
                            stats = (CampaignStats) futureHolder.get(key).get();    // get the results.
                            if (stats != null) {
                                setFormStat(aForm, stats);
                            }
                            setSortedMailingList(stats, req, aForm);
                            aForm.setStatReady(true);
                            aForm.setAction(CampaignAction.ACTION_STAT);
                            destination = mapping.findForward("stat");    // set destination to Statistic-page.
                            futureHolder.remove(key);    // reset Future because we are already done.
                            aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS); // set refresh-time to default.
                            req.setAttribute("targetGroups", targetDao.getTargets(this.getCompanyID(req), true));
                		} else {       
                			// increment Refresh-Rate. if it is a very long request,
                			// we dont have to refresh every 250ms, then 1 second is enough.
                			if( aForm.getRefreshMillis() < 1000 ) { // raise the refresh time
                				aForm.setRefreshMillis( aForm.getRefreshMillis() + 50 );
                			}
                			
                		}   
        			} catch (NullPointerException e) {
        				AgnUtils.logger().error("getCampaignList: "+e+"\n"+AgnUtils.getStackTrace(e));
        	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        			} 
        			
        			break;                  
                    
                case CampaignAction.ACTION_SPLASH:
       			    String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
                	if ( futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
                		aForm.setAction(CampaignAction.ACTION_STAT);
                		destination=mapping.findForward("stat");
                	} else  {
                		loadCampaign(aForm, req);
                		aForm.setAction(CampaignAction.ACTION_SPLASH);
                		destination=mapping.findForward("splash");
                	}
                	break;

                case CampaignAction.ACTION_VIEW_WITHOUT_LOAD:
                    if(allowed("campaign.show", req)) {
                        loadCampaignFormData(aForm, req);
                        aForm.setAction(CampaignAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                	break;
                    
                default:
                    aForm.setAction(CampaignAction.ACTION_LIST);
                    destination=mapping.findForward("list");                    
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if( destination != null && "list".equals(destination.getName())) {
        	try {
        		setNumberOfRows(req,(StrutsFormBase)form);        		
				req.setAttribute("campaignlist", getCampaignList(req ));
			} catch (Exception e) {
				AgnUtils.logger().error("getCampaignList: "+e+"\n"+AgnUtils.getStackTrace(e));
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			} 
        }
       
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        
        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }
       
        return destination;
    }


    /*
     * this method sets the Form-Stats. It would be good, if stat is not null
     */
	private void setFormStat(CampaignForm aForm, CampaignStats stat) {
		if (stat != null) {
			aForm.setOpened(stat.getOpened());
			aForm.setOptouts(stat.getOptouts());
			aForm.setBounces(stat.getBounces());
			aForm.setSubscribers(stat.getSubscribers());
			aForm.setClicks(stat.getClicks());			
			aForm.setMaxClicks(stat.getMaxClicks());
			aForm.setMaxOpened(stat.getMaxOpened());
			aForm.setMaxOptouts(stat.getMaxOptouts());
			aForm.setMaxSubscribers(stat.getMaxSubscribers());
			aForm.setMaxBounces(stat.getMaxBounces());
			aForm.setMailingData(stat.getMailingData());			
		}
	}

    /**
     * Creates linked list of campaign mailings ids with sort order of mailing send date, starting with the last sent mailing,
     * loads the ids' list into the form.
     * @param stat CampaignStats object
     * @param req  HTTP request
     * @param aForm CampaignForm object
     */
	private void setSortedMailingList(CampaignStats stat, HttpServletRequest req, CampaignForm aForm) {
		LinkedList<Number> resultList = new LinkedList<Number>();
		
		// this hashmap contains the mapping from a Date back to the Mail-ID.
		HashMap<Date, Number> tmpDate2MailIDMapping = new HashMap<Date, Number>();
		LinkedList<Date> sortedMailingList = new LinkedList<Date>();
		
		Hashtable map = stat.getMailingData();	// holds the complete mailing Data
		map.keySet();	// all keys for the mailingData (mailIDs)
		
		Number tmpMailID = null;	
		MaildropEntry tmpEntry = null;
		Mailing tmpMailing = null;
		
		// loop over all keys.
		Iterator it = map.keySet().iterator();		
		while (it.hasNext()) {
			LinkedList<Date> sortDates = new LinkedList<Date>();
			tmpMailID = (Number)it.next();	// get the mailID	
			// get one Mailing with tmpMailID
			tmpMailing = (Mailing)mailingDao.getMailing(tmpMailID.intValue(), getCompanyID(req));
			// check if it is a World-Mailing. We have testmailings and dont care about them!
			if (tmpMailing.isWorldMailingSend() == true) {
				// loop over all tmpMailingdropStatus.
				// we look over all mails and take the first send mailing Time.
				// unfortunately is the set not sorted, so we have to sort it ourselves.
				Iterator<MaildropEntry> it2 = tmpMailing.getMaildropStatus().iterator();
				while(it2.hasNext()) {
					tmpEntry = it2.next();		            
					sortDates.add(tmpEntry.getSendDate());		            		            
				}			 
				// check if sortDates has entries and put the one into the Hashmap.
				if (sortDates.size() != 0) {
					Collections.sort(sortDates);
					tmpDate2MailIDMapping.put(sortDates.get(0), tmpMailID);
					sortedMailingList.add(sortDates.get(0));
				}			 
			}
		}
		// at this point, we have a Hashmap with all Dates and Mailing ID's and a List with all Date's.
		// now we sort this List and put the result into the Form (sort with reverse Order ;-) ).
		Collections.sort(sortedMailingList, Collections.reverseOrder());
		// loop over the List and put the corresponding MailID into the List.
		for (int i=0; i < sortedMailingList.size(); i++) {
			resultList.add(tmpDate2MailIDMapping.get(sortedMailingList.get(i)));
		}		
		aForm.setSortedKeys(resultList);
	}

    /**
     * Loads campaign data into form
     * @param aForm CampaignForm object
     * @param req  HTTP request
     */
    protected void loadCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp != null) {
            aForm.setShortname(myCamp.getShortname());
            aForm.setDescription(myCamp.getDescription());
        } else {
            AgnUtils.logger().error("could not load campaign: "+aForm.getTargetID());
        }
    }

    /**
     * Loads list of mailings in the campaign into request
     * @param aForm CampaignForm object
     * @param req  HTTP request
     */
    protected void loadCampaignFormData(CampaignForm aForm, HttpServletRequest req){
        int campaignID = aForm.getCampaignID();
        int companyID = getCompanyID(req);
        req.setAttribute("mailinglist", campaignDao.getCampaignMailings(campaignID, companyID));
    }

    /**
     * Saves campaign in db
     * @param aForm CampaignForm object
     * @param req  HTTP request
     */
    protected void saveCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp == null) {
            aForm.setCampaignID(0);
            myCamp=campaignFactory.newCampaign();
            myCamp.setCompanyID(companyID);
        }
        
        myCamp.setShortname(aForm.getShortname());
        myCamp.setDescription(aForm.getDescription());
        
        campaignID = campaignDao.save(myCamp);
        myCamp.setId(campaignID);
    }

    /**
     * Deletes campaign
     * @param aForm CampaignForm object
     * @param req  HTTP request
     */
    protected void deleteCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp!=null) {
           campaignDao.delete(myCamp);
        }
    } 
    
    /**
     * Loads list of campaigns sorted by given sort parameters
     *
     * @param request  HTTP request
     * @throws InstantiationException 
     * @throws IllegalAccessException
     */

    public List<Campaign> getCampaignList(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
        List<Integer> charColumns = Arrays.asList(new Integer[]{0, 1});
        String[] columns = new String[]{"shortname", "description", ""};

        int sortcolumnindex = 0;
        if (request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_SORT)) != null) {
            sortcolumnindex = Integer.parseInt(request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_SORT)));
        }

        String sort = columns[sortcolumnindex];
        if (charColumns.contains(sortcolumnindex)) {
            sort = "upper( " + sort + " )";
        }

        int order = 1;
        if (request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_ORDER)) != null) {
            order = new Integer(request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_ORDER)));
        }

        List<Campaign> campaigns = campaignDao.getCampaignList(AgnUtils.getCompanyID(request), sort, order);
        return campaigns;
    }

    /**
     * Returns a Future for asynchronous computation of the CampaignStats.
     * @param aForm CampaignForm object
     * @param req  HTTP request
     * @return Future object contains campaign mailings statistic data
     * @throws NumberFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public Future getCampaignListFuture(HttpServletRequest req, CampaignForm aForm  ) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {
    	Company comp = companyDao.getCompany(AgnUtils.getCompanyID(req));
        Locale aLoc = (Locale) req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        boolean mailtracking;
        // i dont know why mailtracking returns an int here, but i use boolean though its not handsome but it works (hopefully).
        if (comp.getMailtracking() == 0) {
        	mailtracking = false;
        } else {
        	mailtracking = true;
        }          	
        
     	// now we start get the data. But we start that as background job.
        // the result is available via future.get().
     	Future future = executorService.submit(	new CampaignQueryWorker(campaignDao, aLoc, aForm, mailtracking, targetDao, aForm.getTargetID(), getCompanyID(req)));
     	
    	return future;  
    }

    public void setCampaignDao(CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
    }

    public void setCompanyDao(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setFutureHolder(AbstractMap<String, Future> futureHolder) {
        this.futureHolder = futureHolder;
    }

    public void setCampaignFactory(CampaignFactory campaignFactory) {
        this.campaignFactory = campaignFactory;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }
}
