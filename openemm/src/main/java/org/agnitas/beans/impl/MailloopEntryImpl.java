package org.agnitas.beans.impl;

import org.agnitas.beans.MailloopEntry;

public class MailloopEntryImpl implements MailloopEntry {


    private String description;

    private Long id;

    private String shortname;

    private String filterEmail;


    public MailloopEntryImpl(Long id, String description, String shortname, String filterEmail) {
        this.id = id;
        this.description = description;
        this.shortname=shortname;
        this.filterEmail =filterEmail;
    }




    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getShortname() {
        return shortname;
    }

    public String getFilterEmail() {
        return filterEmail;
    }
}