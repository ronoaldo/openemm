package org.agnitas.emm.core.commons.uid.parser;

import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.parser.exception.DeprecatedUIDVersionException;
import org.agnitas.emm.core.commons.uid.parser.exception.InvalidUIDException;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;

/**
 * Interface for parsing an UID string representation to a ExtensibleUID instance.
 * 
 * @author md
 */
public interface ExtensibleUIDParser {
	/**
	 * Parses a given UID string representation. Validation is performed after successful
	 * parsing. For errors on the UID representation an InvalidUIDException is throws. This
	 * includes an invalid format and validation errors.
	 * For errors concering the parse process itself, the UIDParseException is thrown.
	 *
	 * 
	 * @param uidString UID string representation
	 * 
	 * @return ExtensibleUID instance
	 * 
	 * @throws UIDParseException on errors during parsing
	 * @throws InvalidUIDException on errors during validation.
	 */
    public ExtensibleUID parse( String uidString) throws UIDParseException, InvalidUIDException, DeprecatedUIDVersionException;
}
