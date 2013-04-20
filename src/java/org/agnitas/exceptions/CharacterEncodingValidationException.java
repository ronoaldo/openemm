package org.agnitas.exceptions;

import java.util.Set;

public class CharacterEncodingValidationException extends Exception {

	private final boolean subjectValid;
	private final Set<String> failedMailingComponents;
	private final Set<String> failedDynamicTags;
	
	public CharacterEncodingValidationException( boolean subjectValid, Set<String> failedMailingComponents, Set<String> failedDynamicTags) {
		this.subjectValid = subjectValid;
		this.failedMailingComponents = failedMailingComponents;
		this.failedDynamicTags = failedDynamicTags;
	}

	public CharacterEncodingValidationException( Set<String> failedMailingComponents, Set<String> failedDynamicTags) {
		this( true, failedMailingComponents, failedDynamicTags);
	}

	public Set<String> getFailedMailingComponents() {
		return this.failedMailingComponents;
	}
	
	public Set<String> getFailedDynamicTags() {
		return this.failedDynamicTags;
	}
	
	public boolean isSubjectValid() {
		return this.subjectValid;
	}
}
