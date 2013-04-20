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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.dao.TargetDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.exception.target.TargetGroupLockedException;
import org.agnitas.dao.exception.target.TargetGroupPersistenceException;
import org.agnitas.target.*;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Targets
 * 
 * @author Martin Helff, Nicole Serek
 */

public class TargetAction extends StrutsActionBase {

	public static final int ACTION_CREATE_ML = ACTION_LAST + 1;

	public static final int ACTION_CLONE = ACTION_LAST + 2;
	
	public static final int ACTION_DELETE_RECIPIENTS_CONFIRM = ACTION_LAST + 3;
	
	public static final int ACTION_DELETE_RECIPIENTS = ACTION_LAST + 4;
	
	public static final int ACTION_BACK_TO_MAILINGWIZARD = ACTION_LAST + 5;
	
	
	protected TargetDao targetDao;
    private RecipientDao recipientDao;
    private TargetRepresentationFactory targetRepresentationFactory;
    private TargetNodeFactory targetNodeFactory;
    protected TargetFactory targetFactory;
	
	
	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
	 *
     * ACTION_LIST: loads list of target groups into request. Initializes columns width list for the table if
     *     necessary. Forwards to "after_delete" which leads to targets list page.
     * <br><br>
     * ACTION_VIEW: If the target group ID of form is 0 - clears all the rules data in form.<br><br>
     *     Otherwise loads target from database, sets name and description to form and fills form with data from
     *     targetRepresentation property of target group. TargetRepresentation is an object containing list of
     *     target-nodes; each target-node contains information about one rule of target group such as: profile-fields
     *     used, primary operator, chain operator, brackets for current rule etc. For filling the form action
     *     iterates through the target-nodes of targetRepresentation and puts each node's data to form properties at
     *     separate index (for each target rule we have its own index in form properties)<br><br>
     *     Forwards to target group view page.
     * <br><br>
     * ACTION_SAVE: checks if there wasn't adding new rule or deleting existing rule performed.
     *     If the check is passed - performs saving of target group:<br>
     *         creates target representation from form (iterates through rules and creates TargetNode for each rule
     *         contained in form and puts all target nodes to target representation);<br>
     *         generates targetSQL from target representation (this targetSQL is used for creating SQL queries for
     *         filtering recipients matching the target group);<br>
     *         if it is a new target - creates new target object with ID 0;<br>
     *         saves name and description to target group object;<br>
     *         finally saves target group to database;<br>
     *     If there was any problem while saving target-group (target was locked, target wasn't saved in db etc.) -
     *     forwards to target view page with appropriate error message. If the saving was ok - forwards to "success"
     *     (which is currently target view page)
     * <br><br>
     * ACTION_NEW: if there wasn't adding of new rule performed - saves target to database (the detailed description
     *     of that process can be found above in description of ACTION_SAVE). Forwards to target view page.
     * <br><br>
     * ACTION_CONFIRM_DELETE: loads data into form (the detailed description of loading target to form can be found
     *     above in description of ACTION_VIEW), forwards to jsp with question to confirm deletion.
     * <br><br>
     * ACTION_DELETE: marks target group as deleted in database, loads list of target groups into request,
     *     forwards to "after_delete" (currently target groups list page)
     * <br><br>
     * ACTION_CREATE_ML: forwards to jsp with question to confirm of creation new mailing list
     * <br><br>
     * ACTION_CLONE: loads data of chosen target group data into form (see description of loading above). Creates a new
     *     target group in database with that data. Forwards to target view page.
     * <br><br>
     * ACTION_DELETE_RECIPIENTS_CONFIRM: loads target group data into form (see description in ACTION_VIEW). Calculates
     *     number of recipients matching target group and sets that number to form. Forwards to jsp with question to
     *     confirm deletion of recipients of chosen target group.
     * <br><br>
     * ACTION_DELETE_RECIPIENTS: loads target group data into form (see description in ACTION_VIEW). Deletes the
     *     recipients matching target group from database. Loads list of target groups into request. Forwards to
     *     "after_delete" (currently target groups list page)
     * <br><br>
     *
     * Also, with each call of execute, the method updateTargetFormProperties is called (independent of current value
     * of "action" property). That method performs the following functions:<br>
     * If addTargetNode property of form is set - the data of new target node is taken from form properties for new
     * rule and is put to form properties containing data of all rules of target group. The new rule is put at the end
     * of rules list.<br>
     * Method updates the list of possible operations for each rule according to rule type and updates the type of
     * data for each rule;<br>
     * Method removes the target rule with selected index which is set by property targetNodeToRemove (chosen by user
     * on the view page)<br><br>
     *
     * If the destination is "success" - loads list of targets to request.
     *
	 * @param form ActionForm object
     * @param req request
     * @param res response
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		TargetForm aForm = null;
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (TargetForm) form;
		} else {
			aForm = new TargetForm();
		}

		boolean removeSelected = this.updateTargetFormProperties(aForm, req);
		
		AgnUtils.logger().info("Action: " + aForm.getAction());

		if (!allowed("targets.show", req)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
			saveErrors(req, errors);
			return null;
		}

		try {
			switch (aForm.getAction()) {
			case ACTION_LIST:
				destination = mapping.findForward( listTargetGroups( aForm, req));
				break;

			case ACTION_VIEW:
				if (aForm.getTargetID() != 0) {
					aForm.setAction(TargetAction.ACTION_SAVE);
					loadTarget(aForm, req);
				} else {
					aForm.clearRules();
					aForm.setAction(TargetAction.ACTION_NEW);
				}
				destination = mapping.findForward("view");
				break;

			case ACTION_SAVE:
				if (!aForm.getAddTargetNode() && !removeSelected) {
					try {
    					if( saveTarget(aForm, req) != 0) {
    						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
    						destination = mapping.findForward("success");
    					} else {
    						errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.target.saving"));
    						destination = mapping.findForward("view");
    					}
					} catch( TargetGroupLockedException e) {
						errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "target.locked"));
					} catch( TargetGroupPersistenceException e) {
						errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.target.saving.general"));
					}
				} else {
					destination = mapping.findForward("success");
				}
				
				break;

			case ACTION_NEW:
				if (!aForm.getAddTargetNode()) {				
					if(saveTarget(aForm, req) != 0) {
						aForm.setAction(TargetAction.ACTION_SAVE);
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved")); 
					} else {
						aForm.setAction(TargetAction.ACTION_SAVE);
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.target.saving"));
					}
				}
				destination = mapping.findForward("view");
				
				break;

			case ACTION_CONFIRM_DELETE:
				loadTarget(aForm, req);
				destination = mapping.findForward("delete");
				aForm.setAction(TargetAction.ACTION_DELETE);
				break;

			case ACTION_DELETE:
				try {
					this.deleteTarget(aForm, req);
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				} catch( TargetGroupLockedException e) {
					errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("target.locked"));
				} catch( TargetGroupPersistenceException e) {
					errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.target.delete"));
				}
				
				aForm.setAction(TargetAction.ACTION_LIST);
				destination = mapping.findForward(listTargetGroups( aForm, req));
				
				
				break;

			case ACTION_CREATE_ML:
				destination = mapping.findForward("create_ml");
				break;

			case ACTION_CLONE:
				if (aForm.getTargetID() != 0) {
					loadTarget(aForm, req);
					cloneTarget(aForm, req);
					aForm.setAction(TargetAction.ACTION_SAVE);
				}
				destination = mapping.findForward("view");
				break;
				
			case ACTION_DELETE_RECIPIENTS_CONFIRM:
				loadTarget(aForm, req);
				this.getRecipientNumber(aForm, req);
				destination = mapping.findForward("delete_recipients");
				break;
				
			case ACTION_DELETE_RECIPIENTS:
				loadTarget(aForm, req);
				this.deleteRecipients(aForm, req);				
				aForm.setAction(TargetAction.ACTION_LIST);
				destination = mapping.findForward(listTargetGroups( aForm, req));
				break;

			default:
				destination = mapping.findForward(listTargetGroups( aForm, req));
				break;
			}

		} catch (Exception e) {
			AgnUtils.logger().error(
					"execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.exception"));
		}
		
		if( "success".equals(destination.getName())) {
			req.setAttribute("targetlist", loadTargetList(req, aForm) );
			setNumberOfRows(req, aForm);
		}
		

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(req, errors);
			return (new ActionForward(mapping.getInput()));
		}

		// Report any message (non-errors) we have discovered
		if (!messages.isEmpty()) {
			saveMessages(req, messages);
		}

		return destination;

	}

	protected String listTargetGroups( TargetForm form, HttpServletRequest request) {
		if ( form.getColumnwidthsList() == null) {
        	form.setColumnwidthsList(getInitializedColumnWidthList(3));
        }		
		
		request.setAttribute("targetlist", loadTargetList(request, form) );
		setNumberOfRows(request, form);
		
		return "after_delete";
	}
	
	/**
	 * Loads target.
	 */
	protected void loadTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
		Target aTarget = targetDao.getTarget(aForm.getTargetID(),
				getCompanyID(req));

		if (aTarget.getId() == 0) {
			AgnUtils.logger().warn(
					"loadTarget: could not load target " + aForm.getTargetID());
			aTarget = targetFactory.newTarget();
			aTarget.setId(aForm.getTargetID());
		}
		aForm.setShortname(aTarget.getTargetName());
		aForm.setDescription(aTarget.getTargetDescription());
		fillFormFromTargetRepresentation(aForm, aTarget.getTargetStructure());
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load target group  " + aForm.getShortname());
		AgnUtils.logger().info("loadTarget: target " + aForm.getTargetID() + " loaded");
	}

	/**
	 * Clone target.
	 */
	protected void cloneTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
		aForm.setTargetID(0);
		aForm.setShortname(SafeString.getLocaleString("mailing.CopyOf", (Locale) req
				.getSession().getAttribute(Globals.LOCALE_KEY))
				+ " " + aForm.getShortname());
		saveTarget(aForm, req);
	}

	/**
	 * Saves target.
	 */
	protected int saveTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
		Target aTarget = targetDao.getTarget(aForm.getTargetID(),
				getCompanyID(req));

		if (aTarget == null) {
			// be sure to use id 0 if there is no existing object
			aForm.setTargetID(0);
			aTarget = targetFactory.newTarget();
			aTarget.setCompanyID(this.getCompanyID(req));
		}
		
		TargetRepresentation targetRepresentation = createTargetRepresentationFromForm(aForm);

		aTarget.setTargetName(aForm.getShortname());
		aTarget.setTargetDescription(aForm.getDescription());
		aTarget.setTargetSQL(targetRepresentation.generateSQL());
		aTarget.setTargetStructure(targetRepresentation);

		int result = targetDao.saveTarget(aTarget);
        if (aForm.getTargetID() == 0) {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create target group  " + aForm.getShortname());
        } else {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit target group  " + aForm.getShortname());
        }
		AgnUtils.logger().info("saveTarget: save target " + aTarget.getId());
		
		if( aForm.getTargetID() == 0)
			aForm.setTargetID(aTarget.getId());
		
		return result;
	}

	/**
	 * Removes target.
	 */
	protected void deleteTarget(TargetForm aForm, HttpServletRequest req) throws TargetGroupPersistenceException {
		targetDao.deleteTarget(aForm.getTargetID(), getCompanyID(req));
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete target group  " + aForm.getShortname());
	}
	
	/**
	 * Gets number of recipients affected in a target group.
	 */
	protected void getRecipientNumber(TargetForm aForm, HttpServletRequest req) {
		Target target = targetFactory.newTarget();
		target = targetDao.getTarget(aForm.getTargetID(), aForm.getCompanyID(req));
		int numOfRecipients = recipientDao.sumOfRecipients(aForm.getCompanyID(req), target.getTargetSQL());
		
		aForm.setNumOfRecipients(numOfRecipients);
		
	}
	
	/**
	 * Removes recipients affected in a target group.
	 */
	protected void deleteRecipients(TargetForm aForm, HttpServletRequest req) {
		Target target = targetFactory.newTarget();
		target = targetDao.getTarget(aForm.getTargetID(), aForm.getCompanyID(req));
		recipientDao.deleteRecipients(aForm.getCompanyID(req), target.getTargetSQL());
	}
	
	/**
	 * load the list of targets
	 * @param request
	 * @return
	 */
	protected List loadTargetList(HttpServletRequest request, TargetForm form) {
		return targetDao.getTargets(AgnUtils.getCompanyID(request));
	}

    /**
        * add new rule if necessary and update exist target rules and properties of form
        * <br><br>
        *
        * @param form
        * @param request
        * @return success or failed result of removing rules from form
        */
	private boolean updateTargetFormProperties(TargetForm form, HttpServletRequest request) {
		int lastIndex = form.getNumTargetNodes();
        int removeIndex = -1;

        // If "add" was clicked, add new rule
		if (form.getAddTargetNode()) {
           	form.setColumnAndType(lastIndex, form.getColumnAndTypeNew());
        	form.setChainOperator(lastIndex, form.getChainOperatorNew());
        	form.setParenthesisOpened(lastIndex, form.getParenthesisOpenedNew());
        	form.setPrimaryOperator(lastIndex, form.getPrimaryOperatorNew());
        	form.setPrimaryValue(lastIndex, form.getPrimaryValueNew());
        	form.setParenthesisClosed(lastIndex, form.getParenthesisClosedNew());
        	form.setDateFormat(lastIndex, form.getDateFormatNew());
        	form.setSecondaryOperator(lastIndex, form.getSecondaryOperatorNew());
        	form.setSecondaryValue(lastIndex, form.getSecondaryValueNew());

        	form.setAddTargetNode( false);
        	
        	lastIndex++;
        }

		int nodeToRemove = form.getTargetNodeToRemove();

		// Iterate over all target rules
        for(int index = 0; index < lastIndex; index++) {
        	if(index != nodeToRemove) {
        		String colAndType = form.getColumnAndType(index);
        		String column = colAndType.substring(0, colAndType.indexOf('#'));
        		String type = colAndType.substring(colAndType.indexOf('#') + 1);

    			form.setColumnName(index, column);
        		
        		if (type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("VARCHAR2") || type.equalsIgnoreCase("CHAR")) {
        			form.setValidTargetOperators(index, TargetNodeString.getValidOperators());
        			form.setColumnType(index, TargetForm.COLUMN_TYPE_STRING);
        		} else if (type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE") || type.equalsIgnoreCase("NUMBER")) {
        			form.setValidTargetOperators(index, TargetNodeNumeric.getValidOperators());
        			form.setColumnType(index, TargetForm.COLUMN_TYPE_NUMERIC);
        		} else if (type.equalsIgnoreCase("DATE")) {
        			form.setValidTargetOperators(index, TargetNodeDate.getValidOperators());
        			form.setColumnType(index, TargetForm.COLUMN_TYPE_DATE);
        		}
        	} else {
        		if (removeIndex != -1)
        			throw new RuntimeException( "duplicate remove??? (removeIndex = " + removeIndex + ", index = " + index + ")");
        		removeIndex = index;
        	}
		}
        
        if (removeIndex != -1) {
        	form.removeRule(removeIndex);
        	return true;
        } else {
        	return false;
        }
	}                   

     /**
         * fill the data to form from TargetRepresentation object
         * <br><br>
         *
         * @param form
         * @param target
         */
	protected void fillFormFromTargetRepresentation(TargetForm form, TargetRepresentation target) {
		// First, remove all previously defined rules from target form
		form.clearRules();
		
		// Now, convert target nodes to form data
		Iterator<TargetNode> it = target.getAllNodes().iterator();
		int index = 0;
		while (it.hasNext()) {
			TargetNode node = it.next();

			form.setChainOperator(index, node.getChainOperator());
			String primaryField = node.getPrimaryField() == null ? null : node.getPrimaryField().toUpperCase();
            form.setColumnAndType(index, primaryField + "#" + node.getPrimaryFieldType());
			form.setPrimaryOperator(index, node.getPrimaryOperator());
			form.setPrimaryValue(index, node.getPrimaryValue());
			form.setColumnName(index, node.getPrimaryField());
			form.setParenthesisOpened(index, node.isOpenBracketBefore() ? 1 : 0);
			form.setParenthesisClosed(index, node.isCloseBracketAfter() ? 1 : 0);
			
			if (node instanceof TargetNodeString) {
				form.setColumnType(index, TargetForm.COLUMN_TYPE_STRING);
				form.setValidTargetOperators(index, TargetNodeString.getValidOperators());
			} else if (node instanceof TargetNodeNumeric) {
				TargetNodeNumeric numericNode = (TargetNodeNumeric) node;
				
				form.setColumnType(index, TargetForm.COLUMN_TYPE_NUMERIC);
				form.setSecondaryOperator(index, numericNode.getSecondaryOperator());
				form.setSecondaryValue(index, Integer.toString(numericNode.getSecondaryValue()));
				form.setValidTargetOperators(index, TargetNodeNumeric.getValidOperators());
			} else if (node instanceof TargetNodeDate) {
				TargetNodeDate dateNode = (TargetNodeDate) node;
				
				form.setDateFormat(index, dateNode.getDateFormat());
				form.setColumnType(index, TargetForm.COLUMN_TYPE_DATE);
				form.setValidTargetOperators(index, TargetNodeDate.getValidOperators());
			} else {
				// uh oh. It seems, somebody forgot to add a new target node type here :(
				AgnUtils.logger().warn("cannot handle target node class " + node.getClass().getCanonicalName());
				throw new RuntimeException("cannot handle target node class " + node.getClass().getCanonicalName());
			}
			
			index++;
		}
	}
	
	protected TargetRepresentation createTargetRepresentationFromForm(TargetForm form) {
        TargetRepresentation target = targetRepresentationFactory.newTargetRepresentation();
       
        int lastIndex = form.getNumTargetNodes(); 
       
        for(int index = 0; index < lastIndex; index++) {
    		String colAndType = form.getColumnAndType(index);
    		String column = colAndType.substring(0, colAndType.indexOf('#'));
    		String type = colAndType.substring(colAndType.indexOf('#') + 1);
    		
    		TargetNode node = null;
    		
    		if (type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("VARCHAR2") || type.equalsIgnoreCase("CHAR")) {
    			node = createStringNode(form, column, type, index);
    		} else if (type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE") || type.equalsIgnoreCase("NUMBER")) {
    			node = createNumericNode(form, column, type, index);
    		} else if (type.equalsIgnoreCase("DATE")) {
    			node = createDateNode(form, column, type, index);
    		}
    		
            target.addNode(node);
        }
        
        return target;
	}
	
	private TargetNodeString createStringNode(TargetForm form, String column, String type, int index) {
		return targetNodeFactory.newStringNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				form.getPrimaryOperator(index), 
				form.getPrimaryValue(index), 
				form.getParenthesisClosed(index));
	}
	
	private TargetNodeNumeric createNumericNode(TargetForm form, String column, String type, int index) {
		int primaryOperator = form.getPrimaryOperator(index);
		int secondaryOperator = form.getSecondaryOperator(index);
		int secondaryValue = 0;
		
    	if(primaryOperator == TargetNode.OPERATOR_MOD.getOperatorCode()) {
            try {
                secondaryOperator = form.getSecondaryOperator(index);
            } catch (Exception e) {
                secondaryOperator = TargetNode.OPERATOR_EQ.getOperatorCode();
            }
            try {
                secondaryValue = Integer.parseInt(form.getSecondaryValue(index));
            } catch (Exception e) {
                secondaryValue = 0;
            }
        }
		
		return targetNodeFactory.newNumericNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				primaryOperator, 
				form.getPrimaryValue(index), 
				secondaryOperator, 
				secondaryValue, 
				form.getParenthesisClosed(index));
	}
	
	private TargetNodeDate createDateNode(TargetForm form, String column, String type, int index) {
		return targetNodeFactory.newDateNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				form.getPrimaryOperator(index), 
				form.getDateFormat(index), 
				form.getPrimaryValue(index), 
				form.getParenthesisClosed(index));
	}

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setRecipientDao(RecipientDao recipientDao) {
        this.recipientDao = recipientDao;
    }

    public void setTargetRepresentationFactory(TargetRepresentationFactory targetRepresentationFactory) {
        this.targetRepresentationFactory = targetRepresentationFactory;
    }

    public void setTargetNodeFactory(TargetNodeFactory targetNodeFactory) {
        this.targetNodeFactory = targetNodeFactory;
    }

    public void setTargetFactory(TargetFactory targetFactory) {
        this.targetFactory = targetFactory;
    }
}
