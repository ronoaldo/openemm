package org.agnitas.emm.core.dyncontent.service;

import java.util.List;

import org.agnitas.beans.DynamicTagContent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;


public interface DynamicTagContentService {

	boolean deleteContent(ContentModel contentModel);

	DynamicTagContent getContent(ContentModel contentModel);

	List<DynamicTagContent> getContentList(ContentModel contentModel);

	int addContent(ContentModel contentModel);
	
	void updateContent(ContentModel contentModel);

    DynamicTagContent getContent(int companyID, int contentID);

    void setApplicationContext(ApplicationContext applicationContext)throws BeansException;
}
