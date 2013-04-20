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

package org.agnitas.beans.impl;

import java.util.Hashtable;

import org.agnitas.beans.TagDetails;
import org.agnitas.util.AgnUtils;

/**
 *
 * @author Martin Helff
 */
public class TagDetailsImpl extends Object implements TagDetails {
    
    protected int startPos;
    protected int endPos;
    protected String fullText;
    protected String name;
    
    /** Holds value of property tagName. */
    protected String tagName;
    
    /** Holds value of property tagParameters. */
    protected Hashtable tagParameters;
    
    /** Creates new TagDetails */
    public TagDetailsImpl() {
    }
    
    public int getStartPos() {
        return startPos;
    }
    
    public int getEndPos() {
        return endPos;
    }
    
    public String getFullText() {
        return fullText;
    }
    
    public String getName() {
        return name;
    }
    
    public void analyzeParameters() throws Exception {
        int nameStart=0;
        int nameEnd=0;
        String anchor = null;
        if(fullText.contains("name=\"")){
            nameStart=fullText.indexOf("name=\"");
            nameStart+=6;
            nameEnd=fullText.indexOf('"', nameStart+1);
        } else if(fullText.contains("name=&")){
            nameStart=fullText.indexOf("name=&");
            nameStart+=11;
            nameEnd=fullText.indexOf('&', nameStart+1);
        } else {
          throw new Exception("NoTagName$"+startPos);
        }
        if(nameEnd==-1) {
            throw new Exception("NoTagName$"+startPos);
        }
        name=fullText.substring(nameStart, nameEnd);
    }
    
    public boolean isComplex() {
        if(fullText.endsWith("/]"))
            return false;
        
        return true;
    }
    
    /** Getter for property tagName.
     * @return Value of property tagName.
     *
     */
    public String getTagName() {
        return this.tagName;
    }
    
    /** Setter for property tagName.
     * @param tagName New value of property tagName.
     *
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    public String findTagName() {
        tagName=null;
        int charPos=fullText.indexOf(' ');

        if(charPos==-1) {
            charPos=fullText.indexOf(']');
        }
        try {
            tagName=fullText.substring(1, charPos);
        } catch (Exception e) {
            AgnUtils.logger().error("findTagName: "+e.getMessage());
        }
        
        return tagName;
    }
    
    public boolean findTagParameters() {
        
        int tlen=0;
        String tag=null;
        boolean returnValue=true;
        int rccnt = 0;
        int state = 0;
        StringBuffer scratch = new StringBuffer(tlen);
        String parm=null;
        int pos=0;
        String variable=null;
        String value=null;
        
        this.tagParameters=new Hashtable();
        
        tlen = this.fullText.length();
        
        if ((tlen > 0) && (this.fullText.charAt(0) == '[')) {
            tag = this.fullText.substring(1);
            --tlen;
        } else {
            tag = this.fullText;
        }
        
        if ((tlen > 0) && (tag.charAt(tlen - 1) == ']')) {
            if ((tlen > 1) && (tag.charAt(tlen - 2) == '/')) {
                tag = tag.substring(0, tlen - 2);
            } else {
                tag = tag.substring(0, tlen - 1);
            }
        }
        tlen = tag.length();
        
        for (int n = 0; n <= tlen; ) {
            char ch;
            
            if (n < tlen) {
                ch = tag.charAt(n);
            } else {
                ch = '\0';
                state = 99;
                ++n;
            }
            
            switch (state) {
                default:
                    returnValue=false;
                    // throw new Exception("Invalid state " + state + " for " + this.fullText);
                    break;
                    
                case 0:
                    if(!isspace(ch)) {
                        scratch.setLength(0);
                        state = 1;
                    } else {
                        ++n;
                    }
                    break;
                    
                case 1:
                    if(isspace(ch)) {
                        state = 99;
                    } else {
                        scratch.append(ch);
                        if (ch == '=') {
                            state = 2;
                        }
                    }
                    ++n;
                    break;
                    
                case 2:
                    if(isspace(ch)) {
                        state = 99;
                    } else {
                        scratch.append(ch);
                        if (ch == '"') {
                            state = 3;
                        } else {
                            state = 4;
                        }
                    }
                    ++n;
                    break;
                    
                case 3:
                    scratch.append(ch);
                    if(ch == '"') {
                        state = 99;
                    }
                    ++n;
                    break;
                    
                case 4:
                    if (isspace(ch)) {
                        state = 99;
                    } else {
                        scratch.append(ch);
                    }
                    ++n;
                    break;
                    
                case 99:
                    if(scratch.length() > 0) {
                        parm=scratch.toString();
                        if(rccnt>0) {
                            pos = parm.indexOf('=');
                            if (pos != -1) {
                                variable = parm.substring(0, pos);
                                value = parm.substring(pos + 1);
                                if ((value.length() > 0) && (value.charAt(0) == '"')) {
                                    value = value.substring(1, value.length() - 1);
                                }
                                this.tagParameters.put(variable, value);
                            } else {
                                AgnUtils.logger().warn("findTagParameters: no equal-sign");
                                returnValue=false;
                            }
                        }
                        rccnt++;
                    }
                    state = 0;
                    break;
            }
        }
        return returnValue;
    }
    
    /** Getter for property tagParameters.
     * @return Value of property tagParameters.
     *
     */
    public Hashtable getTagParameters() {
        return this.tagParameters;
    }
    
    /** Setter for property tagParameters.
     * @param tagParameters New value of property tagParameters.
     *
     */
    public void setTagParameters(Hashtable tagParameters) {
    }
    
    private boolean	isspace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\r') || (ch == '\f'));
        
    }

    /**
     * Setter for property endPos.
     * @param endPos New value of property endPos.
     */
    public void setEndPos(int endPos) {
        this.endPos=endPos;
    }

    /**
     * Setter for property fullText.
     * @param fullText New value of property fullText.
     */
    public void setFullText(java.lang.String fullText) {
        this.fullText=fullText;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name=name;
    }

    /**
     * Setter for property startPos.
     * @param startPos New value of property startPos.
     */
    public void setStartPos(int startPos) {
        this.startPos=startPos;
    }
}
