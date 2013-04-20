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

package org.agnitas.beans;

/**
 *
 * @author mhe
 */
public interface MediatypeEmail extends Mediatype {
    String ONEPIXEL_BOTTOM = "bottom";

    String ONEPIXEL_NONE = "none";

    String ONEPIXEL_TOP = "top";

    /**
     * Getter for property charset.
     * 
     * @return Value of property charset.
     */
    String getCharset();

    /**
     * Getter for property fromAdr.
     * 
     * @return Value of property fromAdr.
     */
    String getFromEmail();

    /**
     * Getter for property fromAdr.
     * 
     * @return Value of property fromAdr.
     */
    String getFromFullname();

    /**
     * Getter for property fromAdr.
     * 
     * @return Value of property fromAdr.
     */
    String getReplyEmail();

    /**
     * Getter for property fromAdr.
     * 
     * @return Value of property fromAdr.
     */
    String getReplyFullname();

    /**
     * Getter for property fromAdr.
     * 
     * @return Value of property fromAdr.
     */
    String getFromAdr() throws Exception;

    /**
     * Getter for property linefeed.
     * 
     * @return Value of property linefeed.
     */
    int getLinefeed();

    /**
     * Getter for property mailFormat.
     * 
     * @return Value of property mailFormat.
     */
    int getMailFormat();

    /**
     * Getter for property onepixel.
     * 
     * @return Value of property onepixel.
     */
    String getOnepixel();

    /**
     * Getter for property replyAdr.
     * 
     * @return Value of property replyAdr.
     */
    String getReplyAdr() throws Exception;

    /**
     * Getter for property subject.
     * 
     * @return Value of property subject.
     */
    String getSubject();

    /**
     * Getter for property subject.
     * 
     * @return Value of property subject.
     */
    String getHtmlTemplate();

    /**
     * Setter for property charset.
     * 
     * @param charset New value of property charset.
     */
    void setCharset(String charset);

    /**
     * Setter for property fromAdr.
     * 
     * @param fromAdr New value of property fromAdr.
     */
    void setFromEmail(String fromEmail);

    /**
     * Setter for property fromAdr.
     * 
     * @param fromAdr New value of property fromAdr.
     */
    void setFromFullname(String fromFullname);

    /**
     * Setter for property fromAdr.
     * 
     * @param fromAdr New value of property fromAdr.
     */
    void setReplyEmail(String replyEmail);

    /**
     * Setter for property fromAdr.
     * 
     * @param fromAdr New value of property fromAdr.
     */
    void setReplyFullname(String replyFullname);

    /**
     * Setter for property linefeed.
     * 
     * @param linefeed New value of property linefeed.
     */
    void setLinefeed(int linefeed);

    /**
     * Setter for property mailFormat.
     * 
     * @param mailFormat New value of property mailFormat.
     */
    void setMailFormat(int mailFormat);

    /**
     * Setter for property onepixel.
     * 
     * @param onepixel New value of property onepixel.
     */
    void setOnepixel(String onepixel);

    /**
     * Setter for property subject.
     * 
     * @param subject New value of property subject.
     */
    void setSubject(String subject);

    /**
     * Setter for property subject.
     * 
     * @param subject New value of property subject.
     */
    void setHtmlTemplate(String htmlTemplate);

    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID();
    
     /**
     * Setter for property mailingID.
     * 
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID);
    
}
