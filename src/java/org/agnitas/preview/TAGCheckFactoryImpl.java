package org.agnitas.preview;

public class TAGCheckFactoryImpl implements TAGCheckFactory {

	public TAGCheck createTAGCheck(long mailingID) throws Exception {
		return new TAGCheckImpl(mailingID);
	}

}
