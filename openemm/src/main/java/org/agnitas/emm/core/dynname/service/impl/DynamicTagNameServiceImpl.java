package org.agnitas.emm.core.dynname.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.DynamicTag;
import org.agnitas.dao.DynamicTagDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.dynname.service.DynamicTagNameService;
import org.agnitas.emm.core.dynname.service.NameModel;
import org.agnitas.emm.core.mailing.service.MailingNotExistException;
import org.agnitas.emm.core.validator.annotation.Validate;

public class DynamicTagNameServiceImpl implements DynamicTagNameService {

	@Resource(name="DynamicTagDao")
	private DynamicTagDao dynamicTagDao;
	@Resource(name="MailingDao")
	private MailingDao mailingDao;

	@Override
	@Validate("listContentBlocksOrNames")
	public List<DynamicTag> getNameList(NameModel nameModel) {
		if (!mailingDao.exist(nameModel.getMailingId(), nameModel.getCompanyId())) {
			throw new MailingNotExistException();
		}
		return dynamicTagDao.getNameList(nameModel.getCompanyId(), nameModel.getMailingId());
	}

}
