package com.example.tuhuynh.myapplication;

import java.io.Serializable;
import java.util.List;

public class BankInfo implements Serializable {
    /*TODO add description*/
    private String name;
    private String shortName;
    private List<InterestAmount> interestAmounts;

    public BankInfo(String name, String shortName, List<InterestAmount> interestAmounts) {
        this.name = name;
        this.shortName = shortName;
        this.interestAmounts = interestAmounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<InterestAmount> getInterestAmounts() {
        return interestAmounts;
    }

    public void setInterestAmounts(InterestAmount interest) {
        this.interestAmounts.add(interest);
    }

}
