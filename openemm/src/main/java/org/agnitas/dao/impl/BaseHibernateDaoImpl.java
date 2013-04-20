package org.agnitas.dao.impl;

import org.hibernate.SessionFactory;

public abstract class BaseHibernateDaoImpl extends BaseDaoImpl {
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
