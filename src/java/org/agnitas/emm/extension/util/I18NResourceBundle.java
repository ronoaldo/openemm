package org.agnitas.emm.extension.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class I18NResourceBundle {

	private final I18NFactory factory;
	private final Map<String, ResourceBundle> messages;
	
	
	public I18NResourceBundle( I18NFactory factory) {
		this.factory = factory;
		this.messages = new HashMap<String, ResourceBundle>();
	}

	public String getMessage( String key, Locale locale) {
		ResourceBundle languageBundle = getMessageBundle( locale.getLanguage() + "_" + locale.getCountry());		
		if( languageBundle != null && languageBundle.containsKey( key))
			return languageBundle.getString( key);
		
		languageBundle = getMessageBundle( locale.getLanguage());
		if( languageBundle != null && languageBundle.containsKey( key))
			return languageBundle.getString( key);

		languageBundle = getMessageBundle( "");
		if( languageBundle != null && languageBundle.containsKey( key))
			return languageBundle.getString( key);
		
		return null;
	}
	
	private ResourceBundle getMessageBundle( String i18nPrefix) {
		ResourceBundle bundle = this.messages.get( i18nPrefix);
		
		if( bundle == null) {
			bundle = factory.getMessages( i18nPrefix);
		
			this.messages.put( i18nPrefix, bundle);
		}
		
		return bundle;
	}
}
