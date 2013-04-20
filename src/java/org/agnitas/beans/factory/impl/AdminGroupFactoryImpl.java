package org.agnitas.beans.factory.impl;

import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.factory.AdminGroupFactory;
import org.agnitas.beans.impl.AdminGroupImpl;

public class AdminGroupFactoryImpl implements AdminGroupFactory {

	@Override
	public AdminGroup newAdminGroup() {
		return new AdminGroupImpl();
	}

}
