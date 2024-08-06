package com.example.springbootemail.model;

public class RegistrationModel extends EmailModel{

    private String name;
    private String companyName;


    public RegistrationModel() {
        super();
        this.name = null;
        this.companyName = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }
}
