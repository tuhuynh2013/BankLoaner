package com.example.tuhuynh.myapplication.bank;

import java.io.Serializable;
import java.util.List;

public class BankInfo implements Serializable {
    /*TODO add description*/
    private int id;
    private String name;
    private String shortName;
    private List<InterestAmount> interestAmounts;

    public BankInfo() {

    }

    public BankInfo(int id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }

    public BankInfo(int id, String name, String shortName, List<InterestAmount> interestAmounts) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.interestAmounts = interestAmounts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
