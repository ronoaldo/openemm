package org.agnitas.service;

import java.util.List;
import java.util.Map;

import org.agnitas.beans.AgnDBTagError;
import org.agnitas.beans.DynamicTagContent;

public interface MailingContentService {
	public final static String AGNDBTAG_WRONG_FORMAT = "AGNDBTAG_WRONG_FORMAT";
	public final static String AGNDBTAG_UNKNOWN_COLUMN = "AGNDBTAG_UNKNOWN_COLUMN"; 
	public List<AgnDBTagError> getInvalidAgnDBTags(String content,int companyID) throws Exception;
	public List<String> scanForAgnDBTags(String content);
	public Map<String, List<AgnDBTagError>> getAgnDBTagErrors( Map<String, DynamicTagContent> tagMap, int companyID ) throws Exception; 
}
