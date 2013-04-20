package org.agnitas.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * some helper methods for handling mail head, and error messages
 * 
 * @author ms
 * 
 */
public class PreviewHelper {
	
	public static String getFrom(String head) {
		Pattern pattern = Pattern.compile("\\s*From\\s*:\\s*(.*?@.*?>)");
		Matcher matcher = pattern.matcher(head);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getSubject(String head) {
		Pattern pattern = Pattern.compile("^Subject\\s*:\\s*(.*?)\\s*$", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(head);
		if (matcher.find()) {
			return matcher.group(1);

		}
		return null;
	}
	
	/**
	 * extract the different tags and corresponding tag-errors from
	 * the error report
	 * 
	 * @param report -
	 *            each line has to use the following structure
	 *            [agnTag]:errormessage#
	 * @return a map with the tag as key and the error as value
	 */
	public static Map<String, String> getTagsWithErrors(StringBuffer report) {

		Map<String, String> tagWithErrors = new HashMap<String, String>();
		String reportString = report.toString();
		Pattern tagPattern = Pattern.compile("(\\[.*?):(.*?)#");
		Matcher matcher = tagPattern.matcher(reportString);
		while (matcher.find()) {
			tagWithErrors.put(matcher.group(1), matcher.group(2));
		}
		return tagWithErrors;
	}
	
	/**
	 * extract the errormessages which are not related with a tag
	 * @param report
	 * @return list of strings describing the error
	 */
	public static List<String> getErrorsWithoutATag(StringBuffer report) {
			List<String> errorList =  new ArrayList<String>();
			String reportString = report.toString();
			Pattern failedToParsePattern = Pattern.compile("\\s*Failed to parse\\s*:\\s*(.*?)\\s*#"); 
			Matcher matcher = failedToParsePattern.matcher(reportString);
			while(matcher.find()) {
				errorList.add(matcher.group(1));
			}
			
			return errorList;
 	}

}
