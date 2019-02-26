package com.example.tuhuynh.myapplication.user;

import java.io.Serializable;


public class UserProfile implements Serializable {

    private String id;
    private String name;
    private String surname;
    private String email;
    private String identity;
    private String gender;
    private String phone;
    private String address;
    private String role;
    private String accountType;
    private String status;

    public UserProfile() {

    }

    // Uses for SharedPrefManager and update user profile
    public UserProfile(String id, String name, String surname, String email, String identity, String gender, String phone, String address, String role, String accountType) {
        setId(id);
        setName(name);
        setSurname(surname);
        setEmail(email);
        setIdentity(identity);
        setGender(gender);
        setPhone(phone);
        setAddress(address);
        setRole(role);
        setAccountType(accountType);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

