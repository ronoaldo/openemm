package org.agnitas.emm.core.mailinglist.service;

public class MailinglistModel {
    private int companyId;
    private int mailinglistId;
    private String shortname;
    private String description;

    public int getCompanyId() {
        return companyId;
    }
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getMailinglistId() {
        return mailinglistId;
    }
    public void setMailinglistId(int mailinglistId) {
        this.mailinglistId = mailinglistId;
    }

    public String getShortname() {
        return shortname;
    }
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
