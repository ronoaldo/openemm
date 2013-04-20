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

/** Convert a relative file path.
 * Converts a relative path to point to the layout folder for the admin.
 */
public class LayoutFileTag extends BodyBase {
    
	protected String	file=null;
	protected String	scope="session";
    
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
    
	public void	setScope(String scope) {
		this.scope = scope;
	}

 
	/**
	 * lists shortnames
	 */
    @Override
	public int doStartTag() throws JspException {
		// Look up the requested bean (if necessary)
		if(TagUtils.getInstance().lookup(pageContext, "emmLayoutBase", scope) == null) {
			TagUtils.getInstance().write(pageContext, file);
			return SKIP_BODY;
		}

		String value = (String) TagUtils.getInstance().lookup(pageContext, "emmLayoutBase", "imagesURL", scope);
        
		if(value == null) {
			TagUtils.getInstance().write(pageContext, file);
			return SKIP_BODY;
		}

		// Print this property value to our output writer
		TagUtils.getInstance().write(pageContext, value+file);

		// Continue processing this page
		return SKIP_BODY;
	}
}
