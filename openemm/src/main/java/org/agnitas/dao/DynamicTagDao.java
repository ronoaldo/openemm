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

import org.agnitas.beans.DynamicTag;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Andreas Rehak, Nicole Serek
 */
public interface DynamicTagDao extends ApplicationContextAware {
    /**
     * Getter for dynamic tag id  by given mailing id and dynamic tag name.
     *
     * @return Value dynamic tag id.
     */
    public int	getIdForName(int mailingID, String name);

    /**
     * Loads list of  DynamicTag objects with established properties Id and DynName for given company and mailing
     *
     * @param companyId The id of the company
     * @param mailingId The id of the mailing
     * @return List of DynamicTag
     */

	public List<DynamicTag> getNameList(int companyId, int mailingId);
}
