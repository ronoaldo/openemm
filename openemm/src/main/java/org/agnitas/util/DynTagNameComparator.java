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

package org.agnitas.util;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator for sorting text-modules in content tab.
 * Compares dynTag names as usual Strings but number values in Strings
 * are compared like int values
 *
 * @author Vyacheslav Stepanov
 * 
 * Comarator sorts Strings by tag names so that names are sorted like usual
 * Strings but number values inside these Strings are compared like numbers
 *
 * Example:
 * "3.2 module"
 * "1 module"
 * "10 module"
 * "3 module"
 * "4 module"
 *
 * will be sorted in a following way:
 * "1 module"
 * "3 module"
 * "3.2 module"
 * "4 module"
 * "10 module"
 */
public class DynTagNameComparator implements Comparator<String> {
    /**
     * Compares two names of dynTags as usual Strings but
     * number values in names are compared like int values
     *
     * @param name1 first name
     * @param name2 second name
     * @return -1 if name1 is lesser; 0 if names are equal; 1 if name1 is greater.
     */
	@Override
	public int compare(String firstName, String secondName) {
		if (firstName.equalsIgnoreCase(secondName)) {
			return 0;
		}
		List<String> firstNameTokens = AgnUtils.splitIntoNumbersAndText(firstName);
		List<String> secondNameTokens = AgnUtils.splitIntoNumbersAndText(secondName);
		int tokensNum = Math.min(firstNameTokens.size(), secondNameTokens.size());
		for (int i = 0; i < tokensNum; i++) {
			String firstToken = firstNameTokens.get(i);
			String secondToken = secondNameTokens.get(i);
			if (firstToken.equalsIgnoreCase(secondToken)) {
				continue;
			} else if (AgnUtils.isDigit(firstToken) && AgnUtils.isDigit(secondToken)) {
				int firstNumber = Integer.parseInt(firstToken);
				int secondNumber = Integer.parseInt(secondToken);
				return firstNumber < secondNumber ? -1 : 1;
			} else {
				return firstName.compareToIgnoreCase(secondName);
			}
		}
		return firstName.compareToIgnoreCase(secondName);
	}
}
