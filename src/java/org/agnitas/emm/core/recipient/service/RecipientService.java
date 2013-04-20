package org.agnitas.emm.core.recipient.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.springframework.context.ApplicationContext;

public interface RecipientService {

	public int findSubscriber(int companyId, String keyColumn, String value);
	
	public int addSubscriber(RecipientModel model);
	
	public Map<String, Object> getSubscriber(int companyId, int customerId);
	
	public void deleteSubscribers(int companyId, List<Integer> list);

	public boolean updateSubscriber(int companyId, int customerID, CaseInsensitiveMap caseInsensitiveMap);

    public void setApplicationContext(ApplicationContext applicationContext);
}
