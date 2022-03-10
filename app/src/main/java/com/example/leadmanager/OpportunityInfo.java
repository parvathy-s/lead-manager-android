package com.example.leadmanager;

public class OpportunityInfo {
    private String name;
    private String amount;
    private String closedate;
    private String stagename;
    private String accountid;
    private String contact__c;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getClosedate() {
        return closedate;
    }

    public void setClosedate(String closedate) {
        this.closedate = closedate;
    }

    public String getStagename() {
        return stagename;
    }

    public void setStagename(String stagename) {
        this.stagename = stagename;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getContact__c() {
        return contact__c;
    }

    public void setContact__c(String contact__c) {
        this.contact__c = contact__c;
    }
}
