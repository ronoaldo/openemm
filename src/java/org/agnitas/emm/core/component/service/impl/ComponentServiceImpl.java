package org.agnitas.emm.core.component.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.component.service.ComponentModel;
import org.agnitas.emm.core.component.service.ComponentNotExistException;
import org.agnitas.emm.core.component.service.ComponentService;
import org.agnitas.emm.core.mailing.service.MailingNotExistException;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

public class ComponentServiceImpl implements ComponentService, ApplicationContextAware {

	@Resource(name="MailingComponentDao")
	private MailingComponentDao mailingComponentDao;
	@Resource(name="MailingDao")
	private MailingDao mailingDao;
	
	private ApplicationContext applicationContext;
	
	@Override
	@Transactional(readOnly = true)
	@Validate("getComponents")
	public List<MailingComponent> getComponents(ComponentModel model) {
		if (!mailingDao.exist(model.getMailingId(), model.getCompanyId())) {
			throw new MailingNotExistException();
		}
		return mailingComponentDao.getMailingComponents(model.getMailingId(), model.getCompanyId(), model.getComponentType());
	}

	@Override
	@Transactional(readOnly = true)
	@Validate("getComponent")
	public MailingComponent getComponent(ComponentModel model) {
		MailingComponent component = mailingComponentDao.getMailingComponent(model.getComponentId(), model.getCompanyId());
		if (component == null || component.getType() != model.getComponentType()) {
			throw new ComponentNotExistException();
		}
		return component;
	}

	@Override
	@Transactional
	@Validate("addComponent")
	public int addComponent(ComponentModel model) {
		MailingComponent component = (MailingComponent) applicationContext.getBean("MailingComponent");
		if (!mailingDao.exist(model.getMailingId(), model.getCompanyId())) {
			throw new MailingNotExistException();
		}
		component.setCompanyID(model.getCompanyId());
		component.setMailingID(model.getMailingId());
		component.setMimeType(model.getMimeType());
		component.setType(model.getComponentType());
		component.setComponentName(model.getComponentName());
		component.setBinaryBlock(model.getData());
		component.setEmmBlock(component.makeEMMBlock());
		
		mailingComponentDao.saveMailingComponent(component);
		return component.getId();
	}

	@Override
	@Transactional
	@Validate("updateComponent")
	public void updateComponent(ComponentModel model) {
		MailingComponent component = mailingComponentDao.getMailingComponent(model.getComponentId(), model.getCompanyId());
		if (component == null || component.getType() != model.getComponentType()) {
			throw new ComponentNotExistException();
		}
		component.setMimeType(model.getMimeType());
		component.setComponentName(model.getComponentName());
		if (model.getData() != null) {
			component.setBinaryBlock(model.getData());
			component.setEmmBlock(component.makeEMMBlock());
		}
		
		mailingComponentDao.saveMailingComponent(component);
	}

	@Override
	@Transactional
	@Validate("getComponent")
	public void deleteComponent(ComponentModel model) {
		MailingComponent component = mailingComponentDao.getMailingComponent(model.getComponentId(), model.getCompanyId());
		if (component == null || component.getType() != model.getComponentType()) {
			throw new ComponentNotExistException();
		}
		mailingComponentDao.deleteMailingComponent(component);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
