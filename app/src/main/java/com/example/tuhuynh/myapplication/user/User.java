package com.example.tuhuynh.myapplication.user;

import com.example.tuhuynh.myapplication.customer.CustomerProfile;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username, name, email;
    private CustomerProfile customerProfile;

    User(int id, String username, String name, String email) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.customerProfile = new CustomerProfile();
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

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }


}

