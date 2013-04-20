package org.agnitas.emm.core.commons.util;

import java.text.SimpleDateFormat;

public interface Constants {

	public static final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String SEPARATOR = System.getProperty("file.separator");

	
	public static SimpleDateFormat DATETIMEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	public static SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm");

	public static int MILLISECONDS_PER_MINUTE = 60 * 1000;
	public static int MILLISECONDS_PER_HOUR = 60 * MILLISECONDS_PER_MINUTE;
	public static int MILLISECONDS_PER_DAY = 24 * MILLISECONDS_PER_HOUR;
}
