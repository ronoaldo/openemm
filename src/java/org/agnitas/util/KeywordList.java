package org.agnitas.util;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class KeywordList {
	private final Set<String> keyWords = new TreeSet<String>();
	
	public void setKeywords( Collection<String> set) {
		this.keyWords.clear();
		
		for( String keyWord : set) 
			this.keyWords.add( keyWord.toLowerCase());
	}
	
	public boolean containsKeyWord( String keyWord) {
		return keyWords.contains( keyWord.toLowerCase());
	}
}
