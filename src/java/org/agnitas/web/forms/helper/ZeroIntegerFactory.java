package org.agnitas.web.forms.helper;

import java.io.Serializable;

import org.apache.commons.collections.Factory;


public class ZeroIntegerFactory implements Factory ,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8521221329956048816L;

	public Object create() {
		return 0;
	}
}