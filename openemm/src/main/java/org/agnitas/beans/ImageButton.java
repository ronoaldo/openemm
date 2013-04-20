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

package org.agnitas.beans;

import java.io.Serializable;

/**
 * Class for storing values sent by clicking an image button.
 * 
 * Using the ImageButton class is really simple:
 * 
 * <ol>
 *   <li>Add ImageButton field to ActionForm and instantiate it (either in constructor or in reset())</li>
 *   <li>Create a setter with parameter of type String. This will be used for the URL parameter containing the button label. Delegate that parameter to the setLabel() method of the ImageButton instance.</li>
 *   <li>Create a getter returning the instance of ImageButton. This will be used by Struts to set the x- and y-coordinates.</li>
 *   <li>To check, if the button was clicked, use isSelected()</li>
 *   <li>If you do not instantiate the ImageButton in reset(), call clearButton() here to reset the button's state to "unclicked".</li>
 * </ol>
 * 
 * @author Markus Dörschmidt
 */
public class ImageButton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3287437050371352848L;

	

	/**
	 * x-coord in image button (if sent).
	 */
	private int x;

	/**
	 * y-coord in image button (if sent).
	 */
	private int y;
	
	/**
	 * Button label, if sent.
	 */
	private String label;
	
	/**
	 * Creates new ImageButton.
	 */
	public ImageButton() {
		clearButton();
	}
	
	/**
	 * Resets button properties to default values.
	 */
	public void clearButton() {
		this.x = -1;
		this.y = -1;
		this.label = "";
	}
	
	/**
	 * Returns x-coordinate. If not sent, -1 is returned. 
	 * @return x-coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Set x-coordinate of click.
	 * @param x x-coordinate of click
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns y-coordinate. If not sent, -1 is returned. 
	 * @return y-coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set y-coordinate of click.
	 * @param y y-coordinate of click
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns button label. If not sent, an empty string is returned. 
	 * @return button label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets button label
	 * @param label button label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Checks, if button was clicked. An image button is clicked, if and x- or y-coordinate was sent or the button label was sent (or all of them).
	 * @return true, if button was clicked
	 */
	public boolean isSelected() {
		return x != -1 || y != -1 || (label != null && !label.equals(""));
	}
}
