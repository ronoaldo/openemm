package org.agnitas.emm.core.mailinglist.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.MailinglistImpl;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.emm.core.mailinglist.service.MailinglistNotEmptyException;
import org.agnitas.emm.core.mailinglist.service.MailinglistNotExistException;
import org.agnitas.emm.core.mailinglist.service.MailinglistService;
import org.agnitas.emm.core.mailinglist.service.MailinglistModel;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.springframework.transaction.annotation.Transactional;

public class MailinglistServiceImpl implements MailinglistService {
	
	@Resource(name="MailinglistDao")
	private MailinglistDao mailinglistDao;
	@Resource(name="MailingDao")
	private MailingDao mailingDao;

	@Override
	@Transactional
	@Validate("addMailinglist")
	public int addMailinglist(MailinglistModel model) {
        Mailinglist mailinglist = new MailinglistImpl();
        mailinglist.setCompanyID(model.getCompanyId());
        mailinglist.setShortname(model.getShortname());
        mailinglist.setDescription(model.getDescription());
		return mailinglistDao.saveMailinglist(mailinglist);
	}

	@Override
	@Transactional
	@Validate("updateMailinglist")
    public void updateMailinglist(MailinglistModel model) {
		Mailinglist mailinglist = getMailinglist(model);
		mailinglist.setShortname(model.getShortname());
		mailinglist.setDescription(model.getDescription());
		mailinglistDao.saveMailinglist(mailinglist);
    }

    @Override
    @Transactional(readOnly=true)
    @Validate("getMailinglist")
    public Mailinglist getMailinglist(MailinglistModel model) {
        Mailinglist mailingList = mailinglistDao.getMailinglist(model.getMailinglistId(), model.getCompanyId());
        if (mailingList == null) {
        	throw new MailinglistNotExistException();
        }
        return mailingList;
    }

	@Override
	@Transactional
	@Validate("getMailinglist")
	public boolean deleteMailinglist(MailinglistModel model) {
		if (mailingDao.getMailingsForMLID(model.getCompanyId(), model.getMailinglistId()).size() > 0) {
			throw new MailinglistNotEmptyException();
		}
		if (!mailinglistDao.exist(model.getMailinglistId(), model.getCompanyId())) {
			throw new MailinglistNotExistException();
		}
        boolean deleteMailinglist = mailinglistDao.deleteMailinglist(model.getMailinglistId(), model.getCompanyId());
        boolean deleteBindings = false;
        if (deleteMailinglist) {
            deleteBindings = mailinglistDao.deleteBindings(model.getMailinglistId(), model.getCompanyId());
        }
		return deleteBindings && deleteMailinglist;
	}

	@Override
	@Transactional(readOnly=true)
	@Validate("company")
	public List<Mailinglist> getMailinglists(MailinglistModel model) {
		return mailinglistDao.getMailinglists(model.getCompanyId());
	}
}
