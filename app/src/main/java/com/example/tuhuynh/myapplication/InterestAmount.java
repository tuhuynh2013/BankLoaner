package com.example.tuhuynh.myapplication;

import java.io.Serializable;

public class InterestAmount implements Serializable {
    private int year;
    private Double interest;

    public InterestAmount(int year, Double interest) {
        this.setYear(year);
        this.setInterest(interest);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }


}
