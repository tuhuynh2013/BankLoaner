package com.example.tuhuynh.myapplication.appication;

import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.user.User;

import java.util.Date;

public class ApplicationInfo {

    private int id;
    private int month;
    private Long amount;
    private double interest;
    private User customer;
    private User agent;
    private BankInfo bankInfo;
    private Date date;
    private String status;


    public ApplicationInfo(int id, int month, Long amount,
                           double interest, User customer, User agent,
                           BankInfo bankInfo, Date date, String status) {

        this.setId(id);
        this.setMonth(month);
        this.setAmount(amount);
        this.setInterest(interest);
        this.setCustomer(customer);
        this.setAgent(agent);
        this.setBankInfo(bankInfo);
        this.setDate(date);
        this.setStatus(status);

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

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getAgent() {
        return agent;
    }

    private void setAgent(User agent) {
        this.agent = agent;
    }

    public BankInfo getBankInfo() {
        return bankInfo;
    }

    private void setBankInfo(BankInfo bankInfo) {
        this.bankInfo = bankInfo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }


}
