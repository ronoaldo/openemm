package org.agnitas.emm.core.commons.uid.builder;

import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.RequiredInformationMissingException;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.UIDStringBuilderException;

/**
 * Interface for implementations to convert a ExtensibleUID object to a String representation.
 * 
 * @author md
 */
public interface ExtensibleUIDStringBuilder {
	/**
	 * Convert instance of ExtensibleUID to String.
	 * 
	 * @param extensibleUID UID to convert
	 * 
	 * @return String representation of UID
	 * 
	 * @throws RequiredInformationMissingException when required informations are not available
	 * @throws UIDStringBuilderException on errors during conversion
	 */
    public String buildUIDString( ExtensibleUID extensibleUID) throws RequiredInformationMissingException, UIDStringBuilderException;

    /**
     * Returns the newest (highest) UID version, that can be handled by the string builder.
     * 
     * @return newest UID version
     */
    public int getNewestHandledUIDVersion();
}
