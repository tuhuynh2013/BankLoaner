package com.example.tuhuynh.myapplication.user;

import com.example.tuhuynh.myapplication.customer.CustomerInfo;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username, name, email;
    private CustomerInfo customerInfo;

    User(int id, String username, String name, String email) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.customerInfo = new CustomerInfo();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }
}

