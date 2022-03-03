package com.example.leadmanager;

public class AccountResponse {
    private String ac_extid__c;
    private String name;
    private String phone;
    private String type;
    private String description;
    private String industry;

    public String getAc_extid__c() {
        return ac_extid__c;
    }

    public void setAc_extid__c(String ac_extid__c) {
        this.ac_extid__c = ac_extid__c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }


}
