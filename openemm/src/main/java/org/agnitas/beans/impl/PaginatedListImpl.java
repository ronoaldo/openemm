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
package org.agnitas.beans.impl;

import java.util.List;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

public class PaginatedListImpl<T> implements PaginatedList {

	private List<T>  partialList;
	private int fullListSize;
	private int pageSize;
	private int pageNumber = 1;
	private String sortCriterion;
	private SortOrderEnum sortDirection = SortOrderEnum.ASCENDING; // DESC or ASC
		
	public PaginatedListImpl(List<T> partialList, int fullListSize, int pageSize, int pageNumber, String sortCriterion, String sortDirection) {
		super();
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.sortCriterion = sortCriterion;
		setSortDirection(sortDirection);
	}

	public void setPartialList(List<T> partialList) {
		this.partialList = partialList;
	}

	public int getFullListSize() {
		return fullListSize;
	}

	public List<T> getList() {
		return partialList;
	}

	public int getObjectsPerPage() {
		return pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public String getSearchId()  {
		return null;
	}

	public String getSortCriterion() {
		return sortCriterion; 
	}

	public SortOrderEnum getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = "ASC".equalsIgnoreCase(sortDirection) ?  SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setFullListSize(int fullListSize) {
		this.fullListSize = fullListSize;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setSortCriterion(String sortCriterion) {
		this.sortCriterion = sortCriterion;
	}
}
