package org.agnitas.emm.core.commons.util;

import static org.agnitas.emm.core.commons.util.Constants.DATE_PATTERN_FULL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.agnitas.util.SafeString;

/**
 * Use this class to handle standard formats 
 * 
 *
 */
public class DateUtil {

	
	/**
	 * @param date
	 * @return the date formatted with the Constants.DATE_PATTERN_FULL  
	 */
	public static String formatDateFull(Date date ) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_FULL);
		return format.format(date);
	}
	
	/**
	 * @param dateAsString - date which matches the Constants.DATE_PATTERN_FULL
	 * @return 
	 * @throws ParseException
	 */
	
	public static Date parseFullDate(String dateAsString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_FULL);
		Date date;
		date = format.parse(dateAsString);
		return date;
	}

	public static String getTimespanString(long timespanInMillis, Locale locale) {
		int days = (int) (timespanInMillis / Constants.MILLISECONDS_PER_DAY);
		int leftover = (int) (timespanInMillis % Constants.MILLISECONDS_PER_DAY);
		int hours = (int) (leftover / Constants.MILLISECONDS_PER_HOUR);
		leftover = (int) (leftover % Constants.MILLISECONDS_PER_HOUR);
		int minutes = (int) (leftover / Constants.MILLISECONDS_PER_MINUTE);
		leftover = (int) (leftover % Constants.MILLISECONDS_PER_MINUTE);
		int seconds = (int) (leftover / 1000);		
		
		if (days > 0) {
			if (hours == 0 && minutes == 0 && seconds == 0) {
				return days + " " + SafeString.getLocaleString("days", locale);
			} else {
				return days + " " + SafeString.getLocaleString("days", locale) + " " + hours + " " + SafeString.getLocaleString("hours", locale);
			}
		} else if (hours > 0) {
			if (minutes == 0 && seconds == 0) {
				return hours + " " + SafeString.getLocaleString("hours", locale) + " " + minutes + " " + SafeString.getLocaleString("minutes", locale);
			} else {
				return hours + " " + SafeString.getLocaleString("hours", locale) + " " + minutes + " " + SafeString.getLocaleString("minutes", locale);
			}
		} else if (minutes > 0) {
			return minutes + " " + SafeString.getLocaleString("minutes", locale) + " " + seconds + " " + SafeString.getLocaleString("seconds", locale);
		} else {
			return seconds + " " + SafeString.getLocaleString("seconds", locale);
		}
	}
	
    /**
     * Checks, if the send date is good for immediate delivery. For immediate mailing
     * delivery, the send date can be up to five minutes in the future.
     *
     * @param sendDate date to check.
     * 
     * @return true if send date is good for immediate delivery
     */
    public static boolean isSendDateForImmediateDelivery( Date sendDate) {
    	// Create the calendar object for comparison
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar sendDateCalendar = new GregorianCalendar();

        // Set the time of the test-calendar
        sendDateCalendar.setTime( sendDate);

        // Move "current time" 5 minutes into future, so we get a 5 minute fairness period
        now.add( Calendar.MINUTE, 5);
        
        // Do the hard work!
        return !now.before( sendDateCalendar);
    }
    
    public static boolean isValidSendDate( Date sendDate) {
    	// Create the calendar object for comparison
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar sendDateCalendar = new GregorianCalendar();

        // Set the time of the test-calendar
        sendDateCalendar.setTime( sendDate);

        // Move "current time" 5 minutes into future, so we get a 5 minute fairness period
        now.add( Calendar.MINUTE, -5);
        
        // Do the hard work!
        return now.before( sendDateCalendar);	
    }

    /**
     * Checks, if the send date is good for immediate generation. For immediate mailing
     * generation, the generation date can be up to five minutes in the future.
     *
     * @param generationDate date to check.
     * 
     * @return true if date is good for immediate generation
     */
    public static boolean isDateForImmediateGeneration( Date generationDate) {
    	// Create the calendar object for comparison
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar generationDateCalendar = new GregorianCalendar();

        // Set the time of the test-calendar
        generationDateCalendar.setTime( generationDate);

        // Move "current time" 5 minutes into future, so we get a 5 minute fairness period
        now.add( Calendar.MINUTE, 5);
        
        // Do the hard work!
        return !now.before( generationDateCalendar);
    }
}
