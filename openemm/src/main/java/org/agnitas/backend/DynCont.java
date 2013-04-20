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


/**
 * Holds all information about one dynamic content block
 */
public class DynCont {
	/** constant for always matching */
	public static final long MATCH_NEVER = -1;
	/** constant for never matching */
	public static final long MATCH_ALWAYS = 0;

	/** Unique content ID */
	public long id;
	/** ID for the target condiition */
	public long targetID;
	/** order to describe importance of this part */
	public long order;
	/** textual content */
	protected BlockData text;
	/** HTML content */
	protected BlockData html;
	/** the condition */
	protected String condition;

	/**
	 * Constructor
	 * 
	 * @param dynContId
	 *            the unique ID
	 * @param dynTarget
	 *            the optional target id
	 * @param dynOrder
	 *            the order value
	 * @param dynContent
	 *            the content of the block
	 */
	public DynCont(long dynContId, long dynTarget, long dynOrder, String dynContent) {
		id = dynContId;
		targetID = dynTarget;
		order = dynOrder;
		text = new BlockData(StringOps.removeHTMLTagsAndEntities(dynContent), null, null, BlockData.TEXT, 0, 0, "text/plain", true, true);
		html = new BlockData(dynContent, null, null, BlockData.HTML, 0, 0, "text/html", true, true);
		condition = null;
	}

	public DynCont() {
		condition = null;
	}
}
