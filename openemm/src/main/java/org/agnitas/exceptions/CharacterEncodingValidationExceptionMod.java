package org.agnitas.exceptions;

import java.util.Set;

/**
 * @author: Igor
 */
public class CharacterEncodingValidationExceptionMod extends Exception{
    private final Set<EncodingError> subjectErrors;
	private final Set<EncodingError> failedMailingComponents;
	private final Set<EncodingError> failedDynamicTags;

	public CharacterEncodingValidationExceptionMod( Set<EncodingError> subjectErrors, Set<EncodingError> failedMailingComponents, Set<EncodingError> failedDynamicTags) {
		this.subjectErrors = subjectErrors;
		this.failedMailingComponents = failedMailingComponents;
		this.failedDynamicTags = failedDynamicTags;
	}

	public Set<EncodingError> getFailedMailingComponents() {
		return this.failedMailingComponents;
	}

	public Set<EncodingError> getFailedDynamicTags() {
		return this.failedDynamicTags;
	}

	public Set<EncodingError> getSubjectErrors() {
        return this.subjectErrors;
    }

}
