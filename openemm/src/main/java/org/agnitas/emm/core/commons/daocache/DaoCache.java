package org.agnitas.emm.core.commons.daocache;

/**
 * Interface for wrapping DAO classes by a cache.
 * 
 * @author md
 *
 * @param <T> type of items returned by the wrapped DAO
 */
public interface DaoCache<T> {
	
	/**
	 * Returns the item with the given ID. When the item is stored in the cache, the cached
	 * item is returned. Otherwise, the items is taken from the DAO, stored in the cache
	 * and returned.
	 * 
	 * @param id item ID
	 * 
	 * @return item
	 */
    public T getItem( int id);
}
