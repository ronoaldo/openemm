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

import javax.servlet.jsp.JspException;
import org.apache.struts.taglib.TagUtils;
import javax.servlet.http.HttpServletResponse;

/** Convert a relative file path.
 * Converts a relative path to point to the layout folder for the admin.
 */
public class LayoutImgTag extends BodyBase {
    
	protected String	file=null;
	protected String	scope="session";
	protected String	align=null;
	protected String	height=null;
	protected String	width=null;
	protected String	border="0";
	protected String	hspace=null;
	protected String	vspace=null;
	protected String	alt=null;
	protected String	altKey=null;
    
	/**
	 * Setter for property column.
	 * 
	 * @param aCol New value of property column.
	 */
	public void	setFile(String file) {
		if(file!=null) {
			this.file=file;
		} else {
			this.file="";
		}
	}
    
	public void	setScope(String scope) { this.scope = scope; }
	public void	setAlign(String align) { this.align = align; }
	public void	setHeight(String height) { this.height = height; }
	public void	setWidth(String width) { this.width = width; }
	public void	setBorder(String border) { this.border = border; }
	public void	setHspace(String hspace) { this.hspace = hspace; }
	public void	setVspace(String vspace) { this.hspace = vspace; }
	public void	setAlt(String alt) { this.alt = alt; }

	public void	setAltKey(String alt) { this.altKey=alt; }
 
	/**
	 * lists shortnames
	 */
    @Override
	public int doStartTag() throws JspException {
		HttpServletResponse response = 
			(HttpServletResponse) pageContext.getResponse();
		StringBuffer result = new StringBuffer("<img");
		String	path="";

		if(file == null) {
			return SKIP_BODY;
		}

		// Look up the requested bean (if necessary)

		if(TagUtils.getInstance().lookup(pageContext, "emmLayoutBase", scope) != null) {
			String value = (String) TagUtils.getInstance().lookup(pageContext, "emmLayoutBase", "imagesURL", scope);
        
			if(value != null) {
				path=value;
			}
		}
		if(altKey != null) {
			alt=TagUtils.getInstance().message(pageContext, null, null, alt, null);
		}
           	result.append(" src=\""+response.encodeURL(path+file)+"\"");
		if(height != null) {
           		result.append(" height=\""+height+"\"");
		}
		if(width != null) {
           		result.append(" width=\""+width+"\"");
		}
		if(align != null) {
           		result.append(" align=\""+align+"\"");
		}
		if(border != null) {
           		result.append(" border=\""+border+"\"");
		}
		if(hspace != null) {
           		result.append(" hspace=\""+hspace+"\"");
		}
		if(vspace != null) {
           		result.append(" vspace=\""+vspace+"\"");
		}
		if(alt != null) {
           		result.append(" alt=\""+alt+"\"");
		}
           	result.append(">");

		// Print this property value to our output writer
		TagUtils.getInstance().write(pageContext, result.toString());

		// Continue processing this page
		return SKIP_BODY;
	}
}
