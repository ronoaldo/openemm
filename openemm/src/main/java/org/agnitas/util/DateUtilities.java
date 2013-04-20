package org.agnitas.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DateUtilities {
	public static final SimpleDateFormat DD_MM_YYYY_HH_MM_SS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	public static final SimpleDateFormat DD_MM_YYYY_HH = new SimpleDateFormat("dd.MM.yyyy HH");
	public static final SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");
	public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_MS = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss,SSS");
	public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_FORFILENAMES = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	public static final SimpleDateFormat YYYYMMDD = new java.text.SimpleDateFormat("yyyyMMdd");
	
	public enum TimespanID {
		previous_week,
		previous_7_days,
		previous_month,
		current_year,
		previous_year;
		
		public static TimespanID fromString(String value) {
			if (value != null) {
				for (TimespanID item : TimespanID.values()) {
					if (item.toString().replace("_", "").equalsIgnoreCase(value.replace("_", ""))) {
						return item;
					}
				}
			}
			throw new IllegalArgumentException("Invalid TimespanID");
		}
	}
	
	public static Tuple<Date, Date> getTimespan(String timespanId) {
		return getTimespan(TimespanID.fromString(timespanId));
	}

	public static Tuple<Date, Date> getTimespan(TimespanID timespanId) {
		return getTimespan(timespanId, null);
	}
	
	public static Tuple<Date, Date> getTimespan(TimespanID timespanId, Date calculationBase) {
		Date now = calculationBase;
		if (now == null) {
			now = new Date();
		}
		
		Calendar today = new GregorianCalendar();
		today.setTime(now);
		today = removeTime(today);
		
		Date start;
		Date end;
		if (TimespanID.previous_week == timespanId) {
			Calendar previousWeekStart = (GregorianCalendar) today.clone();
			previousWeekStart.add(Calendar.DAY_OF_MONTH, -7);
			previousWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			start = previousWeekStart.getTime();
			Calendar previousWeekEnd = (GregorianCalendar) today.clone();
			previousWeekEnd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			previousWeekEnd.add(Calendar.MILLISECOND, -1);
			end = previousWeekEnd.getTime();
		} else if (TimespanID.previous_7_days == timespanId) {
			Calendar sevenDaysAgo = (GregorianCalendar) today.clone();
			sevenDaysAgo.add(Calendar.DAY_OF_MONTH, -7);
			start = sevenDaysAgo.getTime();
			Calendar yesterdayEnd = (GregorianCalendar) today.clone();
			yesterdayEnd.add(Calendar.MILLISECOND, -1);
			end = yesterdayEnd.getTime();
		} else if (TimespanID.previous_month == timespanId) {
			Calendar oneMonthAgo = (GregorianCalendar) today.clone();
			oneMonthAgo.set(Calendar.DAY_OF_MONTH, 1);
			oneMonthAgo.add(Calendar.MONTH, -1);
			start = oneMonthAgo.getTime();
			Calendar monthEnd = (GregorianCalendar) today.clone();
			monthEnd.set(Calendar.DAY_OF_MONTH, 1);
			monthEnd.add(Calendar.MILLISECOND, -1);
			end = monthEnd.getTime();
		} else if (TimespanID.current_year == timespanId) {
			Calendar yearStart = (GregorianCalendar) today.clone();
			yearStart.set(Calendar.DAY_OF_MONTH, 1);
			yearStart.set(Calendar.MONTH, Calendar.JANUARY);
			start = yearStart.getTime();
			Calendar oneMillisecondAgo = (GregorianCalendar) today.clone();
			oneMillisecondAgo.add(Calendar.MILLISECOND, -1);
			end = oneMillisecondAgo.getTime();
		} else if (TimespanID.previous_year == timespanId) {
			Calendar previousYearStart = (GregorianCalendar) today.clone();
			previousYearStart.set(Calendar.DAY_OF_MONTH, 1);
			previousYearStart.set(Calendar.MONTH, Calendar.JANUARY);
			previousYearStart.add(Calendar.YEAR, -1);
			start = previousYearStart.getTime();
			Calendar previousYearEnd = (GregorianCalendar) today.clone();
			previousYearEnd.set(Calendar.DAY_OF_MONTH, 1);
			previousYearEnd.set(Calendar.MONTH, Calendar.JANUARY);
			previousYearEnd.add(Calendar.MILLISECOND, -1);
			end = previousYearEnd.getTime();
		} else  {
			throw new IllegalArgumentException("TimespanID is invalid");
		}
		
		return new Tuple<Date, Date>(start, end);
	}

	public static Calendar getTodayWithoutTime() {
		return removeTime(new GregorianCalendar());
	}
	
	public static Calendar removeTime(Calendar calendar) {
		Calendar returnCalendar = (Calendar) calendar.clone();
		returnCalendar.set(Calendar.HOUR_OF_DAY, 0);
		returnCalendar.set(Calendar.MINUTE, 0);
		returnCalendar.set(Calendar.SECOND, 0);
		returnCalendar.set(Calendar.MILLISECOND, 0);
		return returnCalendar;
	}
	
	/**
	 * Calculation of next scheduled job start
	 * Timingparameter may contain weekdays, clocktimes, months, quarters and holidays
	 * 
	 * Allowed parameters:
	 * "ONCE"                      => only once (returns null)
	 * "0600;0800"                 => daily at 06:00 and 08:00
	 * "MoMi:1700"                 => Every monday and wednesday at 17:00
	 * "M05:1600"                  => every 05th day of month at 16:00
	 * "Q:1600"                    => every first day of quarter at 16:00
	 * "QW:1600"                   => every first working day of quarter at 16:00
	 * "MoDiMiDoFr:1700;!23012011" => mondyas to fridays at 17:00 exept for 23.01.2011 (Holidays marked by '!')
	 * 
	 * All values may be combined separated by semicolons.
	 * 
	 * @param timingString
	 * @return
	 * @throws Exception
	 */
	public static Date calculateNextJobStart(String timingString) {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar returnStart = new GregorianCalendar();
		returnStart.add(GregorianCalendar.YEAR, 1);
		
		// Holidays to exclude
		List<GregorianCalendar> excludedDays = new ArrayList<GregorianCalendar>();
		
		if (timingString.equalsIgnoreCase("once"))
			return null;

		String[] timingParameterList = timingString.split(";");
		for (String timingParameter : timingParameterList) {
			if (timingParameter.startsWith("!")) {
				try {
					GregorianCalendar exclusionDate = new GregorianCalendar();
					exclusionDate.setTime(new SimpleDateFormat("ddMMyyyy").parse(timingParameter.substring(1)));
					excludedDays.add(exclusionDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (String timingParameter : timingParameterList) {
			GregorianCalendar nextStartByThisParameter = new GregorianCalendar();
			nextStartByThisParameter.setTime(now.getTime());

			if (timingParameter.startsWith("!")) {
				// Exclusions are done previously
				continue;
			}
			else if (!timingParameter.contains(":")) {
				if (AgnUtils.isDigit(timingParameter)) {
					// daily execusion on given time
					nextStartByThisParameter.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(timingParameter.substring(0, 2)));
					nextStartByThisParameter.set(GregorianCalendar.MINUTE, Integer.parseInt(timingParameter.substring(2)));
					nextStartByThisParameter.set(GregorianCalendar.SECOND, 0);
					nextStartByThisParameter.set(GregorianCalendar.MILLISECOND, 0);
	
					// Move next start into future (+1 day) until rule is matched
					// Move also when meeting holiday rule
					while (nextStartByThisParameter.before(now) && nextStartByThisParameter.before(returnStart)
							|| AgnUtils.dayListIncludes(excludedDays, nextStartByThisParameter))
						nextStartByThisParameter.add(GregorianCalendar.DAY_OF_MONTH, 1);
				} else {
					// weekly execution at 00:00 Uhr
					List<Integer> weekdayIndexes = new ArrayList<Integer>();
					for (String weekDay : AgnUtils.chopToChunks(timingParameter, 2)) {
						weekdayIndexes.add(getWeekdayIndex(weekDay));
					}
					nextStartByThisParameter.set(GregorianCalendar.HOUR_OF_DAY,0);
					nextStartByThisParameter.set(GregorianCalendar.MINUTE, 0);
					nextStartByThisParameter.set(GregorianCalendar.SECOND, 0);
					nextStartByThisParameter.set(GregorianCalendar.MILLISECOND, 0);

					// Move next start into future (+1 day) until rule is matched
					// Move also when meeting holiday rule
					while ((nextStartByThisParameter.before(now)
							|| !weekdayIndexes.contains(nextStartByThisParameter.get(Calendar.DAY_OF_WEEK))) && nextStartByThisParameter.before(returnStart)
							|| AgnUtils.dayListIncludes(excludedDays, nextStartByThisParameter))
						nextStartByThisParameter.add(GregorianCalendar.DAY_OF_MONTH, 1);
				}
			}
			else if (timingParameter.length() == 8) {
				// month rule "M01:1700"
				String tag = timingParameter.substring(1, timingParameter.indexOf(":"));
				String zeit = timingParameter.substring(timingParameter.indexOf(":") + 1);
				nextStartByThisParameter.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(tag));
				nextStartByThisParameter.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(zeit.substring(0, 2)));
				nextStartByThisParameter.set(GregorianCalendar.MINUTE, Integer.parseInt(zeit.substring(2)));
				nextStartByThisParameter.set(GregorianCalendar.SECOND, 0);
				nextStartByThisParameter.set(GregorianCalendar.MILLISECOND, 0);
				
				// find next matching month
				while (nextStartByThisParameter.before(now) && nextStartByThisParameter.before(returnStart))
					nextStartByThisParameter.add(GregorianCalendar.MONTH, 1);
				
				// Move also when meeting holiday rule
				while (AgnUtils.dayListIncludes(excludedDays, nextStartByThisParameter))
					nextStartByThisParameter.add(GregorianCalendar.DAY_OF_YEAR, 1);
			}
			else if (timingParameter.startsWith("Q:")) {
				// quarterly execution (Q:1200) at first day of month
				if (nextStartByThisParameter.get(GregorianCalendar.MONTH) < GregorianCalendar.APRIL)
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.APRIL);
				else if (nextStartByThisParameter.get(GregorianCalendar.MONTH) < GregorianCalendar.JULY)
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.JULY);
				else if (nextStartByThisParameter.get(GregorianCalendar.MONTH) < GregorianCalendar.OCTOBER)
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.OCTOBER);
				else {
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
					nextStartByThisParameter.add(GregorianCalendar.YEAR, 1);
				}
				
				nextStartByThisParameter.set(GregorianCalendar.DAY_OF_MONTH, 1);
				String zeit = timingParameter.substring(timingParameter.indexOf(":") + 1);
				nextStartByThisParameter.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(zeit.substring(0, 2)));
				nextStartByThisParameter.set(GregorianCalendar.MINUTE, Integer.parseInt(zeit.substring(2)));
				nextStartByThisParameter.set(GregorianCalendar.SECOND, 0);
				nextStartByThisParameter.set(GregorianCalendar.MILLISECOND, 0);
				
				// Move also when meeting holiday rule
				while (AgnUtils.dayListIncludes(excludedDays, nextStartByThisParameter))
					nextStartByThisParameter.add(GregorianCalendar.DAY_OF_YEAR, 1);
			}
			else if (timingParameter.startsWith("QW:")) {
				// quarterly execution (QW:1200) at first workingday of month
				if (nextStartByThisParameter.get(GregorianCalendar.MONTH) < GregorianCalendar.APRIL)
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.APRIL);
				else if (nextStartByThisParameter.get(GregorianCalendar.MONTH) < GregorianCalendar.JULY)
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.JULY);
				else if (nextStartByThisParameter.get(GregorianCalendar.MONTH) < GregorianCalendar.OCTOBER)
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.OCTOBER);
				else {
					nextStartByThisParameter.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
					nextStartByThisParameter.add(GregorianCalendar.YEAR, 1);
				}
				
				nextStartByThisParameter.set(GregorianCalendar.DAY_OF_MONTH, 1);

				// Move also when meeting holiday rule
				while (nextStartByThisParameter.get(GregorianCalendar.DAY_OF_WEEK) == java.util.Calendar.SATURDAY
					|| nextStartByThisParameter.get(GregorianCalendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY
					|| AgnUtils.dayListIncludes(excludedDays, nextStartByThisParameter))
					nextStartByThisParameter.add(GregorianCalendar.DAY_OF_MONTH, 1);
				
				String zeit = timingParameter.substring(timingParameter.indexOf(":") + 1);
				nextStartByThisParameter.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(zeit.substring(0, 2)));
				nextStartByThisParameter.set(GregorianCalendar.MINUTE, Integer.parseInt(zeit.substring(2)));
				nextStartByThisParameter.set(GregorianCalendar.SECOND, 0);
				nextStartByThisParameter.set(GregorianCalendar.MILLISECOND, 0);
			}
			else {
				// weekday execution (also allowes workingday execution Werktagssteuerung)
				String wochenTage = timingParameter.substring(0, timingParameter.indexOf(":"));
				String zeit = timingParameter.substring(timingParameter.indexOf(":") + 1);
				List<Integer> weekdayIndexes = new ArrayList<Integer>();
				for (String weekDay : AgnUtils.chopToChunks(wochenTage, 2)) {
					weekdayIndexes.add(getWeekdayIndex(weekDay));
				}
				nextStartByThisParameter.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(zeit.substring(0, 2)));
				nextStartByThisParameter.set(GregorianCalendar.MINUTE, Integer.parseInt(zeit.substring(2)));
				nextStartByThisParameter.set(GregorianCalendar.SECOND, 0);
				nextStartByThisParameter.set(GregorianCalendar.MILLISECOND, 0);

				// Move next start into future (+1 day) until rule is matched
				// Move also when meeting holiday rule
				while ((nextStartByThisParameter.before(now)
						|| !weekdayIndexes.contains(nextStartByThisParameter.get(Calendar.DAY_OF_WEEK))) && nextStartByThisParameter.before(returnStart)
						|| AgnUtils.dayListIncludes(excludedDays, nextStartByThisParameter))
					nextStartByThisParameter.add(GregorianCalendar.DAY_OF_MONTH, 1);
			}

			if (nextStartByThisParameter.before(returnStart))
				returnStart = nextStartByThisParameter;
		}

		return returnStart.getTime();
	}
	
	public static int getWeekdayIndex(String weekday) {
		return getWeekdayIndex(weekday, true);
	}
	
	public static int getWeekdayIndex(String weekday, boolean useLocaleStringsFirst) {
		if (StringUtils.isBlank(weekday)) {
			return -1;
		} else {
			weekday = weekday.toLowerCase().trim();
			String[] localeWeekdays = DateFormatSymbols.getInstance().getWeekdays();
			for (int i = 0; i < localeWeekdays.length; i++) {
				if (localeWeekdays[i].toLowerCase().startsWith(weekday)) {
					return i;
				}
			}
			
			if (weekday.startsWith("so") || weekday.startsWith("su")) {
				return Calendar.SUNDAY;
			} else if (weekday.startsWith("mo")) {
				return Calendar.MONDAY;
			} else if (weekday.startsWith("di") || weekday.startsWith("tu")) {
				return Calendar.TUESDAY;
			} else if (weekday.startsWith("mi") || weekday.startsWith("we")) {
				return Calendar.WEDNESDAY;
			} else if (weekday.startsWith("do") || weekday.startsWith("th")) {
				return Calendar.THURSDAY;
			} else if (weekday.startsWith("fr")) {
				return Calendar.FRIDAY;
			} else if (weekday.startsWith("sa")) {
				return Calendar.SATURDAY;
			} else {
				return -1;
			}
		}
	}
}
