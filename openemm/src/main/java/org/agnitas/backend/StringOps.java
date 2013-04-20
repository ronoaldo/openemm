/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.backend;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ibm.icu.text.IDNA;
import com.ibm.icu.text.StringPrepParseException;

/**
 * Some useful string operations
 */
public class StringOps {
	/**
	 * for strip off HTML code
	 */
	private static Pattern FINDHTML = Pattern.compile("<[!/?%a-z][^>]*>|&([a-z]+|#x?[0-9a-f]+);", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	/**
	 * translation table for transforming between HTML and text
	 */
	static private Hashtable<String, String> transtab = null;
	static private Hashtable<String, String> rtranstab = null;
	static private Pattern entityFinder = null;
	static {
		transtab = new Hashtable<String, String>();

		transtab.put("lt", "<");
		transtab.put("gt", ">");
		transtab.put("amp", "&");
		transtab.put("quot", "\"");
		transtab.put("apos", "'");
		transtab.put("nbsp", " ");
/*
		transtab.put("auml", "ä");
		transtab.put("ouml", "ö");
		transtab.put("uuml", "ü");
		transtab.put("Auml", "Ä");
		transtab.put("Ouml", "Ö");
		transtab.put("Uuml", "Ü");
*/
		
		rtranstab = new Hashtable<String, String>();
		StringBuffer	pm = new StringBuffer ();
		String		pfix = "&(";
		for (Entry<String, String> entry : transtab.entrySet()) {
			rtranstab.put(entry.getValue(), entry.getKey());
			pm.append (pfix);
			pm.append (entry.getKey ());
			pfix = "|";
		}
		pm.append (");");
		entityFinder = Pattern.compile (pm.toString ());
	}

	public static String decodeEntity(String ent, String dflt) {
		String rc = transtab.get(ent);
		return rc == null ? dflt : rc;
	}

	public static String decodeEntity(String ent) {
		return transtab.get(ent);
	}

	public static String encodeEntity(String plain, String dflt) {
		String rc = rtranstab.get(plain);
		return rc == null ? dflt : rc;
	}

	public static String encodeEntity(String plain) {
		return rtranstab.get(plain);
	}

	public static String removeEntities(String s) {
		Matcher		m = entityFinder.matcher (s);
		int 		slen = s.length();
		StringBuffer	d = new StringBuffer (slen);
		int		pos = 0;
		
		while ((pos < slen) && m.find (pos)) {
			int	start = m.start ();
			
			if (pos < start) {
				d.append (s.substring (pos, start));
			}
			d.append (transtab.get (m.group (1)));
			pos = m.end ();
		}
		if (pos < slen) {
			d.append (s.substring (pos));
		}
		return d.toString ();
	}

	/**
	 * replaces every occurance of `pattern' in `str' with `replace'
	 * 
	 * @param str
	 *            the source
	 * @param pattern
	 *            the pattern to replace
	 * @param replace
	 *            the substition
	 * @return the new string with replacements
	 */
	public static String replace(String str, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();
		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

	/**
	 * fill the left side of the string with `0's until `length' is reached
	 * 
	 * @param text
	 *            the source
	 * @param length
	 *            the length to extend the string to
	 * @return the filled string
	 */
	public static String format_number(String text, int length) {
		int text_length = text.length();

		if (text_length >= length) {
			return text;
		}
		String result = text;
		for (int i = text_length; i != length; i++) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * fill the left side of a string representation of a number with `0's until
	 * length is reached
	 * 
	 * @param nr
	 *            the source
	 * @param length
	 *            the length to extend the string to
	 * @return the filled string
	 */
	public static String format_number(int nr, int length) {
		return format_number(Integer.toString(nr), length);
	}

	/**
	 * Split a comma separated string into its elements
	 * 
	 * @param str
	 *            the input string
	 * @return the filled vector
	 */
	public static Vector<String> splitString(String str) {
		return splitString(null, str);
	}
	
	/**
	 * Split a comma separated string into its elements
	 * 
	 * @param v
	 *            optional existing vector to append to
	 * @param str
	 *            the input string
	 * @return the filled vector
	 */
	public static Vector<String> splitString(Vector<String> v, String str) {
		if (v == null) {
			v = new Vector<String>();
		}
		if (StringUtils.isNotEmpty(str)) {
			for (String part : str.split(",")) {
				v.add(part.trim());
			}
		}
		
		return v;
	}

	/**
	 * Converts a DB blob into a string
	 * 
	 * @param blob
	 *            the source
	 * @param encoding
	 *            the encoding of the blob
	 * @return the extracted string
	 */
	public static String blob2string(Blob blob, String encoding) throws SQLException {
		String rc;

		try {
			rc = blob == null ? "" : new String(blob.getBytes(1, (int) blob.length()), encoding);
		} catch (UnsupportedEncodingException e) {
			rc = null;
		}
		return rc;
	}

	/**
	 * Converts a DB clob into a string
	 * 
	 * @param clob
	 *            the source
	 * @return the extracted string
	 */
	public static String clob2string(Clob clob) throws SQLException {
		return clob == null ? "" : clob.getSubString(1, (int) clob.length());
	}

	/**
	 * Simple formating for a date
	 * 
	 * @param date
	 *            the source
	 * @return the formated date as string
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss");

		return fmt.format(date);
	}

	/**
	 * Format a date to a SQL expression
	 * 
	 * @param date
	 *            the source
	 * @return the SQL expression
	 */
	public static String sqlDate(Date date) {
		return "str_to_date('" + formatDate(date) + "', '%d-%m-%Y:%H:%i:%s')";
	}

	/**
	 * Format a string to its SQL representation
	 * 
	 * @param str
	 *            the source
	 * @return the SQL conform strin
	 */
	public static String sqlString(String str) {
		StringBuffer r = new StringBuffer(str.length() + 8);

		r.append('\'');
		for (int n = 0; n < str.length(); ++n) {
			char ch = str.charAt(n);

			r.append(ch);
			if (ch == '\'')
				r.append(ch);
		}
		r.append('\'');
		return r.toString();
	}

	/**
	 * Transform an SQL representation to a string
	 * 
	 * @param str
	 *            the source
	 * @return the stripped off version
	 */
	public static String unSqlString(String str) {
		int start, end;

		start = 0;
		end = str.length();
		if ((end > 0) && (str.charAt(0) == '\'') && (str.charAt(end - 1) == '\'')) {
			++start;
			--end;

			StringBuffer r = new StringBuffer(end - start + 1);
			for (int n = start; n < end; ++n) {
				char ch = str.charAt(n);

				r.append(ch);
				if ((ch == '\'') && (n + 1 < end) && (str.charAt(n + 1) == '\''))
					++n;
			}
			str = r.toString();
		}
		return str;
	}

	/**
	 * convert the domain part of the email to punycoded, if required
	 * 
	 * @param email
	 *            the email address
	 * @return the email in punycode format
	 */
	public static String punycoded(String email) {
		if (email == null) return null;
		
		int n;

		if ((n = email.indexOf('@')) != -1) {
			String user = email.substring(0, n);
			String domain = email.substring(n + 1).toLowerCase();
			int dlen = domain.length();
			StringBuffer ndomain = new StringBuffer(dlen + 32);

			n = 0;
			while (n < dlen) {
				int dpos = domain.indexOf('.', n);
				String sub;

				if (dpos == -1)
					dpos = dlen;
				sub = domain.substring(n, dpos);
				try {
					sub = IDNA.convertToASCII(sub, IDNA.DEFAULT).toString();
				} catch (StringPrepParseException e) {
					;
				}
				ndomain.append(sub);
				if (dpos < dlen)
					ndomain.append('.');
				n = dpos + 1;
			}
			email = user + '@' + ndomain.toString();
		}
		return email;
	}

	/**
	 * convert old style <agn ...> and </agn ...> to new style [agn ...] and
	 * [/agn ...]
	 * 
	 * @param in
	 *            the source
	 * @return the converted string
	 */
	public static String convertOld2New(String in) {
		int ilen = in.length();
		StringBuffer out = new StringBuffer(ilen);
		int n, cur, pos;

		cur = 0;
		while ((cur < ilen) && ((n = in.indexOf('<', cur)) != -1) && (n + 5 < ilen)) {
			out.append(in.substring(cur, n));
			pos = n;
			++n;
			if (in.charAt(n) == '/')
				++n;
			if (in.substring(n, n + 3).equals("agn") && ((n = in.indexOf('>', n)) != -1)) {
				out.append('[' + in.substring(pos + 1, n) + ']');
				cur = n + 1;
			} else {
				out.append(in.charAt(pos));
				cur = pos + 1;
			}
		}
		if (cur < ilen)
			out.append(in.substring(cur));
		return out.toString();
	}

	/**
	 * Removed HTML entities and tags from the input
	 * 
	 * @param inputString
	 *            input string
	 * @return the HTML-cleared string
	 */
	public static String removeHTMLTagsAndEntities(String inputString) {
		if (StringUtils.isEmpty(inputString)) {
			return inputString;
		} else {
			Matcher regexMatcher = FINDHTML.matcher(inputString);
			boolean hasNextMatch = regexMatcher.find();
			if (!hasNextMatch) {
				return inputString;
			} else {
				StringBuffer returnValue = new StringBuffer(inputString.length());

				int endOfPreviousMatchingString = 0;

				while (hasNextMatch) {
					// group(1) only takes the inner part without & or ;
					String matchingString = regexMatcher.group(1);
					int matchingStringStart = regexMatcher.start();
					if (endOfPreviousMatchingString < matchingStringStart) {
						returnValue.append(inputString.substring(endOfPreviousMatchingString, matchingStringStart));
					}
					endOfPreviousMatchingString = regexMatcher.end();
					if (matchingString != null) {
						String replacement = decodeEntity(matchingString);
						if (replacement != null) {
							returnValue.append(replacement);
						}
					}

					hasNextMatch = regexMatcher.find();
				}

				if (endOfPreviousMatchingString < inputString.length()) {
					returnValue.append(inputString.substring(endOfPreviousMatchingString));
				}

				return returnValue.toString();
			}
		}
	}
}
