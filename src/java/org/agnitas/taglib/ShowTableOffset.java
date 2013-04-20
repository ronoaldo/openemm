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

package org.agnitas.taglib;

import javax.servlet.jsp.JspTagException;

public class ShowTableOffset extends BodyBase {
    
    private static final long serialVersionUID = 8976977980200158571L;
	private int numRows;
    private int startOffset;
    private int maxRows;
    private int maxPages;
    private int endPage, currentPage, pageNum;
    private String id=null;
    
    @Override
    public void setId(String aId) {
        id=aId;
    }
    
    public void setMaxPages(String num) {
        try {
            maxPages=Integer.parseInt(num);
        } catch (Exception e) {
            maxPages=0;
        }
    }
    
    /**
     * Prepares the offset.
     */
    @Override
    public int doStartTag() throws JspTagException {
        
        if(id==null) {
            id = "";
        }
        
        try {
            maxRows=((Integer)pageContext.getAttribute("__"+id+"_MaxRows")).intValue();
        } catch (Exception e) {
            maxRows=0;
            return SKIP_BODY;
        }
        
        try {
            numRows=((Integer)pageContext.getAttribute("__"+id+"_ShowTableRownum")).intValue();
        } catch (Exception e) {
            numRows=0;
        }
        
        try {
            startOffset=Integer.parseInt(pageContext.getRequest().getParameter("startWith"));
            if(startOffset < 0) {
                startOffset=0;
            }
        } catch (Exception e) {
            startOffset=0;
        }
       
        if(maxRows > 0) { 
            endPage=((numRows+maxRows-1)/maxRows);
            currentPage = startOffset/maxRows;
            pageNum=0;
            if(endPage <= 0)
                return SKIP_BODY;
        }
        
        return doAfterBody();
    }
    
    /**
     * Sets attribute for the pagecontext.
     */
    @Override
    public int doAfterBody() throws JspTagException {

        // pageContext.setAttribute("index", new Integer(a));
        if((pageNum>=endPage) || (pageNum>maxPages))
            return SKIP_BODY;

        pageContext.setAttribute("startWith", Integer.toString(pageNum*maxRows));
        pageContext.setAttribute("pageNum", Integer.toString(pageNum+1));
		pageContext.setAttribute("endPage", Integer.toString(endPage));
        if(pageNum==currentPage) {
            pageContext.setAttribute("activePage", "1");
        } else {
            pageContext.removeAttribute("activePage");
        }
        pageNum++;
        
        return EVAL_BODY_BUFFERED;
    }
    
}
