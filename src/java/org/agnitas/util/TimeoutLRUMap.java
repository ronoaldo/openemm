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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.map.LRUMap;

/**
 * 
 * @author mhe
 */
public class TimeoutLRUMap<K, V> implements java.io.Serializable {
	private static final long serialVersionUID = -1755144418309829988L;

	/**
	 * Holds value of property timeout.
	 */
	private long timeoutInMillis;

	private LRUMap internalMap;

	/**
	 * Creates a new instance of TimeoutLRUMap
	 */
	public TimeoutLRUMap() {
		timeoutInMillis = 5000; // Milliseconds
		internalMap = new LRUMap(1000);
	}

	/**
	 * Creates a new instance of TimeoutLRUMap
	 * 
	 * @param capacity
	 * @param to
	 */
	public TimeoutLRUMap(int capacity, long timeoutInMillis) {
		setTimeout(timeoutInMillis);
		internalMap = new LRUMap(capacity);
	}

	/**
	 * Saves a key with value and default timeout period
	 */
	public synchronized K put(K key, V value) {
		return put(key, value, timeoutInMillis);
	}

	/**
	 * Saves a key with value and explicit timeout period
	 */
	public synchronized K put(K key, V value, long validityPeriod) {
		if (!internalMap.containsKey(key)) {
			TimeoutObject aObject = new TimeoutObject();
			aObject.object = value;
			aObject.validUtil = System.currentTimeMillis() + validityPeriod;
			internalMap.put(key, aObject);
		}
		return key;
	}

	/**
	 * Gets the value from a key
	 */
	public synchronized V get(Object key) {
		@SuppressWarnings("unchecked")
		TimeoutObject timeoutObject = (TimeoutObject) internalMap.get(key);
		if (timeoutObject != null) {
			if (System.currentTimeMillis() < timeoutObject.validUtil) {
				return timeoutObject.object;
			} else {
				internalMap.remove(key);
			}
		}
		return null;
	}
	
	/**
	 * Removes unused objects.
	 */
	public synchronized int cleanupGarbage() {
		int removedCount = 0;
		long time = System.currentTimeMillis();

		// Watch out, because every get() may change the internalMap.
		// Therefore we store the keys in a separate list and iterate that, instead of iterating over the keyset.
		@SuppressWarnings("unchecked")
		List<String> keyList = new ArrayList<String>(internalMap.keySet());
		
		for (String key : keyList) {
			@SuppressWarnings("unchecked")
			TimeoutObject aObject = (TimeoutObject) internalMap.get(key);
			if (time >= aObject.validUtil) {
				internalMap.remove(key);
				removedCount++;
			}
		}

		return removedCount;
	}

	/**
	 * Getter for property timeout.
	 * 
	 * @return Value of property timeout.
	 * 
	 */
	public long getTimeout() {
		return timeoutInMillis;
	}

	/**
	 * Setter for property timeout.
	 * 
	 * @param timeout
	 *            New value of property timeout.
	 */
	public void setTimeout(long timeoutInMillis) {
		this.timeoutInMillis = timeoutInMillis;
	}

	public synchronized int size() {
		return internalMap.size();
	}

	private class TimeoutObject {
		public V object;
		public long validUtil;
	}
}
