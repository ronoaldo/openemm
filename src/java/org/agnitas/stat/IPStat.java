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

package org.agnitas.stat;

import java.io.Serializable;
import java.util.Locale;

/**
 *
 * @author es
 */
public interface IPStat extends Serializable {

    /**
     * Getter for property stat from database.
     *
     * @return Value of property stat from database.
     */
    boolean getStatFromDB(Locale locale);
   
    /**
     * Setter for property companyID.
     * 
     * @param id New value of property companyID.
     */
    void setCompanyID(int id);
    
    /**
     * Setter for property targetID.
     * 
     * @param id New value of property targetID.
     */
    void setTargetID(int id);
    
    /**
     * Setter for property lidtID.
     * 
     * @param id New value of property listID.
     */
    void setListID(int id);
    
    /**
     * Setter for property total.
     * 
     * @param total New value of property total.
     */
    void setTotal(int total);
    
    /**
     * Setter for property rest.
     * 
     * @param rest New value of property rest.
     */
    void setRest(int rest);
    
    /**
     * Setter for property lines.
     * 
     * @param lines New value of property lines.
     */
    void setLines(int lines);
    
    /**
     * Setter for property ips.
     * 
     * @param ips New value of property ips.
     */
    void setIps(java.util.LinkedList ips);
    
    /**
     * Setter for property subscribers.
     * 
     * @param subscribers New value of property subscribers.
     */
    void setSubscribers(java.util.LinkedList subscribers);
    
    /**
     * Setter for property csvfile.
     * 
     * @param file New value of property csvfile.
     */
    void setCsvfile(String file);
    
    /**
     * Setter for property maxIPs.
     * 
     * @param maxIPs New value of property maxIPs.
     */
    void setMaxIPs(int maxIPs);
    
    /**
     * Setter for property biggest.
     * 
     * @param biggest New value of property biggest.
     */
    void setBiggest(int biggest);

    
    /**
     * Getter for property listID.
     * 
     * @return Value of property listID.
     */
    int getListID();
    
     /**
     * Getter for property targetID.
     * 
     * @return Value of property targetID.
     */
    int getTargetID();
    
     /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();
    
     /**
     * Getter for property total.
     * 
     * @return Value of property total.
     */
    int getTotal();
    
     /**
     * Getter for property rest.
     * 
     * @return Value of property rest.
     */
    int getRest();
    
     /**
     * Getter for property lines.
     * 
     * @return Value of property lines.
     */
    int getLines();
    
     /**
     * Getter for property ips.
     * 
     * @return Value of property ips.
     */
    java.util.LinkedList getIps();
     
    /**
     * Getter for property subscribers.
     * 
     * @return Value of property subscribers.
     */
    java.util.LinkedList getSubscribers();
    
     /**
     * Getter for property maxIps.
     * 
     * @return Value of property maxIps.
     */
    int getMaxIPs();
    
     /**
     * Getter for property csvfile.
     * 
     * @return Value of property csvfile.
     */
    String getCsvfile();
    
     /**
     * Getter for property biggest.
     * 
     * @return Value of property biggest.
     */
    int getBiggest();
}