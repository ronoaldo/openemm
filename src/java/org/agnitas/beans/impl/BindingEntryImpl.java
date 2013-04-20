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

package org.agnitas.beans.impl;

import org.agnitas.beans.BindingEntry;
import org.agnitas.dao.BindingEntryDao;

import java.util.Date;
import java.util.Map;

/** Class holds information about a Customers "Binding" to a Mailinglist
 *
 * @author mhe
 */
public class BindingEntryImpl implements BindingEntry {
    
	private static final long serialVersionUID = -7149749237041195396L;
	
	/** Mailinglist ID for this BindingEntry
     */
    protected int mailinglistID;
    protected int customerID;
    protected int exitMailingID;
    protected String userType;
    protected int userStatus;
    protected String userRemark;
    protected Date changeDate;
    protected Date creationDate;

    private BindingEntryDao bindingEntryDao;

    /** Holds value of property mediaType. */
    protected int mediaType;
    
    /** Creates new, empty BindingEntry
     */
    public BindingEntryImpl() {
        mailinglistID=0;
        customerID=0;
        userType = "W";
        userStatus=0;
        userRemark = "";
        mediaType=BindingEntry.MEDIATYPE_EMAIL;
    }
    
/*    public BindingEntryImpl(int ml, int ci, String ut, int us, String ur) {
        super();
        setMailinglistID(ml);
        setCustomerID(ci);
        setUserType(ut);
        setUserStatus(us);
        setUserRemark(ur);
    }
*/

    public void setBindingEntryDao(BindingEntryDao bindingEntryDao) {
        this.bindingEntryDao = bindingEntryDao;
    }

    public void setMailinglistID(int ml) {
        mailinglistID=ml;
    }
    
    public void setExitMailingID(int mi) {
        exitMailingID=mi;
    }
    
    public int getExitMailingID() {
        return exitMailingID;
    }
    
    public void setCustomerID(int ci) {
        customerID=ci;
    }
    
    public void setUserType(String ut) {
        if(ut.compareTo(USER_TYPE_ADMIN) == 0 ||
           ut.compareTo(USER_TYPE_TESTUSER) == 0 ||
           ut.compareTo(USER_TYPE_TESTVIP) == 0 ||
           ut.compareTo(USER_TYPE_WORLD) == 0 ||
           ut.compareTo(USER_TYPE_WORLDVIP) == 0) {
            userType=ut;
        } else {
            userType=USER_TYPE_WORLD;
        }
    }
    
    public void setUserRemark(String remark) {
        if(remark == null) {
            remark = "";
        }
        userRemark=remark;
    }

    public void setUserStatus(int us) {
        userStatus=us;
    }
    
/*    public void setUserRemark(String ur) {
        userRemark=ur;
    }
*/
    
    public void setChangeDate(Date ts) {
        changeDate=ts;
    }
    
    public int getMailinglistID() {
        return mailinglistID;
    }
    public int getCustomerID() {
        return customerID;
    }
    public String getUserType() {
        return userType;
    }
    public int getUserStatus() {
        return userStatus;
    }
    public String getUserRemark() {
        return userRemark;
    }
    public Date getChangeDate() {
        return changeDate;
    }
    
	public boolean updateStatusInDB(int companyID) {
		return bindingEntryDao.updateStatus(this,  companyID);
	}

    public BindingEntryDao getBindingEntryDao() {
        return bindingEntryDao;
    }

    public boolean saveBindingInDB(int companyID, Map allCustLists) {
        Map types=(Map) allCustLists.get(new Integer(mailinglistID));
        boolean changed=false;

        if(types != null) {
            BindingEntry old=(BindingEntry) types.get(new Integer(mediaType));

            if(old != null) {
                if(old.getExitMailingID() != exitMailingID) {
                    changed=true; 
                }
                if(!old.getUserType().equals(userType)) {
                    changed=true;
                }
                if(old.getUserStatus() != userStatus) {
                    changed=true;
                    if(userStatus == BindingEntry.USER_STATUS_ADMINOUT) { 
                        userRemark="Opt-Out by ADMIN";
                    } else {
                        userRemark="Opt-In by ADMIN";
                    }
                } else {
                    userRemark=old.getUserRemark();
                }
                if(old.getMediaType() != mediaType) {
                    changed=true;
                }
                if(changed == true) {
                    if(updateBindingInDB(companyID) != true) {
                        return false;
                    }
                }
                return true;
            } 
        }
        if(insertNewBindingInDB(companyID) == true) {
            return true;
        }
        return false;
    }

	/**
	 * Updates this Binding in the Database
	 * 
	 * @return True: Sucess
	 * False: Failure
	 * @param companyID The company ID of the Binding
	 */
	public boolean updateBindingInDB(int companyID) {
		return bindingEntryDao.updateBinding(this,  companyID);
	}
    
	public boolean insertNewBindingInDB(int companyID) {
		return bindingEntryDao.insertNewBinding(this,  companyID);
	}
    
	public boolean optOutEmailAdr(String email, int companyID) {
		return bindingEntryDao.optOutEmailAdr(email,  companyID);
	}
    
    /** Getter for property mediaType.
     * @return Value of property mediaType.
     *
     */
    public int getMediaType() {
        return this.mediaType;
    }
    
    /** Setter for property mediaType.
     * @param mediaType New value of property mediaType.
     *
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

	private String	remoteAddr=null;

	public void	setRemoteAddr(String remoteAddr) {
		this.remoteAddr=remoteAddr;
	}

	public String getRemoteAddr()	{
		return remoteAddr;
	}
   
    public String toString() { 
        return "List: "+mailinglistID+" Customer: "+customerID+" ExitID: "+exitMailingID+" Type: "+userType+" Status: "+userStatus+" Remark: "+userRemark+" mediaType: "+mediaType;
    }

     public boolean getUserBindingFromDB(int companyID) {
         return bindingEntryDao.getUserBindingFromDB( this, companyID);
     }

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
