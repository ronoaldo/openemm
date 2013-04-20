package org.agnitas.beans.factory.impl;

import org.agnitas.beans.ProfileField;
import org.agnitas.beans.factory.ProfileFieldFactory;
import org.agnitas.beans.impl.ProfileFieldImpl;

public class ProfileFieldFactoryImpl implements ProfileFieldFactory {

	@Override
	public ProfileField newProfileField() {
		return new ProfileFieldImpl();
	}

}
