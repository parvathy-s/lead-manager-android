package com.example.leadmanager;

public class ContactResponse {
    private String c_extd__c;
    private String cname;
    private String aname;
    private String title;

    public ContactResponse(){

    }

    public ContactResponse(String id, String name, String ac, String tit){
        c_extd__c =id;
        cname = name;
        aname = ac;
        title = tit;
    }

    public String getC_extd__c() {
        return c_extd__c;
    }

    public void setC_extd__c(String c_extd__c) {
        this.c_extd__c = c_extd__c;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
