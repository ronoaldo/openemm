package org.agnitas.dao;

import java.util.List;

import org.agnitas.beans.DynamicTagContent;

public interface DynamicTagContentDao {

    /**
     * Deletes tag content for the given company from the database.
     *
     * @param companyID
     *            The id of the company for content.
     * @param contentID
     *            The id of the content to delete.
	 * @return True: Sucess
	 * False: Failure
     */

    boolean deleteContent(int companyID, int contentID);

    /**
	 * Loads tag content identified by content id company id.
	 *
	 * @param contentID
	 *            The id of the content that should be loaded.
	 * @param companyID
	 *            The companyID for the content.
	 * @return The DynamicTagContent or null on failure.
	 */
    DynamicTagContent getContent(int companyID, int contentID);

    /**
     * Loads all tag contents for given mailing and company.
     *
     * @param companyID
     *            The companyID for the content.
     * @param mailingID
     *            The mailingID for the content.
     * @return  List of DynamicTagContent or empty list.
     */
	List<DynamicTagContent> getContentList(int companyID, int mailingID);

	/**
	 * Saves tag content.
	 *
	 * @param dynamicTagContent
	 *            The DynamicTagContent that should be saved.
	 */
	void updateContent(DynamicTagContent dynamicTagContent);
    
}
