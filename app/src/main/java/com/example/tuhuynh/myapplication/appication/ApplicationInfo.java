package com.example.tuhuynh.myapplication.appication;

import java.util.Date;

public class ApplicationInfo {

    private int id;
    private int month;
    private Long amount;
    private double interest;
    private String agentName;
    private String bankName;
    private String shortName;
    private Date date;
    private String status;

    ApplicationInfo() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getAgentName() {
        return agentName;
    }

    void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getBankName() {
        return bankName;
    }

    void setBankName(String bankName) {
        this.bankName = bankName;
    }

    String getShortName() {
        return shortName;
    }

    void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }


}
