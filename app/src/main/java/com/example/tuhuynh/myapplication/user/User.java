package com.example.tuhuynh.myapplication.user;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;
    private String name;
    private String email;
    private String surname;
    private String identity;
    private String gender;
    private String phone;
    private String address;
    private String role;

    protected User() {

    }

    public User(int id, String username, String name, String email, String role) {
        setId(id);
        setUsername(username);
        setName(name);
        setEmail(email);
        setRole(role);
    }

    public User(String name, String surname, String gender, String phone, String address) {
        setName(name);
        setSurname(surname);
        setGender(gender);
        setPhone(phone);
        setAddress(address);
    }

    public User(int id, String name, String surname, String identity, String gender, String phone, String address, String role) {
        setId(id);
        setName(name);
        setSurname(surname);
        setIdentity(identity);
        setGender(gender);
        setPhone(phone);
        setAddress(address);
        setRole(role);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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


}

