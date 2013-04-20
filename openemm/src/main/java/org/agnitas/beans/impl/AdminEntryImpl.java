package org.agnitas.beans.impl;

import org.agnitas.beans.AdminEntry;

public class AdminEntryImpl implements AdminEntry {


    private String shortname;
    private String username;
    private String fullname;
    private int id;


    public AdminEntryImpl(Integer id, String username, String fullname, String shortname) {
        this.username=username;
        this.fullname=fullname;
        this.shortname=shortname;
        this.id=id;
    }


    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getShortname() {
        return shortname;
    }

    public int getId() {
        return id;
    }
}