package org.agnitas.beans.impl;

import org.agnitas.beans.SalutationEntry;

public class SalutationEntryImpl implements SalutationEntry {


    private String description;
    private Integer titleId;


    public SalutationEntryImpl(Integer titleId, String description) {
        this.titleId = titleId;
        this.description = description;
    }


    public Integer getTitleId() {
        return titleId;
    }

    public String getDescription() {
        return description;
    }
}