package org.agnitas.emm.core.commons.uid.impl;

import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.emm.core.commons.uid.builder.ExtensibleUIDStringBuilder;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.RequiredInformationMissingException;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.UIDStringBuilderException;
import org.agnitas.emm.core.commons.uid.parser.ExtensibleUIDParser;
import org.agnitas.emm.core.commons.uid.parser.exception.DeprecatedUIDVersionException;
import org.agnitas.emm.core.commons.uid.parser.exception.InvalidUIDException;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;

/**
 * Facade. Implementation of ExtensibleUIDService.
 *  
 * @author md
 */
public class ExtensibleUIDServiceImpl implements ExtensibleUIDService {

	// ------------------------------------------------------ Dependency Injection
	/** Parser for UIDs. */
	private ExtensibleUIDParser parser;
	
	/** String builder for UIDs. */
	private ExtensibleUIDStringBuilder stringBuilder;

	/**
	 * Sets the ExtensibleUIDParser.
	 * 
	 * @param parser ExtensibleUIDParser
	 */
	public void setParser( ExtensibleUIDParser parser) {
		this.parser = parser;
	}

	/**
	 * Sets the ExtensibleUIDStringBuilder.
	 * 
	 * @param stringBuilder ExtensibleUIDStringBuilder
	 */
	public void setStringBuilder( ExtensibleUIDStringBuilder stringBuilder) {
		this.stringBuilder = stringBuilder;
	}

	// ------------------------------------------------------ Business Logic

	@Override
	public ExtensibleUID newUID() {
		return new ExtensibleUIDImpl();
	}

	@Override
	public String buildUIDString( ExtensibleUID extensibleUID) throws UIDStringBuilderException, RequiredInformationMissingException {
		return this.stringBuilder.buildUIDString( extensibleUID);
	}

	@Override
	public ExtensibleUID parse( String uidString) throws UIDParseException, InvalidUIDException, DeprecatedUIDVersionException {
		return this.parser.parse( uidString);
	}

}
