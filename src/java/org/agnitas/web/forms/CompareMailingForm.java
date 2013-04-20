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


package org.agnitas.web.forms;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.web.CompareMailingAction;
import org.apache.struts.action.*;


public class CompareMailingForm extends StrutsFormBase {
    
    private static final long serialVersionUID = 8456061813681855065L;
	private int targetID;
    private int action;
    
    private int biggestOptouts;     // biggest value from optouts[]     - for the correct graphical representation in the JSP
    private int biggestBounce;      // biggest value from bounces[]     - for the correct graphical representation in the JSP
    private int biggestOpened;      // biggest value from opened[]      - for the correct graphical representation in the JSP
    private int biggestRecipients;  // biggest value from recipients[]  - for the correct graphical representation in the JSP
    private int biggestClicks;      // biggest value from clicks[]      - for the correct graphical representation in the JSP
        
    private String cvsfile;
    
 
    /** 
     * Holds value of property numOpen. 
     */
    private Hashtable numOpen;
    
    /**
     * Holds value of property mailings. 
     */
    protected LinkedList mailings;
    
    /**
     * Holds value of property numBounce. 
     */
    private Hashtable numBounce;
    
    /** 
     * Holds value of property numRecipients. 
     */
    private Hashtable numRecipients;
    
    /**
     * Holds value of property numOptout. 
     */
    private Hashtable numOptout;
    
    /**
     * Holds value of property numClicks. 
     */
    private Hashtable numClicks;
    
    /**
     * Holds value of property mailingName. 
     */
    private Hashtable mailingName;
    
