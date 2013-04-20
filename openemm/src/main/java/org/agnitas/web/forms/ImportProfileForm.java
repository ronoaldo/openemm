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

package org.agnitas.web.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.ImportProfile;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.importvalues.Charset;
import org.agnitas.util.importvalues.CheckForDuplicates;
import org.agnitas.util.importvalues.DateFormat;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.util.importvalues.NullValuesAction;
import org.agnitas.util.importvalues.Separator;
import org.agnitas.util.importvalues.TextRecognitionChar;
import org.agnitas.web.ImportProfileAction;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Form class that incapsulates the data of import profile
 *
 * @author Vyacheslav Stepanov
 */
public class ImportProfileForm extends StrutsFormBase {

    /**
	 *
	 */
	private static final long serialVersionUID = 3558724976522120933L;

	private static final int MAX_GENDER_VALUE = 2;

    protected int action;

    protected int profileId;

    protected int defaultProfileId;

    protected ImportProfile profile;

    protected String[] allDBColumns;

    protected String addedGender;

    protected int addedGenderInt;

    protected boolean fromListPage;

	protected ImportMode[] availableImportModes;

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public ImportProfile getProfile() {
        return profile;
    }

    public void setProfile(ImportProfile profile) {
        this.profile = profile;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getDefaultProfileId() {
        return defaultProfileId;
    }

    public void setDefaultProfileId(int defaultProfileId) {
        this.defaultProfileId = defaultProfileId;
    }

    public boolean getFromListPage() {
        return fromListPage;
    }

    public void setFromListPage(boolean fromListPage) {
        this.fromListPage = fromListPage;
    }

    public String getAddedGender() {
        return addedGender;
    }

    public void setAddedGender(String addedGender) {
        this.addedGender = addedGender;
    }

    public int getAddedGenderInt() {
        return addedGenderInt;
    }

    public void setAddedGenderInt(int addedGenderInt) {
        this.addedGenderInt = addedGenderInt;
    }

    public Charset[] getCharsets() {
        return Charset.values();
    }

    public Separator[] getSeparators() {
        return Separator.values();
    }

    public DateFormat[] getDateFormats() {
        return DateFormat.values();
    }

    public TextRecognitionChar[] getDelimiters() {
        return TextRecognitionChar.values();
    }

    public ImportMode[] getImportModes() {
        return availableImportModes;
    }

	public void setImportModes(ImportMode[] importModes) {
		availableImportModes = importModes;
	}

    public NullValuesAction[] getNullValuesActions() {
        return NullValuesAction.values();
    }

    public CheckForDuplicates[] getCheckForDuplicatesValues() {
        return CheckForDuplicates.values();
    }

    public String[] getAllDBColumns() {
        return new String[]{"email", "name", "etc"};
    }

	public List<Integer> getGenderValues() {
		List<Integer> genders = new ArrayList<Integer>();
		for (int i = 0; i <= MAX_GENDER_VALUE; i++) {
			genders.add(i);
		}
		return genders;
	}

	public int getGenderQuantity() {
		return getGenderValues().size();
	}

    public ActionErrors formSpecificValidate(ActionMapping actionMapping, HttpServletRequest request) {
        ActionErrors actionErrors = new ActionErrors();

        if (action == ImportProfileAction.ACTION_SAVE) {
            if (AgnUtils.parameterNotEmpty(request, "save")) {
                if (profile.getName().length() < 3) {
                    actionErrors.add("shortname", new ActionMessage("error.nameToShort"));
                }
                if (!GenericValidator.isBlankOrNull(profile.getMailForReport()) &&
                        !GenericValidator.isEmail(profile.getMailForReport())) {
                    actionErrors.add("mailForReport", new ActionMessage("error.invalid.email"));
                }
            }
        }
        return actionErrors;
    }

	public List<String> splitGenderSequence(String genderSequence) {
		List<String> strGenders = new ArrayList<String>();
		StringTokenizer stringTokenizerNewGender = new StringTokenizer(genderSequence, ",");
        while(stringTokenizerNewGender.hasMoreTokens()){
           strGenders.add(stringTokenizerNewGender.nextToken().trim());
        }
		return strGenders;
    }
}
