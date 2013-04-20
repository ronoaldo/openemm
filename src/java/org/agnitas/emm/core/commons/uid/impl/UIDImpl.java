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
package org.agnitas.emm.core.commons.uid.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.agnitas.emm.core.commons.uid.UID;

/**
 * This class creates and validates UIDs used
 * in link creation
 */
@Deprecated
public class UIDImpl implements UID {
    
    /**
     * the company ID 
     */
    protected long      companyID = 0;
    
    /**
     * company ID coded base 36 
     */
    protected String    codedCompanyID = null;
    
    /**
     * the mailing ID 
     */
    protected long      mailingID = 0;
    
    /**
     * mailing ID coded base 36 
     */
    protected String    codedMailingID = null;
    
    /**
     * the customer ID 
     */
    protected long      customerID = 0;
    
    /**
     * customer ID coded base 36 
     */
    protected String    codedCustomerID = null;
    
    /**
     * the URL ID as found in the database 
     */
    protected long      URLID = 0;
    
    /**
     * URL ID coded base 36 
     */
    protected String    codedURLID = null;
    
    /**
     * the signature in the UID 
     */
    protected String    signature = null;
    
    /**
     * the password to create/validate the link 
     */
    protected String    password = null;
    
    /**
     * optional prefix string 
     */
    protected String    prefix = null;
    
    /**
     * instance for creating hash signatures 
     */
    protected MessageDigest hash = null;

    /**
     * Encode a long into a base36 string
     *
     * @param n input value
     * @return the coded string
     */
    private String codeBase36 (long n) {
        return Long.toString (n, 36);
    }

    /**
     * Decode a base36 string into a long
     *
     * @param s the coded string
     * @return the decoded value
     */
    private long decodeBase36 (String s) {
        return Long.parseLong (s, 36);
    }

    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    @Override
    public long getCompanyID () {
        return companyID;
    }
   
    /** 
     * Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
    @Override
    public void setCompanyID (long companyID) {
        this.companyID = companyID;
        codedCompanyID = codeBase36 (companyID);
    }
  
    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    @Override
    public long getMailingID () {
        return mailingID;
    }
    
    /**
     * Setter for property mailingID.
     *
     * @param mailingID New value of property mailingID.
     */
    @Override
    public void setMailingID (long mailingID) {
        this.mailingID = mailingID;
        codedMailingID = codeBase36 (mailingID);
    }
    
    /**
     * Getter for property customerID.
     *
     * @return Value of property customerID.
     */
    @Override
    public long getCustomerID () {
        return customerID;
    }
    
    /**
     * Setter for property customerID.
     *
     * @param customerID New value of property customerID.
     */
    @Override
    public void setCustomerID (long customerID) {
        this.customerID = customerID;
        codedCustomerID = codeBase36 (customerID);
    }
   
    /**
     * Getter for property URLID.
     *
     * @return Value of property URLID.
     */
    @Override
    public long getURLID () {
        return URLID;
    }
    
    /** 
     * Setter for property URLID.
     *
     * @param URLID New vlaue of property URLID.
     */
    @Override
    public void setURLID (long URLID) {
        this.URLID = URLID;
        codedURLID = codeBase36 (URLID);
    }
    
    /** 
     * Getter for property password.
     *
     * @return Value of property password.
     */
    @Override
    public String getPassword () {
        return password;
    }
    
    /** 
     * Setter for property password.
     *
     * @param password New value of property password.
     */
    @Override
    public void setPassword (String password) {
        this.password = password;
    }
    
    /**
     * Getter for property prefix.
     *
     * @return Value of property prefix.
     */
    @Override
    public String getPrefix () {
        return prefix;
    }
    
