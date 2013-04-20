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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.cms.utils;

import java.util.*;
import org.agnitas.cms.webservices.generated.*;

/**
 * @author Vyacheslav Stepanov
 */
public class CMLocationsUtils {

	public static List<ContentModuleLocation> createValidLocations(
			List<ContentModuleLocation> existingLocations,
			Collection<Integer> assignedCMs, String[] placeholders,
			int cmTemplateId, int mailingId) {
		if(placeholders == null || placeholders.length == 0) {
			return new ArrayList<ContentModuleLocation>();
		}
		List<ContentModuleLocation> validLocations = new ArrayList<ContentModuleLocation>();
		for(ContentModuleLocation location : existingLocations) {
			if(isValidLocation(location, cmTemplateId, placeholders, assignedCMs)) {
				validLocations.add(location);
			}
		}
		for(Integer cmId : assignedCMs) {
			ContentModuleLocation location = getLocationForCMId(existingLocations, cmId);
			if(validLocations.contains(location)) {
				continue;
			}
			if(location == null) {
				location = new ContentModuleLocation();
				location.setContentModuleId(cmId);
				location.setTargetGroupId(0);
				location.setMailingId(mailingId);
			}
			location.setCmTemplateId(cmTemplateId);
			String firstFreePh = getFirstFreePlaceholder(validLocations, placeholders);
			if(firstFreePh != null) {
				location.setDynName(firstFreePh);
				location.setOrder(1);
			} else {
				continue;
				/*
				// was used if several CMs can be placed inside one placeholder
				String lastPh = placeholders[placeholders.length - 1];
				int order = getLastOrderForPlaceholder(validLocations, lastPh) + 1;
				location.setDynName(lastPh);
				location.setOrder(order);
				*/
			}
			validLocations.add(location);
		}
		// fix positions in placeholders if there are empty slots
		for(String placeholder : placeholders) {
			List<ContentModuleLocation> phLocations =
					getLocationsForPlaceholder(validLocations, placeholder);
			int maxOrder = getLocationsMaxOrder(phLocations);
			int currentOrder = 1;
			for(int orderIndex = 1; orderIndex <= maxOrder; orderIndex++) {
				ContentModuleLocation location = getLocationByOrder(phLocations,
						orderIndex);
				if(location != null) {
					location.setOrder(currentOrder);
					currentOrder++;
				}
			}
		}
		return validLocations;
	}

	private static ContentModuleLocation getLocationByOrder(
			List<ContentModuleLocation> locations, int order) {
		for(ContentModuleLocation location : locations) {
			if(location.getOrder() == order) {
				return location;
			}
		}
		return null;
	}


	private static int getLocationsMaxOrder(List<ContentModuleLocation> locations) {
		int maxOrder = 0;
		for(ContentModuleLocation location : locations) {
			if(location.getOrder() > maxOrder) {
				maxOrder = location.getOrder();
			}
		}
		return maxOrder;
	}

	private static List<ContentModuleLocation> getLocationsForPlaceholder(
			List<ContentModuleLocation> locations, String placeholder) {
		List<ContentModuleLocation> resultList = new ArrayList<ContentModuleLocation>();
		for(ContentModuleLocation location : locations) {
			if(placeholder.equals(location.getDynName())) {
				resultList.add(location);
			}
		}
		return resultList;
	}

	public static int getLastOrderForPlaceholder(List<ContentModuleLocation> locations,
												 String placeholder) {
		int orderMax = 0;
		for(ContentModuleLocation location : locations) {
			if(placeholder.equals(location.getDynName())) {
				if(location.getOrder() > orderMax) {
					orderMax = location.getOrder();
				}
			}
		}
		return orderMax;
	}

	public static boolean isValidLocation(ContentModuleLocation location, int templateId,
										  String[] placeholders,
										  Collection<Integer> assignedCMs) {
		boolean placeholderIsValid = false;
		for(String placeholder : placeholders) {
			if(placeholder.equals(location.getDynName())) {
				placeholderIsValid = true;
				break;
			}
		}
		boolean templateIdIsValid = location.getCmTemplateId() == templateId;
		boolean cmIsAssigned = assignedCMs.contains(location.getContentModuleId());
		return placeholderIsValid && templateIdIsValid && cmIsAssigned;
	}

	public static ContentModuleLocation getLocationForCMId(
			List<ContentModuleLocation> locations, int cmId) {
		for(ContentModuleLocation location : locations) {
			if(location.getContentModuleId() == cmId) {
				return location;
			}
		}
		return null;
	}

	public static String getFirstFreePlaceholder(List<ContentModuleLocation> locations,
												 String[] placeholders) {
		for(String placeholder : placeholders) {
			boolean freePlaceholder = true;
			for(ContentModuleLocation location : locations) {
				if(placeholder.equals(location.getDynName())) {
					freePlaceholder = false;
					break;
				}
			}
			if(freePlaceholder) {
				return placeholder;
			}
		}
		return null;
	}
}
