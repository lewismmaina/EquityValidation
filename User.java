package com.example.equityvalidation;

import java.util.Date;



public class User {
    String agentcode;
    String fullName;
    String branch;
    String country;
    Date sessionExpiryDate;

    public void setAgentcode(String agentcode) {
        this.agentcode = agentcode;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

    public String getAgentcode() {
        return agentcode;
    }

}