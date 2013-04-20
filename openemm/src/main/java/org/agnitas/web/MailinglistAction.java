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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.Mailinglist;

import org.agnitas.beans.factory.MailinglistFactory;
import org.agnitas.beans.impl.MailinglistImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.*;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class MailinglistAction extends StrutsActionBase {

    public static final int ACTION_MAILINGLIST_RECIPIENTS_DELETE = ACTION_LAST + 3;
    public static final int ACTION_MAILINGLIST_RECIPIENTS_CONFIRM_DELETE = ACTION_LAST + 4;
    public static final int ACTION_MAILINGLIST_RECIPIENTS_DELETE_YES = ACTION_LAST + 5;

    protected MailingDao mailingDao;
    protected TargetDao targetDao;
    protected BindingEntryDao bindingEntryDao;
    protected MailinglistFactory mailinglistFactory;
    protected MailinglistDao mailinglistDao;
    protected RecipientDao recipientDao;
    protected DataSource dataSource;


    class MListCompare implements Comparator {
        public int compare (Object o1, Object o2) {
            Mailinglist m1 = (Mailinglist) o1,
                    m2 = (Mailinglist) o2;
            
            return m2.getId () - m1.getId ();
        }
    }
        
    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br><br>
     * ACTION_LIST: initializes columns width list if necessary. Forwards to "list".
     * <br><br>
     * ACTION_VIEW: loads data of chosen mailinglist into form and forwards to mailinglist view page.
     * <br><br>
     * ACTION_NEW: resets mailinglist id property of form. Forwards to page of creation new mailinglist entry.
     * <br><br>
     * ACTION_SAVE: if there is no mailinglist with the same name - creates new mailinglist or updates existing
     *     mailinglist in database. Sets the ID of mailinglist into form. If the "targetID" parameter of request is
     *     set - creates bindings of created mailinglist to recipients matching the target group with that ID.
     *     Forwards to view page.
     * <br><br>
     * ACTION_CONFIRM_DELETE: loads the mailinglist into form. If mailinglist is not used by any of the mailings -
     *     forwards to delete-confirmation page, otherwise shows error and forwards to "list"
     * <br><br>
     * ACTION_DELETE: deletes bindings of recipients associated with chosen mailinglist from database. Deletes
     *     mailinglist entry from database. Forwards to list.
     * <br><br>
     * ACTION_MAILINGLIST_RECIPIENTS_DELETE: resets recipients deletion options. Forwards to page with choosing
     *     options for deletion of mailinglist recipients.
     * <br><br>
     * ACTION_MAILINGLIST_RECIPIENTS_CONFIRM_DELETE: forwards to mailinglist-recipients deletion confirmation-page.
     * <br><br>
     * ACTION_MAILINGLIST_RECIPIENTS_DELETE_YES: deletes recipients bindings of current mailinglist. Deletes recipients
     *     which were assigned only to current mailinglist. During deletion considers two form-properties set by user:
     *     notAdminsAndTest (if true - removes all but admin- and test-recipients, if false - removes all recipients),
     *     onlyActive (if true - removes only recipients with "active"-status in this mailinglist, if false - doesn't
     *     cares about the status during deletion)
     * <br><br>
     * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * If forward is "list" - initializes columns width list for the table; loads list of mailinglists to request.
     *
     * @param form data for the action filled by the jsp
	 * @param req request from jsp.
	 * @param res response
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

        MailinglistForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;

        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        if(form!=null) {
            aForm=(MailinglistForm)form;
        } else {
            aForm=new MailinglistForm();
        }


        AgnUtils.logger().info("Action: "+aForm.getAction());
        if(AgnUtils.parameterNotEmpty(req, "delete")) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        try {
            switch(aForm.getAction()) {
                case MailinglistAction.ACTION_LIST:
                    if(allowed("mailinglist.show", req)) {
                        if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                    	destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailinglistAction.ACTION_VIEW:
                    if(allowed("mailinglist.show", req)) {
                        loadMailinglist(aForm, req);
                        aForm.setAction(MailinglistAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailinglistAction.ACTION_NEW:
                    if(allowed("mailinglist.new", req)) {
                        aForm.setMailinglistID(0);
                        aForm.setAction(MailinglistAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;


                case MailinglistAction.ACTION_SAVE:
                    if(allowed("mailinglist.change", req) && AgnUtils.parameterNotEmpty(req, "save")) {
                        String targetId = req.getParameter( "targetID" );
                    	if(req.getParameter("save")!=null) {
                            if (!mailingChangedToExisting(aForm, req)) {
                                saveMailinglist(aForm, req);

                                if (StringUtils.isNotEmpty(targetId)) {
                                    // create MailingList from Target
                                    createMailingListFromTarget(targetId, req, aForm);
                                    if (aForm.getColumnwidthsList() == null) {
                                        aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                                    }
                                    // FIXME the result is ignored. Unnecessary call?
                                    // getMailinglist(req, aForm);
                                }
                                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                            } else {
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.mailinglist.duplicate", aForm.getShortname()));
                            }

                    		// Always go back to overview
                            destination = mapping.findForward("view");
                            aForm.setAction(MailinglistAction.ACTION_SAVE);
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailinglistAction.ACTION_CONFIRM_DELETE:
                    if(allowed("mailinglist.delete", req)) {
                        
                        loadMailinglist(aForm, req);
                        List mlids=mailingDao.getMailingsForMLID(getCompanyID(req), aForm.getMailinglistID());

                        if(mlids.size() > 0) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.mailinglist.cannot_delete"));
                            //aForm.setAction(MailinglistAction.ACTION_SAVE);
                            // FIXME the result is ignored. Unnecessary call?
                            // getMailinglist(req, aForm);
                            destination=mapping.findForward("list");
                       } else {
                            aForm.setAction(MailinglistAction.ACTION_DELETE);
                            destination=mapping.findForward("delete");                          
                       }
                    
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }

                    break;

                case MailinglistAction.ACTION_DELETE:
                    if(allowed("mailinglist.delete", req)) {
                        deleteMailinglist(aForm, req);
                        aForm.setAction(MailinglistAction.ACTION_LIST);
                        // FIXME the result is ignored. Unnecessary call?
                        // getMailinglist(req, aForm);
                        destination=mapping.findForward("list");
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                   } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_MAILINGLIST_RECIPIENTS_DELETE: {
                    aForm.setAction(ACTION_MAILINGLIST_RECIPIENTS_CONFIRM_DELETE);
                    aForm.setActiveOnly(false);
                    aForm.setNotAdminsAndTest(false);
                    return mapping.findForward("recipients_delete");
                }

                case ACTION_MAILINGLIST_RECIPIENTS_CONFIRM_DELETE: {
                    aForm.setAction(ACTION_MAILINGLIST_RECIPIENTS_DELETE_YES);
                    return mapping.findForward("confirm_recipients_delete");
                }

                case ACTION_MAILINGLIST_RECIPIENTS_DELETE_YES: {
                    if(!allowed("mailinglist.recipients.delete",req)){
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        saveErrors(req, errors);
                        return mapping.findForward("failure");
                    }

                    aForm.setAction(ACTION_VIEW);
                    Mailinglist mlist = mailinglistDao.getMailinglist(aForm.getMailinglistID(), AgnUtils.getCompanyID(req));
                    aForm.setDescription(mlist.getDescription());
                    aForm.setShortname(mlist.getShortname());
                    boolean onlyActive = aForm.isActiveOnly();
                    boolean notAdminsAndTest = aForm.isNotAdminsAndTest();

                    String tmpTable = recipientDao.createTmpTableByMailinglistID(AgnUtils.getCompanyID(req), aForm.getMailinglistID());
                    recipientDao.deleteRecipientsBindings(aForm.getMailinglistID(), AgnUtils.getCompanyID(req), onlyActive, notAdminsAndTest);
                    recipientDao.deleteAllNoBindings(AgnUtils.getCompanyID(req), tmpTable);

                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("mailinglist.recipients.deleted"));
                    saveMessages(req, messages);
                    return mapping.findForward("view");
                }

                default:
                    if(allowed("mailinglist.show", req)) {
                        if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                        aForm.setAction(MailinglistAction.ACTION_LIST);
                    	destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if(destination != null && "list".equals(destination.getName())) {
            if ( aForm.getColumnwidthsList() == null) 
        		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
        	
        	try {
        		setNumberOfRows(req,(StrutsFormBase)form);
        		req.setAttribute("mailinglistList",getMailinglist(req, aForm));
			} catch (Exception e) {
				AgnUtils.logger().error("getMailinglistList: "+e+"\n"+AgnUtils.getStackTrace(e));
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			} 
        }  

        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        
        // Report any message (non-errors) we have discovered
        if(!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

    /**
     * creates a mailingList from a given Target
     * @param req 
     * @param form
     */
	private void createMailingListFromTarget( String targetIdString, HttpServletRequest req, MailinglistForm form ) {
		Integer targetId = Integer.valueOf( targetIdString );

		if ( targetId == null ) {
			return;
		}
		Target target = targetDao.getTarget( targetId, getCompanyID( req ) );

		if ( target != null ) {

			bindingEntryDao.addTargetsToMailinglist(getCompanyID(req), form.getMailinglistID(), target);
		}
	}

	/**
     * Sets attributes for mailingslists request.
     * @deprecated replaced by getMailinglistList
     */
    protected void list(MailinglistForm aForm, HttpServletRequest req) {
        List mlist = mailinglistDao.getMailinglists(this.getCompanyID(req));
        Object[] temp = mlist.toArray ();
        ArrayList alist;
        Arrays.sort (temp, new MListCompare ());

        alist = new ArrayList (temp.length);
        for (int n = 0; n < temp.length; ++n) {
            alist.add (temp[n]);
        }
        req.setAttribute("mailinglists", alist);
    }

    /**
     * Loads mailingslist.
     */
    protected void loadMailinglist(MailinglistForm aForm, HttpServletRequest req) {

        Mailinglist aMailinglist=mailinglistDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));

        if(aMailinglist!=null) {
            aForm.setShortname(aMailinglist.getShortname());
            aForm.setDescription(aMailinglist.getDescription());
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load mailinglist " + aMailinglist.getShortname());
        } else if(aForm.getMailinglistID() != 0) {
            AgnUtils.logger().warn("loadMailinglist: could not load mailinglist: "+aForm.getMailinglistID());
        }
    }

    /**
     * Saves mailinglist.
     */
    protected boolean saveMailinglist(MailinglistForm aForm, HttpServletRequest req) {


        Mailinglist aMailinglist=mailinglistDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
        boolean is_new=false;

        if(aMailinglist==null) {
            aForm.setMailinglistID(0);
            aMailinglist=mailinglistFactory.newMailinglist();
            aMailinglist.setCompanyID(this.getCompanyID(req));
            is_new=true;
        }
        aMailinglist.setShortname(aForm.getShortname());
        aMailinglist.setDescription(aForm.getDescription());

        mailinglistDao.saveMailinglist(aMailinglist);

        aForm.setMailinglistID(aMailinglist.getId());
        AgnUtils.logger().info("saveMailinglist: save mailinglist id: "+aMailinglist.getId());
        if (is_new){
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create mailinglist " + aMailinglist.getShortname());
        }else{
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit mailinglist " + aMailinglist.getShortname());
        }
        return is_new;
    }

    /**
     * Removes mailinglist.
     */
    protected void deleteMailinglist(MailinglistForm aForm, HttpServletRequest req) {

        if(aForm.getMailinglistID()!=0) {

            Mailinglist aMailinglist=mailinglistDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
            if(aMailinglist!=null) {
                mailinglistDao.deleteBindings(aMailinglist.getId(), aMailinglist.getCompanyID());
                mailinglistDao.deleteMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
                AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete mailinglist " + aMailinglist.getShortname());
            }
        }
    }

   /**
     * Load Mailing lists from database into paginated list.
     */
    public PaginatedList getMailinglist(HttpServletRequest request, MailinglistForm aForm ) throws IllegalAccessException, InstantiationException {
    	
    	ApplicationContext aContext= getWebApplicationContext();
	    JdbcTemplate aTemplate=new JdbcTemplate( dataSource);
	    List<Integer>  charColumns = Arrays.asList(new Integer[]{1,2 });
		String direction = request.getParameter("dir");
     	String sort =  getSort(request, aForm);

        String sortForQuery = StringUtils.isEmpty(sort) ? "shortname" : sort;
	    String upperSort = "upper( " + sortForQuery + " )";
    	    	
    	int rownums = aForm.getNumberofRows() >= 0 ? aForm.getNumberofRows() : 0;	
    	
     	if( direction == null ) {
     		direction = aForm.getOrder();     		
     	} else {
     		aForm.setOrder(direction);
     	}
     	
     	String pageStr = request.getParameter("page");
     	if ( pageStr == null || "".equals(pageStr.trim()) ) {
     		if ( aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
     				aForm.setPage("1");
     		} 
     		pageStr = aForm.getPage();
     	}
     	else {
     		aForm.setPage(pageStr);
     	}
     	
     	if( aForm.isNumberOfRowsChanged() ) {
     		aForm.setPage("1");
     		aForm.setNumberOfRowsChanged(false);
     		pageStr = "1";
     	}

	    String sqlStatement = "SELECT mailinglist_id, shortname, description "; 
	    String sqlStatementFrompart = " FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request);
		String orderBypart = " ORDER BY "+ upperSort +" "+direction;
				
		int totalRows = aTemplate.queryForInt("SELECT count(mailinglist_id) " + sqlStatementFrompart );

        Integer page = Integer.parseInt(pageStr);
        page = AgnUtils.getValidPageNumber(totalRows, page, rownums);
        aForm.setPage(page.toString());

        int offset =  ( page  - 1) * rownums;
				
		if( AgnUtils.isOracleDB() ) {
			sqlStatement += ", rownum r";		
			sqlStatement = "SELECT * FROM (" + sqlStatement + sqlStatementFrompart + orderBypart + ") WHERE r between " + (offset+1) + " and " + ( offset+ rownums );
		}
		if ( AgnUtils.isMySQLDB()) {
			sqlStatement = sqlStatement + sqlStatementFrompart + orderBypart + " LIMIT  " + offset + " , " + rownums;
		}
	        
	    
	    List<Map> tmpList = aTemplate.queryForList(sqlStatement);
	      
        List<Mailinglist> result = new ArrayList<Mailinglist>();
	      
	      for(Map row:tmpList) {
	
	    	  Mailinglist newBean = new MailinglistImpl();
	    	  newBean.setId(((Number)row.get("MAILINGLIST_ID")).intValue());
	    	  newBean.setShortname((String) row.get("SHORTNAME"));
	    	  newBean.setDescription((String) row.get("DESCRIPTION"));
	    	  result.add(newBean);
	    	  
	      } 
	      
	      PaginatedListImpl paginatedList = new PaginatedListImpl(result, totalRows, rownums, page, sort, direction );
	      return paginatedList;
	      
    }
    protected String getSort(HttpServletRequest request, StrutsFormBase aForm) {
		String sort = request.getParameter("sort");  
		 if( sort == null ) {
			 sort = aForm.getSort();			 
		 } else {
			 aForm.setSort(sort);
		 }
		return sort;
	}

    private boolean mailingChangedToExisting(MailinglistForm aForm, HttpServletRequest request) {

        int mailinglistID = aForm.getMailinglistID();
        if (mailinglistID != 0) {
            Mailinglist mailinglist = mailinglistDao.getMailinglist(mailinglistID, AgnUtils.getCompanyID(request));
            if (mailinglist != null && mailinglist.getShortname().equals(aForm.getShortname())){
                return false;
            }
        }
        return mailinglistDao.mailinglistExists(aForm.getShortname(), AgnUtils.getCompanyID(request));
    }


    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setBindingEntryDao(BindingEntryDao bindingEntryDao) {
        this.bindingEntryDao = bindingEntryDao;
    }

    public void setMailinglistDao(MailinglistDao mailinglistDao) {
        this.mailinglistDao = mailinglistDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMailinglistFactory(MailinglistFactory mailinglistFactory) {
        this.mailinglistFactory = mailinglistFactory;
    }

    public void setRecipientDao(RecipientDao recipientDao) {
        this.recipientDao = recipientDao;
    }
}

