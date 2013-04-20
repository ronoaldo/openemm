package org.agnitas.emm.core.recipient.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.agnitas.beans.Recipient;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.core.recipient.service.RecipientService;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

public class RecipientServiceImpl implements RecipientService, ApplicationContextAware {
	
	private static Logger logger = Logger.getLogger(RecipientServiceImpl.class);

	@Resource(name="RecipientDao")
	private RecipientDao recipientDao;

	private ApplicationContext applicationContext;
	
	@Override
	@Transactional
	public int findSubscriber(int companyId, String keyColumn, String value) {
		try {
			return recipientDao.findByColumn(companyId, keyColumn, value);
		} catch (RuntimeException e) {
			logger.error("Exception", e);
			throw e;
		}
	}

	@Override
	@Transactional
	@Validate("addSubscriber")
	public int addSubscriber(RecipientModel model) {
		int returnValue = 0;
		int tmpCustID = 0;
		model.setEmail(model.getEmail().toLowerCase());
		Recipient aCust = (Recipient) applicationContext.getBean("Recipient");
        aCust.setCompanyID(model.getCompanyId());
        aCust.setCustParameters(model.getParameters());
        if(model.isDoubleCheck()) {
            tmpCustID = recipientDao.findByColumn(aCust.getCompanyID(), model.getKeyColumn().toLowerCase(), (String)model.getParameters().get(model.getKeyColumn()));
            if(tmpCustID == 0) {
                returnValue = recipientDao.insertNewCust(aCust);
            } else {
                returnValue = tmpCustID;
                if(model.isOverwrite()) {
                    aCust.setCustomerID(tmpCustID);
                    recipientDao.updateInDB(aCust);
                }
            }
        } else {
            returnValue = recipientDao.insertNewCust(aCust);
        }
		return returnValue;
	}

	@Override
	@Transactional
	public boolean updateSubscriber(int companyId, int customerID,
			CaseInsensitiveMap custParameters) {
		String email = (String) custParameters.get("email");
		if (email != null) {
			custParameters.put("email", email.toLowerCase());
		}
		Recipient aCust = (Recipient) applicationContext.getBean("Recipient");
        aCust.setCompanyID(companyId);
        aCust.loadCustDBStructure();
        aCust.setCustParameters(recipientDao.getCustomerDataFromDb(aCust.getCompanyID(), customerID));
        aCust.setCustomerID(customerID);
       	for (Object key : custParameters.keySet()) {
       		aCust.setCustParameters((String)key, (String)custParameters.get(key));
		}
   		return recipientDao.updateInDB(aCust);
	}

	@Override
	@Transactional
	public Map<String, Object> getSubscriber(int companyId, int customerId) {
		return recipientDao.getCustomerDataFromDb(companyId, customerId);
	}

	@Override
	public void deleteSubscribers(int companyId, List<Integer> list) {
		recipientDao.deleteRecipients(companyId, list);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
