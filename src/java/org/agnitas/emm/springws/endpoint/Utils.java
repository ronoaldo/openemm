package org.agnitas.emm.springws.endpoint;

import org.agnitas.emm.springws.jaxb.Map;
import org.agnitas.emm.springws.jaxb.MapItem;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

public class Utils {

	public static CaseInsensitiveMap toCaseInsensitiveMap(Map map) {
		if (map == null || map.getItem() == null) {
			return null;
		}
		CaseInsensitiveMap resultMap = new CaseInsensitiveMap(map.getItem().size());
		for (MapItem item : map.getItem()) {
			resultMap.put(item.getKey(), item.getValue());
		}
		return resultMap;
	}
	
	public static int getUserCompany() {
		// assume that authority looks like USER_{companyId} 
		return Integer.valueOf(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAuthorities()[0].getAuthority().substring(5));
	}

	@SuppressWarnings("rawtypes")
	public static Map toJaxbMap(java.util.Map map, ObjectFactory objectFactory) {
		if (map == null) {
			return null;
		}
		Map resultMap = objectFactory.createMap();
		for (Object key : map.keySet()) {
			MapItem mapItem = objectFactory.createMapItem();
			mapItem.setKey(key);
			mapItem.setValue(map.get(key));
			resultMap.getItem().add(mapItem);
		}
		return resultMap;
	}
	
}
