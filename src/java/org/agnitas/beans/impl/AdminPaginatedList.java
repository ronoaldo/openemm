package org.agnitas.beans.impl;

import org.agnitas.beans.AdminEntry;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.util.List;

public class AdminPaginatedList implements PaginatedList {


    private List<AdminEntry> currentPageElements;

    private int totalsize;

    private int currentPageNumber;

    private int pagesize;

    private SortOrderEnum sortOrder;

    private String sortCriterion;

    public AdminPaginatedList(List<AdminEntry> currentPageElements,
                                   int totalsize, int currentPageNumber, int pagesize,
                                   String sort, String sortCriterion) {

        this.currentPageElements = currentPageElements;
        this.totalsize = totalsize;
        this.currentPageNumber = currentPageNumber;
        this.pagesize = pagesize;
        this.sortOrder = ("asc".equals(sortCriterion) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
        this.sortCriterion = sort;
    }

    public int getFullListSize() {
        return totalsize;
    }

    public List getList() {

        return currentPageElements;
    }

    public int getObjectsPerPage() {
        return pagesize;
    }

    public int getPageNumber() {
        return currentPageNumber;
    }

    public String getSearchId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSortCriterion() {
        return sortCriterion;
    }

    public SortOrderEnum getSortDirection() {
        return sortOrder;
    }

}