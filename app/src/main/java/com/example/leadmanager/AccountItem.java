package com.example.leadmanager;

public class AccountItem {
    private String name;
    private String id;
    private String type;
    private String industry;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getIndustry() {
        return industry;
    }

    public AccountItem(String name, String id,String type, String industry){
        this.name = name;
        this.id = id;
        this.type = type;
        this.industry = industry;
    }
}
