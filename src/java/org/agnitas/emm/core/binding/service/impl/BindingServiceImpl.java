package org.agnitas.emm.core.binding.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.dao.BindingEntryDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.core.binding.service.BindingNotExistException;
import org.agnitas.emm.core.binding.service.BindingService;
import org.agnitas.emm.core.mailing.service.MailingNotExistException;
import org.agnitas.emm.core.mailinglist.service.MailinglistNotExistException;
import org.agnitas.emm.core.recipient.service.RecipientNotExistException;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

public class BindingServiceImpl implements BindingService, ApplicationContextAware {

	@Resource(name="BindingEntryDao")
	private BindingEntryDao bindingEntryDao;
	@Resource(name="MailinglistDao")
	private MailinglistDao mailinglistDao;
	@Resource(name="RecipientDao")
	private RecipientDao recipientDao;
	@Resource(name="MailingDao")
	protected MailingDao mailingDao;
	
	private ApplicationContext applicationContext;

	@Override
	@Transactional(readOnly = true)
	@Validate("getBinding")
	public BindingEntry getBinding(BindingModel model) {
		BindingEntry bindingEntry = bindingEntryDao.get(model.getCustomerId(), model.getCompanyId(), model.getMailinglistId(), model.getMediatype());
		if (bindingEntry == null) {
			throw new BindingNotExistException();
		}
		return bindingEntry;
	}

	@Override
	@Transactional
	@Validate("setBinding")
	public void setBinding(BindingModel model) {
		if (!mailinglistDao.exist(model.getMailinglistId(), model.getCompanyId())) {
			throw new MailinglistNotExistException();
		}
		if (!recipientDao.exist(model.getCustomerId(), model.getCompanyId())) {
			throw new RecipientNotExistException();
		}
		if (model.getExitMailingId() != 0 && !mailingDao.exist(model.getExitMailingId(), model.getCompanyId())) {
			throw new MailingNotExistException();
		}
		BindingEntry binding = bindingEntryDao.get(model.getCustomerId(), model.getCompanyId(), model.getMailinglistId(), model.getMediatype());
        if(binding == null) {
            binding = (BindingEntry) applicationContext.getBean("BindingEntry");
            binding.setCustomerID(model.getCustomerId()); 
            binding.setMailinglistID(model.getMailinglistId()); 
            binding.setMediaType(model.getMediatype()); 
        }
        binding.setUserStatus(model.getStatus());
        binding.setUserType(model.getUserType());
        binding.setExitMailingID(model.getExitMailingId());
        binding.setUserRemark(model.getRemark());
        bindingEntryDao.save(model.getCompanyId(), binding);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	@Transactional
	@Validate("getBinding")
	public void deleteBinding(BindingModel model) {
		if (!bindingEntryDao.exist(model.getCustomerId(), model.getCompanyId(), model.getMailinglistId(), model.getMediatype())) {
			throw new BindingNotExistException();
		}
		bindingEntryDao.delete(model.getCustomerId(), model.getCompanyId(), model.getMailinglistId(), model.getMediatype());
	}
	
	@Override
	@Transactional(readOnly = true)
	@Validate("listBinding")
	public List<BindingEntry> getBindings(BindingModel model) {
		if (!recipientDao.exist(model.getCustomerId(), model.getCompanyId())) {
			throw new RecipientNotExistException();
		}
		return bindingEntryDao.getBindings(model.getCompanyId(), model.getCustomerId());
	}


}
