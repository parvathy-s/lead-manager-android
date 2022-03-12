package com.example.leadmanager;

public class LeadResponse {

    private String l_extid__c;
    private String name;
    private String company;
    private String status;

    public LeadResponse(String id, String name, String company, String status){
        l_extid__c=id;
        this.name=name;
        this.company=company;
        this.status=status;
    }
    public String getL_extid__c() {
        return l_extid__c;
    }

    public void setL_extid__c(String l_extid__c) {
        this.l_extid__c = l_extid__c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