    /**
     * Setter for property prefix.
     *
     * @param prefix New value of property prefix.
     */
    @Override
    public void setPrefix (String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * Constructor
     *
     * @throws java.lang.Exception 
     */
    public UIDImpl () throws Exception {
        try {
            hash = MessageDigest.getInstance ("sha-1");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception ("Failed to setup UID due to missing SHA-1: " + e.toString ());
        }
    }
    
    /**
     * Constructor with basic informations
     *
     * @param companyID the company ID
     * @param mailingID the mailing ID
     * @param password the password
     * @throws java.lang.Exception 
     */
    public UIDImpl (long companyID, long mailingID, String password) throws Exception {
        this ();
        setCompanyID (companyID);
        setMailingID (mailingID);
        setPassword (password);
    }
    
    /**
     * Constructor for parsing UIDs
     *
     * @param uid the UID to parse
     * @throws java.lang.Exception 
     */
    public UIDImpl (String uid) throws Exception {
        this ();
        parseUID (uid);
    }
    
    /**
     * Create the pure signature
     *
     * @return the signature
     * @param s the string to sign
     * @throws java.lang.Exception 
     */
    public String makeSignature (String s) throws Exception {
        byte[]      result;
        StringBuffer    sig;
        
        hash.reset ();
        try {
            hash.update (s.getBytes ("US-ASCII"));
        } catch (UnsupportedEncodingException e) {
            throw new Exception ("Failed to create hash due to failed conversion to ASCII: " + e.toString ());
        }
        result = hash.digest ();
        sig = new StringBuffer ();
        for (int n = 0; n < result.length; n += 2) {
            long    ch = (((new Byte (result[n])).longValue () & 0xff) >> 2) % 36;

            sig.append (codeBase36 (ch));
        }

        return sig.toString ();
    }
        
    /** 
     * Create the base UID string
     *
     * @return the UID
     */
    @Override
    public String makeBaseUID () {
        return (prefix != null ?  prefix + "." : "") +
            codedCompanyID + "." + codedMailingID + "." + codedCustomerID + "." + codedURLID;
    }

    /**
     * Create a signature when all parameter are set
     *
     * @return signature as string
     * @param base 
     * @throws java.lang.Exception 
     */
    public String createSignature (String base) throws Exception {
        return makeSignature (base + "." + (password == null ? "" : password));
    }
        
    /**
     * Make the final UID string
     *
     * @return UID as string
     * @throws java.lang.Exception 
     */
    @Override
    public String makeUID () throws Exception {
        String  base = makeBaseUID ();
        String  sig = createSignature (base);

        return base + "." + sig;
    }
    
    /**
     * Make the final UID string using given customer id and URL ID
     *
     * @return UID as string
     * @param customerID the customer ID to use
     * @param URLID the URL ID to use
     * @throws java.lang.Exception 
     */
    @Override
    public String makeUID (long customerID, long URLID) throws Exception {
        setCustomerID (customerID);
        setURLID (URLID);
        return makeUID ();
    }
    
    /**
     * Parses an uid
     *
     * @param uid 
     * @throws java.lang.Exception 
     */
    @Override
    public void parseUID (String uid) throws Exception {
        String[]    parts = uid.split ("\\.");

        if ((parts.length == 5) || (parts.length == 6)) {
            try {
                int start = parts.length - 5;
                String  pfix = (start == 0 ? null : parts[0]);
                long    coid = decodeBase36 (parts[start]);
                long    mid = decodeBase36 (parts[start + 1]);
                long    cuid = decodeBase36 (parts[start + 2]);
                long    urlid = decodeBase36 (parts[start + 3]);
                String  sig = parts[start + 4];
                
                setPrefix (pfix);
                setCompanyID (coid);
                setMailingID (mid);
                setCustomerID (cuid);
                setURLID (urlid);
                signature = sig;
            } catch (Exception e) {
                throw new Exception ("Failed in parsing parts: " + e.toString ());
            }
        } else {
            throw new Exception ("Invalid format for UID: " + uid);
        }
    }
    
    /**
     * Validate an UID
     *
     * @return true, if UID is valid
     * @throws java.lang.Exception 
     */
    @Override
    public boolean validateUID () throws Exception {
        String  lsig = createSignature (makeBaseUID ());

        return lsig.equals (signature);
    }

    /**
     * Validate an UID
     *
     * @return true, if UID is valid
     * @param password the password
     * @throws java.lang.Exception 
     */
    @Override
    public boolean validateUID (String password) throws Exception {
        setPassword (password);
        return validateUID ();
    }
}
