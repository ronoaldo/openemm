package org.agnitas.web.forms.helper;

import java.io.Serializable;

import org.agnitas.beans.ImageButton;
import org.apache.commons.collections.Factory;


public class ImageButtonFactory implements Factory ,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1787205656128875775L;

	public Object create() {
		return new ImageButton();
	}
}