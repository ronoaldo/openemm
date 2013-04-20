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

import java.util.Map;

/**
 * Generic String keyed Map that ignores the String case
 * 
 * @author Andreas
 *
 * @param <V>
 */
public class CaseInsensitiveMap<V> extends AbstractHashMap<String, V> {
	private static final long serialVersionUID = -528027610172636779L;

	public static <V> CaseInsensitiveMap<V> create() {
		return new CaseInsensitiveMap<V>();
	}
	
	public CaseInsensitiveMap() {
		super();
	}

	public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CaseInsensitiveMap(int initialCapacity) {
		super(initialCapacity);
	}

	public CaseInsensitiveMap(Map<? extends String, ? extends V> map) {
		super(map.size());
		putAll(map);
	}

	@Override
	protected String convertKey(Object key) {
		return key == null ? null : key.toString().toLowerCase();
	}
}
