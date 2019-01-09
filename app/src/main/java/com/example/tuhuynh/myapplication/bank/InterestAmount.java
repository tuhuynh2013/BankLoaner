package com.example.tuhuynh.myapplication.bank;

import java.io.Serializable;

public class InterestAmount implements Serializable {
    private int month;
    private Double interest;

    InterestAmount(int month, Double interest) {
        this.setMonth(month);
        this.setInterest(interest);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }


}
