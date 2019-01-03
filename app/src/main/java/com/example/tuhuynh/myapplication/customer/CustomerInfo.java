package com.example.tuhuynh.myapplication.customer;

public class CustomerInfo {

    private String surname;
    private String identity;
    private String gender;
    private String phone;
    private String address;
    private String workplace;
    private String designation;

    public CustomerInfo() {

    }

    public CustomerInfo(String surname, String identity, String gender,
                        String phone, String address, String workplace, String designation) {
        this.surname = surname;
        this.identity = identity;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.workplace = workplace;
        this.designation = designation;
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

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }


}
