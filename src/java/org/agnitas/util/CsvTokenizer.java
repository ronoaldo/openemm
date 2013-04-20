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

import java.util.Vector;

/**
 *
 * @author  mhe
 */
public class CsvTokenizer {

    /**
     * Holds value of property input.
     */
    private String input;

    /**
     * Holds value of property delimiter.
     */
    private String delimiter="";

    /**
     * Holds value of property separator.
     */
    private String separator=";";

    private int parsePos = 0;

    /**
     * Creates a new instance of CsvTokenizer
     */
    public CsvTokenizer() {
    }

    public CsvTokenizer(String input) {
        this.setInput(input);
    }

    public CsvTokenizer(String input, String separator) {
        this.setInput(input);
        this.setSeparator(separator);
    }

    public CsvTokenizer(String input, String separator, String delimiter) {
        this.setInput(input);
        this.setSeparator(separator);
        this.setDelimiter(delimiter);
    }

    /**
     * Getter for property input.
     *
     * @return Value of property input.
     */
    public String getInput() {
        return this.input;
    }

    /**
     * Setter for property input.
     *
     * @param input New value of property input.
     */
    public void setInput(String input) {
        this.input = input;
        this.parsePos=0;
    }

    /**
     * Getter for property delimiter.
     *
     * @return Value of property delimiter.
     */
    public String getDelimiter() {
        return this.delimiter;
    }

    /**
     * Setter for property delimiter.
     *
     * @param delimiter New value of property delimiter.
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Getter for property separator.
     *
     * @return Value of property separator.
     */
    public String getSeparator() {
        return this.separator;
    }

    /**
     * Setter for property separator.
     *
     * @param separator New value of property separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Returns the next token.
     */
    public String nextToken() throws Exception {
        String token=null;
        int startPos=this.parsePos;
        int endPos=-1;
        boolean closingDelimiterReached=false;
        boolean hasDelimiters=false;

        // reached EOL, return null
        if(parsePos>this.input.length()) {
            return null;
        }

        // empty token at end of line
        if(parsePos==this.input.length()) {
            parsePos++;
            return "";
        }

        // query if token starts with delimiter
        if(this.delimiter.length() > 0) {
            if(this.input.substring(this.parsePos, this.parsePos+this.delimiter.length()).equals(this.delimiter)) {
                hasDelimiters=true;
                startPos+=this.delimiter.length();
                closingDelimiterReached=false;
                while(!closingDelimiterReached) {
                    this.parsePos=this.input.indexOf(this.delimiter, this.parsePos+1);
                    if(this.parsePos==-1) { // no closing delimiter, error
                        return null;
                    }
                    // eol reached
                    if(this.parsePos+this.delimiter.length()==this.input.length()) {
                        endPos=this.input.length();
                        break;
                    }
                    if(this.input.substring(this.parsePos+1, this.parsePos+this.delimiter.length()+1).equals(this.delimiter)) {
                        this.parsePos++;
                    } else {
                        closingDelimiterReached=true;
                    }
                }
                endPos=this.parsePos;
            }
        }

        if(hasDelimiters) {
            if(this.parsePos+this.separator.length()+1<this.input.length()) {
                if(!this.input.substring(this.parsePos+1, this.parsePos+this.separator.length()+1).equals(this.separator)) {
                    throw new Exception("error in linestructure!");
                }
            }
        }

        this.parsePos=this.input.indexOf(this.separator, this.parsePos);
        if(this.parsePos==-1) {
            this.parsePos=this.input.length();
        }
        if(endPos==-1) {
            endPos=this.parsePos;
        }

        this.parsePos++;

        token=this.input.substring(startPos, endPos);

        if(hasDelimiters) {
            token=removeDoubleDelimiters(token);
        } else {
            // if token not using delimiters it is not allowed to have delimiter-chars
            if(this.delimiter.length() > 0) {
                if(token.indexOf(this.delimiter)!=-1) {
                    throw new Exception("error in linestructure!");
                }
            }
        }

        return token;
    }

    /**
     * Writes token into an array.
     */
    public String[] toArray() throws Exception {
        Vector<String> vec=new Vector<String>();
        String[] result=null;
        String curr ="";

        while((curr=nextToken())!=null) {
            curr=curr.trim();
            vec.add(curr);
        }

        result=new String[vec.size()];

        for(int c=0; c < vec.size(); c++) {
             result[c]=(String) vec.get(c);
        }
        return result;
    }

    /**
     * Adds source to a string.
     */
    public static String join(String[] src, String separator) {
        StringBuffer ret=new StringBuffer("");

        if(src.length > 0) {
            ret.append(src[0]);
            for(int c=1; c < src.length; c++) {
                ret.append(separator + src[c]);
            }
        }
        return ret.toString();
    }

    /**
     * Removes double delimiter
     */
    protected String removeDoubleDelimiters(String token) {
        StringBuffer tmp=null;
        String doubleDelim = this.delimiter + this.delimiter;
        int delimPos=-1;

        while((delimPos=token.indexOf(doubleDelim))!=-1) {
            tmp=new StringBuffer(token);
            tmp.delete(delimPos, delimPos+this.delimiter.length());
            token=tmp.toString();
        }

        return token;
    }
}
