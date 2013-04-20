package org.agnitas.emm.core.commons.daocache;

import org.agnitas.util.TimeoutLRUMap;

/**
 * Basic implementation of the DAOCache interface providing caching functionality, but only abstract access
 * to the DAO.
 * 
 * @author md
 *
 * @param <T> type of items returned by the DAO
 */
public abstract class AbstractDaoCache<T> implements DaoCache<T> {

	// --------------------------------------------------- Dependency Injection
	/** Cache providing aging of the stored elements. */
	private TimeoutLRUMap cache;

	/**
	 * Set the cache map.
	 * 
	 * @param cache cache map
	 */
	public void setCache( TimeoutLRUMap cache) {
		this.cache = cache;
	}

	// --------------------------------------------------- Business Logic
	@Override
	public T getItem( int id) {
		if( cache == null) {
			// When no cache is set, access DAO directly 
			return getItemFromDao( id);
		} else {
			// First, try to get the requested item from the cache map
			@SuppressWarnings("unchecked")
			T item = (T) cache.get( id);

			if( item == null) {
				// If no item was returned, get the item from the DAO
				item = getItemFromDao( id);
				
				if( item != null) {
					// and put it into the cache
					cache.put( id, item);
				}
			}

			return item;
		}
	}

	/**
	 * Get item from DAO.
	 * 
	 * @param id item ID
	 * 
	 * @return item
	 */
	protected abstract T getItemFromDao( int id);
}
