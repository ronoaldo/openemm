package org.agnitas.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.beans.AgnDBTagError;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.ProfileField;
import org.agnitas.beans.impl.AgnDBTagErrorImpl;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.service.MailingContentService;
import org.agnitas.util.CaseInsensitiveMap;
/**
 * 
 * @author ms
 * @deprecated Validating tags is handled by TAGCheck now, which wraps the back-end engine
 */
public class MailingContentServiceImpl implements MailingContentService {
	
	private ColumnInfoService columnInfoService;
	
	public List<AgnDBTagError> getInvalidAgnDBTags(String content, int companyID) throws Exception {
		
		List<AgnDBTagError> invalidTags = new ArrayList<AgnDBTagError>();
		
		List<String> agnDBTags = scanForAgnDBTags(content);
		List<String> columnNames = extractColumnNames(agnDBTags);
		
		CaseInsensitiveMap<ProfileField> columnInfoMap = columnInfoService.getColumnInfoMap(companyID);
		
		int i=0;
		for(String columnName:columnNames) {
			if( AGNDBTAG_WRONG_FORMAT.equals(columnName)) {
				invalidTags.add(new AgnDBTagErrorImpl(agnDBTags.get(i) , AGNDBTAG_WRONG_FORMAT));
			}
			else {
				if (!columnInfoMap.containsKey(columnName)) {
					invalidTags.add(new AgnDBTagErrorImpl(agnDBTags.get(i),AGNDBTAG_UNKNOWN_COLUMN));
				}
			}
			i++;
		}	
		
		return invalidTags;
	}
	
	
	public List<String> scanForAgnDBTags(String content) {
		String agnDBTagPattern = ".*?(\\[agnDB column=\"(.*?)\"\\])";
		List<String> agnDBTags = new ArrayList<String>();
		Pattern pattern = Pattern.compile(agnDBTagPattern);
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			agnDBTags.add(matcher.group(1));
		}		
		return agnDBTags;
	}
	
	
	public List<String> extractColumnNames(List<String> agnDBTags ) {
		List<String> agnDBTagColumnNames = new ArrayList<String>();
		Pattern classNamePattern = Pattern.compile("column=\"(.*?)\""); 
		
		for(String agnDBTag:agnDBTags) {
			
			if( !agnDBTag.matches("\\[agnDB column=\"(.*?)\"\\]")){
				agnDBTagColumnNames.add(MailingContentService.AGNDBTAG_WRONG_FORMAT);
				continue;
			} else {
				Matcher matcher = classNamePattern.matcher(agnDBTag);
				if( matcher.find()) {
					agnDBTagColumnNames.add(matcher.group(1));
				}				
			}			
		}
		return agnDBTagColumnNames;
	}


	public Map<String, List<AgnDBTagError>> getAgnDBTagErrors( Map<String, DynamicTagContent> contentMap, int companyID ) throws Exception {
	
    	Map<String,List<AgnDBTagError>> agnDBTagErrors =  new HashMap<String, List<AgnDBTagError>>();
    	
    	for(String contentKey:contentMap.keySet()) {
    			DynamicTagContent tagContent = contentMap.get(contentKey);
    			String content = tagContent.getDynContent();
    			List<AgnDBTagError> tagErrors = getInvalidAgnDBTags(content, companyID);
        		if( tagErrors.size() > 0) {
        			agnDBTagErrors.put(contentKey,tagErrors);
        		}
    		}    		    		
    	return agnDBTagErrors;
	}

	public void setColumnInfoService(ColumnInfoService columnInfoService) {
		this.columnInfoService = columnInfoService;
	}


	
}
