package org.agnitas.web.forms.helper;

import java.io.Serializable;

import org.apache.commons.collections.Factory;


public class EmptyStringFactory implements Factory ,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3489492316190369204L;

	public Object create() {
		return "";
	}
}