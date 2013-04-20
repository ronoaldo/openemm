package org.agnitas.emm.core.commons.uid;

import org.agnitas.emm.core.commons.uid.builder.impl.exception.RequiredInformationMissingException;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.UIDStringBuilderException;
import org.agnitas.emm.core.commons.uid.parser.exception.DeprecatedUIDVersionException;
import org.agnitas.emm.core.commons.uid.parser.exception.InvalidUIDException;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;

/**
 * Facade interface. Provides a combined interface for methods dealing with UIDs. 
 * 
 * @author md
 */
public interface ExtensibleUIDService {
	
	/**
	 * Converts the UID to its string representation.
	 * 
	 * @param extensibleUID UID
	 * 
	 * @return string representation of the UID
	 * 
	 * @throws UIDStringBuilderException on errors during conversion
	 */
    public String buildUIDString( ExtensibleUID extensibleUID) throws UIDStringBuilderException, RequiredInformationMissingException;
    
    /**
     * Parses the string representation of a UID.
     * 
     * @param uidString string representation
     * 
     * @return parsed ExtensibleUID instance
     * 
     * @throws UIDParseException on errors during parsing
     * @throws InvalidUIDException on errors indicating an invalid UID
     */
    public ExtensibleUID parse( String uidString) throws UIDParseException, InvalidUIDException, DeprecatedUIDVersionException;
    
    /**
     * Creates a new and empty ExtensibleUID instance.
     * 
     * @return ExtensibleUID instance
     */
    public ExtensibleUID newUID();
}
