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

import java.util.Map;


/**
 *
 * @author mhe
 */
public interface DynamicTag {
   /**
    * Adds a content.
    *
    * @param aContent Added content
    */
	public boolean addContent(DynamicTagContent aContent);

    /**
     * Changes the content order.
     *
     * @param aID
     * @param direction
     */
	public boolean changeContentOrder(int aID, int direction);

	public boolean changeContentOrder(int aID, int direction, boolean searchByOrderId);

    /**
     * Move content down in the list.
     *
     * @param aID
     * @param amount (negative values will move up)
     */
	public boolean moveContentDown(int aID, int amount);

	public boolean moveContentDown(int aID, int amount, boolean searchByOrderId);

    /**
     * Removes a content.
     *
     * @param aID ID of content which will be removed
     */
	public boolean removeContent(int aID);

    /**
     * Getter for property companyId.
     *
     * @return Value of property companyID.
     */
	public int getCompanyID();

    /**
     * Getter for property dynContent.
     *
     * @return Value of property dynContent.
     */
	public Map<String, DynamicTagContent> getDynContent();

    /**
     * Getter for property dynContentCount.
     *
     * @return Value of property dynContentCount.
     */
	public int getDynContentCount();

    /**
     * Getter for property dynContentID.
     *
     * @return Value of property dynContentID.
     */
	public DynamicTagContent getDynContentID(int id);

    /**
     * Getter for property dynName.
     *
     * @return Value of property dynName.
     */
	public String getDynName();

    /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
	public int getId();

    /**
     * Getter for property endTagEnd.
     * 
     * @return Value of property endTagEnd.
     */
	public int getEndTagEnd();

    /**
     * Getter for property endTagStart.
     * 
     * @return Value of property endTagStart.
     */
	public int getEndTagStart();

    /**
     * Getter for property mailingID.
     * 
     * @return Value of property mailingID.
     */
	public int getMailingID();

    /**
     * Getter for property maxOrder.
     * 
     * @return Value of property maxOrder.
     */
	public int getMaxOrder();

    /**
     * Getter for property endPos.
     * 
     * @return Value of property endPos.
     */
	public int getStartTagEnd();

    /**
     * Getter for property startPos.
     * 
     * @return Value of property startPos.
     */
	public int getStartTagStart();

    /**
     * Getter for property valueEnd.
     * 
     * @return Value of property valueEnd.
     */
	public int getValueTagEnd();

    /**
     * Getter for property valueStart.
     * 
     * @return Value of property valueStart.
     */
	public int getValueTagStart();

    /**
     * Getter for property complex.
     * 
     * @return Value of property complex.
     */
	public boolean isComplex();

	public void setCompanyID(int id);

    /**
     * Setter for property complex.
     * 
     * @param complex New value of property complex.
     */
	public void setComplex(boolean complex);

    /**
     * Setter for property dynName.
     * 
     * @param name New value of property dynName.
     */
	public void setDynName(String name);

    /**
     * Setter for property id.
     * 
     * @param id New value of property id.
     */
	public void setId(int id);

    /**
     * Setter for property endTagEnd.
     * 
     * @param endTagEnd New value of property endTagEnd.
     */
	public void setEndTagEnd(int endTagEnd);

    /**
     * Setter for property endTagStart.
     * 
     * @param endTagStart New value of property endTagStart.
     */
	public void setEndTagStart(int endTagStart);

    /**
     * Setter for property mailingId.
     * 
     * @param id New value of property MailingId.
     */
	public void setMailingID(int id);

    /**
     * Setter for property endPos.
     * 
     * @param startTagEnd 
     */
    public void setStartTagEnd(int startTagEnd);

    /**
     * Setter for property startPos.
     * 
     * @param startTagStart 
     */
    public void setStartTagStart(int startTagStart);

    /**
     * Setter for property valueEnd.
     * 
     * @param valueTagEnd 
     */
    public void setValueTagEnd(int valueTagEnd);

    /**
     * Setter for property valueStart.
     * 
     * @param valueTagStart 
     */
    public void setValueTagStart(int valueTagStart);

    /**
     * Setter for property dynContent.
     *
     * @param dynContent New value of property dynContent.
     */
    public void setDynContent(Map<String, DynamicTagContent> dynContent);

    /**
     * Getter for property mailing.
     *
     * @return Value of property mailing.
     */
    public Mailing getMailing();

    /**
     * Setter for property mailing.
     *
     * @param mailing New value of property mailing.
     */
    public void setMailing(Mailing mailing);

    /** Getter for property group.
     * The group i used to group dynamic tags logicaly together. 
     * @return Value of property group.
     */
    public int getGroup();

    /** Setter for property group.
     * 
     * @param group 
     */
    public void setGroup(int group);
}
