package com.example.leadmanager;

public class OpportunityResponse {

    private String o_extid__c;
    private String oname;
    private String aname;
    private String stagename;

    public OpportunityResponse(){

    }

    public OpportunityResponse(String id, String name, String ac, String stage){
        o_extid__c=id;
        oname=name;
        aname=ac;
        stagename=stage;
    }

    public String getO_extid__c() {
        return o_extid__c;
    }

    public void setO_extid__c(String o_extid__c) {
        this.o_extid__c = o_extid__c;
    }

    public String getOname() {
        return oname;
    }

    public void setOname(String oname) {
        this.oname = oname;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getStagename() {
        return stagename;
    }

    public void setStagename(String stagename) {
        this.stagename = stagename;
    }
}