    /**
     * Holds value of property mailingDescription. 
     */
    private Hashtable mailingDescription;
    
      
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        if(mailings==null) {
            resetHashTables();          
        }
    }

	protected void resetHashTables() {
		mailings=new LinkedList();
		mailingDescription=new Hashtable();
		mailingName=new Hashtable();
		numBounce=new Hashtable();
		numClicks=new Hashtable();
		numOpen=new Hashtable();
		numOptout=new Hashtable();
		numRecipients=new Hashtable();
	}

    public void resetForNewCompare(){
        resetResults();
        resetHashTables();
    }

    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */

    public ActionErrors formSpecificValidate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if(this.action==CompareMailingAction.ACTION_COMPARE) {
            String curr;
            Integer tmpInt=null;
            boolean isFirst=true;

            // get all Parameters from Form.
            Enumeration params = request.getParameterNames();
            while(params.hasMoreElements() ) {
                curr=(String)params.nextElement();
                if(curr.startsWith("MailCompID_") ) {	// Form uses "MailCompID_" for storing properties.
                    if(isFirst) { // if selection done, reset lists first
                        this.mailings=null;
                        this.reset(mapping, request);
                        isFirst=false;
                    }
                    tmpInt=new Integer(curr.substring(11, curr.length()));
                    validateCleanUp(tmpInt);

                }
            }

            if(this.mailings.size()<2 || this.mailings.size()>10) {
                errors.add("shortname", new ActionMessage("error.NrOfMailings"));
            }

            Collections.sort(this.mailings, new CompareDescending());
        }
        
        return errors;
    }

	protected void validateCleanUp(Integer tmpInt) {
		this.mailings.add(tmpInt);
		this.numBounce.put(tmpInt, new Integer(0));
		this.numClicks.put(tmpInt, new Integer(0));
		this.numOpen.put(tmpInt, new Integer(0));
		this.numOptout.put(tmpInt, new Integer(0));
		this.numRecipients.put(tmpInt, new Integer(0));
	}

    /**
     * Clear results from previous run.
     */
    public void resetResults() {    
        Iterator aIt=this.mailings.iterator();

        while(aIt.hasNext()) {
            Integer id=(Integer) aIt.next();
            resetNumResults(id);            
        }
        
        resetBiggestResults();        
    }

	protected void resetBiggestResults() {
		biggestBounce=0;
        biggestClicks=0;
        biggestOpened=0;
        biggestOptouts=0;
        biggestRecipients=0;
	}

	protected void resetNumResults(Integer id) {
		numBounce.put(id, new Integer(0));
		numClicks.put(id, new Integer(0));
		numOpen.put(id, new Integer(0));
		numOptout.put(id, new Integer(0));
		numRecipients.put(id, new Integer(0));
	}
    
    
    
    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /**
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    /**
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /**
     * Getter for property biggestOptouts.
     *
     * @return Value of property biggestOptouts.
     */
    public int getBiggestOptouts() {
        return this.biggestOptouts;
    }
    
    /**
     * Setter for property biggestOptouts.
     *
     * @param biggestOptouts New value of property biggestOptouts.
     */
    public void setBiggestOptouts(int biggestOptouts) {
        this.biggestOptouts = biggestOptouts;
    }
    
    /**
     * Getter for property biggestBounce.
     *
     * @return Value of property biggestBounce.
     */
    public int getBiggestBounce() {
        return this.biggestBounce;
    }
    
    /**
     * Setter for property biggestBounce.
     *
     * @param biggestBounce New value of property biggestBounce.
     */
    public void setBiggestBounce(int biggestBounce) {
        this.biggestBounce = biggestBounce;
    }
    
    /**
     * Getter for property biggestOpened.
     *
     * @return Value of property biggestOpened.
     */
    public int getBiggestOpened() {
        return this.biggestOpened;
    }
    
    /**
     * Setter for property biggestOpened.
     *
     * @param biggestOpened New value of property biggestOpened.
     */
    public void setBiggestOpened(int biggestOpened) {
        this.biggestOpened = biggestOpened;
    }
    
    /**
     * Getter for property biggestRecipients.
     *
     * @return Value of property biggestRecipients.
     */
    public int getBiggestRecipients() {
        return this.biggestRecipients;
    }
    
    /**
     * Setter for property biggestRecipients.
     *
     * @param biggestRecipients New value of property biggestRecipients.
     */
    public void setBiggestRecipients(int biggestRecipients) {
        this.biggestRecipients = biggestRecipients;
    }
    
    /**
     * Getter for property biggestClicks.
     *
     * @return Value of property biggestClicks.
     */
    public int getBiggestClicks() {
        return this.biggestClicks;
    }
    
    /**
     * Setter for property biggestClicks.
     *
     * @param biggestClicks New value of property biggestClicks.
     */
    public void setBiggestClicks(int biggestClicks) {
        this.biggestClicks = biggestClicks;
    }
    
    /**
     * Getter for property biggestCvsfile.
     *
     * @return Value of property biggestCvsfile.
     */
    public String getCvsfile() {
        return this.cvsfile;
    }
    
    /**
     * Setter for property cvsfile.
     *
     * @param cvsfile New value of property cvsfile.
     */
    public void setCvsfile(String cvsfile) {
        this.cvsfile = cvsfile;
    }
    
    /** 
     * Getter for property numOpen.
     *
     * @return Value of property numOpen.
     */
    public Hashtable getNumOpen() {
        return this.numOpen;
    }
    
    /**
     * Setter for property numOpen.
     *
     * @param numOpen New value of property numOpen.
     */
    public void setNumOpen(Hashtable numOpen) {
        this.numOpen = numOpen;
    }
    
    /**
     * Getter for property mailings.
     *
     * @return Value of property mailings.
     */
    public LinkedList getMailings() {
        return this.mailings;
    }

    /**
     * Setter for property mailings.
     *
     * @param mailings New value of property mailings.
     */
    public void setMailings(LinkedList mailings) {
        this.mailings = mailings;
    }
    
    /**
     * Getter for property numBounce.
     *
     * @return Value of property numBounce.
     */
    public Hashtable getNumBounce() {
        return this.numBounce;
    }
    
    /**
     * Setter for property numBounce.
     *
     * @param numBounce New value of property numBounce.
     */
    public void setNumBounce(Hashtable numBounce) {
        this.numBounce = numBounce;
    }
    
    /**
     * Getter for property numReceipients.
     *
     * @return Value of property numReceipients.
     */
    public Hashtable getNumRecipients() {
        return this.numRecipients;
    }
    
    /** 
     * Setter for property numReceipients.
     *
     * @param numRecipients New value of property numReceipients.
     */
    public void setNumRecipients(Hashtable numRecipients) {
        this.numRecipients = numRecipients;
    }
    
    /**
     * Getter for property numOptout.
     *
     * @return Value of property numOptout.
     */
    public Hashtable getNumOptout() {
        return this.numOptout;
    }
    
    /**
     * Setter for property numOptout.
     *
     * @param numOptout New value of property numOptout.
     */
    public void setNumOptout(Hashtable numOptout) {
        this.numOptout = numOptout;
    }
    
    /**
     * Getter for property numClicks.
     *
     * @return Value of property numClicks.
     */
    public Hashtable getNumClicks() {
        return this.numClicks;
    }
    
    /**
     * Setter for property numClicks.
     *
     * @param numClicks New value of property numClicks.
     */
    public void setNumClicks(Hashtable numClicks) {
        this.numClicks = numClicks;
    }
    
    /**
     * Getter for property mailingName.
     *
     * @return Value of property mailingName.
     */
    public Hashtable getMailingName() {
        return this.mailingName;
    }
    
    /**
     * Setter for property mailingName.
     *
     * @param mailingName New value of property mailingName.
     */
    public void setMailingName(Hashtable mailingName) {
        this.mailingName = mailingName;
    }
    
    /**
     * Getter for property mailingDescription.
     *
     * @return Value of property mailingDescription.
     */
    public Hashtable getMailingDescription() {
        return this.mailingDescription;
    }
    
    /**
     * Setter for property mailingDescription.
     *
     * @param mailingDescription New value of property mailingDescription.
     */
    public void setMailingDescription(Hashtable mailingDescription) {
        this.mailingDescription = mailingDescription;
    }
    
   
    
    private class CompareDescending implements Comparator {
        public int compare(Object o1, Object o2) {
            Integer a,b;
            if(!(o1 instanceof Integer) || !(o2 instanceof Integer)) { // cannot compare
                return 0;
            }
            a=(Integer)o1;
            b=(Integer)o2;
            
            return (b.intValue()-a.intValue());
        }
    }
}
