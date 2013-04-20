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

package org.agnitas.util;


import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.exceptions.CharacterEncodingValidationException;
import org.agnitas.exceptions.CharacterEncodingValidationExceptionMod;
import org.agnitas.web.MailingContentForm;
import org.agnitas.web.forms.MailingBaseForm;

/**
 * This class validates the content of a mailing against the character set
 * defined in the mailing.
 * 
 * 
 * @author Markus Dörschmidt
 *
 */
public interface CharacterEncodingValidator {

	/**
	 * Validates text and HTML template of the given form and the content blocks of the given Mailing object. This method is called directly <b>before modifying</b>
	 * the MailingBaseForm object. 
	 * 
	 * @param form form to validate text and HTML template
	 * @param mailing mailing to validate content blocks
	 * @param unencodeableMailingComponents
	 * @param unencodeableDynamicTags
	 */
	public void validate( MailingBaseForm form, Mailing mailing) throws CharacterEncodingValidationException;

	/**
	 * Validates the content from given MailingContentForm.
	 * 
	 * @param form MailingContentForm to be validated
	 * @param mailing mailing object to determined character encoding
	 * @throws CharacterEncodingValidationException
	 */
	public void validate( MailingContentForm form, Mailing mailing) throws CharacterEncodingValidationException;

    /**
	 * Validates the content from given MailingContentForm.
	 *
	 * @param form MailingContentForm to be validated
	 * @param charset mailing charset
	 * @throws CharacterEncodingValidationException
	 */
	public void validate( MailingContentForm form, String charset) throws CharacterEncodingValidationException;
	
	/**
	 * Validates a mailing component.
	 * 
	 * @param component the mailing component to be validated
	 * @param charsetName the name of the character set to be used
	 * @return true if the mailing component passed validated otherwise false
	 */
	public boolean validate( MailingComponent component, String charsetName);
	
	/**
	 * Validates a dynamic tag.
	 * 
	 * @param dynTag DynamicTag to be validated
	 * @param charsetName character set to be used for validation
	 * @return true if the DynamicTag passed validated otherwise false
	 */
	public boolean validate( DynamicTag dynTag, String charsetName);

    public void validateMod( MailingBaseForm form, Mailing mailing) throws CharacterEncodingValidationExceptionMod;

    public void validateContentMod( MailingContentForm form, String charset) throws CharacterEncodingValidationExceptionMod;
	
}
