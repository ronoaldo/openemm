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

package org.agnitas.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class SafeString {
	
	private static final transient Logger logger = Logger.getLogger( SafeString.class);
    
    /**
     * Replaces the special characters (like "<", ">" and "\") with HMTL quotation.
     */
    public static String getHTMLSafeString(String input) {
        if ( input == null )
            input = "";
        // input = replace(input, "&", "&amp;");
        input = replace(input, "<", "&lt;");
        input = replace(input, ">", "&gt;");
        input = replace(input, "\"", "&" + "quot;");
        return input;
    }
    
    /**
     * Gets the SQL string.
     */
    public static String getSQLSafeString(String input) {
		if (input == null) {
			return " ";
		} else {
			return input.replace("'", "''");
		}
    }
    
    /**
     * Checks if the email string is in correct email adress syntax.
     */
    public static String getEmailSafeString(String input) {
        int at,pt;
        
        if(input == null)
            return null;
        input=input.toLowerCase().trim();
        if(input.length() < 1)
            return null;
        if((at=input.indexOf('@')) < 1)		// [1-n chars]@
            return null;
        if((pt=input.indexOf('.',at)) < (at+2))	// @[1-n chars].
            return null;
        if(pt >= (input.length()-1))		// .[1-n chars]
            return null;
        return input;
    }
    
   /**
     * Gets the SQL string.
     */
    public static String getSQLSafeString(String input, int len) {
        input=getSQLSafeString(input);
        if(input.length()>len)
            input=input.substring(0, len);
        
        return input;
    }
    
    /**
     * Gets the HTML string.
     */
    public static String getHTMLSafeString(String input, int len) {
        input=getHTMLSafeString(input);
        if(input.length()>len) {
        	for(int i = len; i >= len - 10; i--) {
        		Character help = input.charAt(i);
        		if(help.equals('&')) {
        			input = input.substring(0, i);
        			return input;
        		}
        	}
            input=input.substring(0, len);
        }
        
        return input;
    }
    
    /**
     * Cuts the length of the string to a fixed length.
     *
     * @param len Fixed length.
     */
    public static String cutLength(String input, int len) {
        if(input.length()>len)
            input=input.substring(0, len);
        
        return input;
    }
    
    /**
     * Cuts the length of the string to a fixed length.
     *
     * @param len Fixed length.
     */
    public static String cutByteLength(String input, int len) {
        
        try {
            while(input.getBytes("UTF-8").length>len) {
                input=input.substring(0, input.length()-1);
            }
        } catch (Exception e) {
            logger.error("cutByteLength", e);
        }
        return input;
    }
    
    /**
     * Cuts the length of the line to a length of 72 characters.
     */
    public static String cutLineLength(String input) {
        return SafeString.cutLineLength(input, 72);
    }
    
    /**
     * Cuts the string length into the line length.
     */
    public static String cutLineLength(String input, int lineLength) {
        int posA, posB, posC;
        StringBuffer tmpBuf=null;
        
        posA=0;
        posB=input.indexOf('\n', posA);
        if(posB==-1)
            posB=input.length();
        
        while(true) {
            if((posB-posA) >= lineLength) {
                posC=input.lastIndexOf(' ', posA+lineLength+1);
                if((posC==-1) || (posC<posA)) {
                    posC=input.indexOf(' ', posA);
                    if((posC<posB) && (posC!=-1)) {
                        tmpBuf=new StringBuffer(input);
                        tmpBuf.insert(posC+1, '\n');
                        input=tmpBuf.toString();
                        posA=posC+2;
                    } else {
                        posA=posB+2;
                    }
                } else {
                    tmpBuf=new StringBuffer(input);
                    tmpBuf.insert(posC+1, '\n');
                    input=tmpBuf.toString();
                    posA=posC+2;
                }
            } else {
                posA=posB+1;
            }
            if(posA+lineLength >= input.length())
                break;
            
            posB=input.indexOf('\n', posA);
            if(posB==-1)
                posB=input.length();
        }
        return input;
    }
    
    /**
     * Replaces on string by another string.
     */
    public static String replace(String str, String pattern, String replace) {
        if (replace == null) {
            replace = "";
        }
        int s = 0, e = 0;
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
     * Replaces the characters in a substring.
     *
     * @param str Input string.
     * @param pattern repalceable part of str.
     * @param replace String that should be replaced.
     */
    public static String replaceIgnoreCase(String str, String pattern, String replace) {
        StringBuffer regex=new StringBuffer();
        String letter;
        String toLower;
        for(int i=0; i<pattern.length(); i++) {
            letter=Character.toString(pattern.charAt(i)).toUpperCase();
            toLower = letter.toLowerCase();
            if(letter.equals(toLower)) {
                regex.append(letter);
            } else {
                regex.append("["+letter+toLower+"]");
            }
        }
        
        return str.replaceAll(regex.toString(), replace);
    }
    
    /**
     * Gets a locale string.
     */
    public static String getLocaleString(String key, Locale loc) {
        
        String text=null;
        try {
            ResourceBundle res=ResourceBundle.getBundle("messages", loc);
            text=res.getString(key);
            if(text==null)
                text= "Error, Text missing!";
        } catch (Exception e) {
        	logger.error( "getLocaleString (key: " + key + ", locale: " + loc + ")", e);
            text =  "Error, Text missing!";
        }
        return text;
    }
    
    /**
     * Removes HTML tags from an input string.
     */
    public static String removeHTMLTags(String input) {
        StringBuffer output=new StringBuffer(input);
        int posA, posB=0;
        while((posA=input.indexOf("<"))!=-1) {
            posB=input.indexOf(">", posA);
            if(posB<posA) {
                break;
            }
            output.delete(posA, posB+1);
            input=output.toString();
        }
        
        return output.toString();
    }
}
