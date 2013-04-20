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

package org.agnitas.dao;

import java.util.List;
import java.util.Vector;

import org.agnitas.beans.MailingComponent;

/**
 *
 * @author mhe, nse
 */
public interface MailingComponentDao {

    /**
     * Loads mailing component identified by component id and company id.
     *
     * @param compID
     *            The id of the mailing component that should be loaded.
     * @param companyID
     *            The companyID for mailing component.
     * @return The MailingComponent or null on failure.
     */
    MailingComponent getMailingComponent(int compID, int companyID);

    /**
     * Loads mailing component identified by mailing id, company id and name.
     *
     * @param mailingID
     *            The id of the mailing for mailing component.
     * @param companyID
     *            The companyID for mailing component.
     * @param name
     *            The name of the mailing component.
     * @return The MailingComponent or null on failure.
     */
    MailingComponent getMailingComponentByName(int mailingID, int companyID, String name);

    /**
     * Saves or updates mailing component.
     *
     * @param comp
     *          The mailing component that should be saved.
     */
    void saveMailingComponent(MailingComponent comp);

    /**
     * Deletes mailing component.
     *
     * @param comp
     *          The mailing component that should be deleted.
     */
    void deleteMailingComponent(MailingComponent comp);

    /**
     * Loads all components identified by mailing id, company id and component type.
     *
     * @param mailingID
     *          The id of the mailing for mailing component.
     * @param companyID
     *          The companyID for mailing component.
     * @param componentType
     *          The type for mailing component.
     * @return Vector of MailingComponents.
     */
    Vector<MailingComponent> getMailingComponents(int mailingID, int companyID, int componentType);

    /**
     * Loads all components identified by mailing id and company id.
     *
     * @param mailingID
     *          The id of the mailing for mailing component.
     * @param companyID
     *          The companyID for mailing component.
     * @return Vector of MailingComponents.
     */
	Vector<MailingComponent> getMailingComponents(int mailingID, int companyID);

    /**
     * Loads all components identified by mailing id, company id.
     * And type for these components should be MailingComponent.TYPE_ATTACHMENT or MailingComponent.TYPE_PERSONALIZED_ATTACHMENT.
     *
     * @param mailingID
     *          The id of the mailing for mailing component.
     * @param companyID
     *          The companyID for mailing component.
     * @return Vector of MailingComponents.
     */
	List<MailingComponent> getPreviewHeaderComponents(int mailingID, int companyID);
}
