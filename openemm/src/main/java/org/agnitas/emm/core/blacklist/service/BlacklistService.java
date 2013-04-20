package org.agnitas.emm.core.blacklist.service;

import org.agnitas.beans.Mailinglist;
import org.agnitas.dao.BlacklistDao;

import java.util.List;

public interface BlacklistService {

	boolean	insertBlacklist(BlacklistModel model);

	boolean deleteBlacklist(BlacklistModel model);

	boolean checkBlacklist(BlacklistModel model);
	
	List<String> getEmailList(int companyID) throws Exception;

    public void setBlacklistDao(BlacklistDao blacklistDao);
	
    public List<Mailinglist> getMailinglistsWithBlacklistedBindings( BlacklistModel model);

	public void updateBlacklistedBindings(BlacklistModel bm, List<Integer> mailinglists, int userStatus);
}
