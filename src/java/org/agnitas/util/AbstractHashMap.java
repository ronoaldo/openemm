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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 868647429993685054L;

	public AbstractHashMap() {
		super();
	}

	public AbstractHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public AbstractHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public AbstractHashMap(Map<? extends K, ? extends V> map) {
		super(map.size());
		putAll(map);
	}

	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(convertKey(key));
	}

	@Override
	public V get(Object key) {
		return super.get(convertKey(key));
	}

	@Override
	public V put(K key, V value) {
		return super.put(convertKey(key), value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		return super.remove(convertKey(key));
	}
	
	protected abstract K convertKey(Object key);
}
