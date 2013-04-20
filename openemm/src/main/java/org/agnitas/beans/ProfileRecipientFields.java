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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.List;

/**
 * @author Viktor Gema
 */
public interface ProfileRecipientFields extends Serializable {

    public String getEmail();

    public void setEmail(String email);

    public String getGender();

    public void setGender(String gender);

    public String getMailtype();

    public void setMailtype(String mailtype);

    public String getFirstname();

    public void setFirstname(String firstname);

    public String getLastname();

    public void setLastname(String lastname);

    public String getCreation_date();

    public void setCreation_date(String creation_date);

    public String getChange_date();

    public void setChange_date(String change_date);

    public String getTitle();

    public void setTitle(String title);

    public String getTemporaryId();

    public void setTemporaryId(String customer_id);

    public List<Integer> getUpdatedIds();

    public void addUpdatedIds(Integer updatedId);

    public Map<String, String> getCustomFields();

    public void setCustomFields(Map<String, String> customFields);

    public String getMailtypeDefined();

    public void setMailtypeDefined(String isMailtypeDefined);
}
