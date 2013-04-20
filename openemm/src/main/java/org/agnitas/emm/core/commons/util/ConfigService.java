package org.agnitas.emm.core.commons.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * ConfigurationService for EMM
 * This class uses buffering of the values of the config_tbl for better performance.
 * The value for refreshing period is also stored in config_tbl and can be changed
 * manually with no need for restarting the server.
 * For refreshing the values the very next time the old period value will be used.
 * Afterwards the new one will take effect.
 * The value 0 means, there will be no buffering.
 * 
 * @author aso
 *
 */
public class ConfigService {
	private static final transient Logger logger = Logger.getLogger(ConfigService.class);

	protected ConfigTableDao configTableDao;
		
	public static enum Value {
		DB_Vendor("DB_Vendor"),
		ConfigurationExpirationMinutes("configuration.expiration.minutes"),
		Test("Test.Test"),
		Password_Plaintext_Allowed("password.plaintext.allowed"),
		Pickup_Rdir("pickup.rdir"),
		
		System_Licence("system.licence"),
		System_Master("system.master"),
		System_Master_Hash("system.master-hash"),
		
		Linkchecker_Linktimeout("linkchecker.linktimeout"),
		Linkchecker_Threadcount("linkchecker.threadcount"),
		
		Predelivery_Litmusapikey("predelivery.litmusapikey"),
		Predelivery_Litmusapiurl("predelivery.litmusapiurl"),
		Predelivery_Litmuspassword("predelivery.litmuspassword"),
		
		Uid_Generation_Default_Version("uid.generation.default.version"),
		Uid_Deprecated_Redirection_Url("uid.deprecated.redirection.url"),

		Thumbnail_Generate("thumbnail.generate"),
		Thumbnail_Scalex("thumbnail.scalex"),
		Thumbnail_Scaley("thumbnail.scaley"),
		Thumbnail_Sizex("thumbnail.sizex"),
		Thumbnail_Sizey("thumbnail.sizey"),
		Thumbnail_Treshold("thumbnail.treshold");
		
		private Value(String name) {
			this.name = name;
		}
		private final String name;
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static enum CompanyConfigValue {
		COMPANY_PROPERTY_DEFAULT_LINK_EXTENSION("DefaultLinkExtension");
		
		private CompanyConfigValue(String name) {
			this.name = name;
		}
		private final String name;
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private static Map<String, String> configurationValues = null;
	private static Calendar expirationTime = null;
	
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	public void setConfigTableDao(ConfigTableDao configTableDao) {
		this.configTableDao = configTableDao;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

	private void refreshValues() {
		try {
			if (expirationTime == null || GregorianCalendar.getInstance().after(expirationTime)) {
				Map<String, String> newValues = configTableDao.getAllEntries();
				
				int minutes = 0;
				if (newValues.containsKey(Value.ConfigurationExpirationMinutes.toString()))
					minutes = Integer.parseInt(newValues.get(Value.ConfigurationExpirationMinutes.toString()));

				if (minutes > 0) {
					GregorianCalendar nextExpirationTime = new GregorianCalendar();
					nextExpirationTime.add(GregorianCalendar.MINUTE, minutes);
					expirationTime = nextExpirationTime;
				} else {
					expirationTime = null;
				}
				
				configurationValues = newValues;
			}
		} catch (Exception e) {
			logger.error("Cannot refresh config data from database", e);
		}
	}
	
	public String getValue(Value configurationValueID) {
		refreshValues();
		return configurationValues.get(configurationValueID.toString());
	}
	
	public String getValue(Value configurationValueID, int companyID) {
		refreshValues();
		return configurationValues.get(configurationValueID.toString() + "." + companyID);
	}
	
	public boolean getBooleanValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		return value != null 
				&& (
					"true".equalsIgnoreCase(value)
					|| "yes".equalsIgnoreCase(value)
					|| "allowed".equalsIgnoreCase(value)
					|| "1".equals(value)
					|| "+".equals(value)
				);
	}
	
	public boolean getBooleanValue(Value configurationValueID, int companyID) {
		String value = getValue(configurationValueID, companyID);
		return value != null 
				&& (
					"true".equalsIgnoreCase(value)
					|| "yes".equalsIgnoreCase(value)
					|| "allowed".equalsIgnoreCase(value)
					|| "1".equals(value)
					|| "+".equals(value)
				);
	}
	
	public int getIntegerValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		if (StringUtils.isNotEmpty(value))
			return Integer.parseInt(value);
		else
			return 0;
	}

	public float getFloatValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		if (StringUtils.isNotEmpty(value))
			return Float.parseFloat(value);
		else
			return 0;
	}
	
	public List<String> getListValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		if (StringUtils.isNotEmpty(value))
			return Arrays.asList(value.split(";"));
		else
			return Collections.emptyList();
	}
}
