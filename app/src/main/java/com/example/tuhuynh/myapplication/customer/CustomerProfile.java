package com.example.tuhuynh.myapplication.customer;

import com.example.tuhuynh.myapplication.user.User;

public class CustomerProfile extends User {

    private String employment;
    private String company;
    private Long salary;
    private Long bank_account;

    public CustomerProfile() {
        super();
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public Long getBank_account() {
        return bank_account;
    }

    public void setBank_account(Long bank_account) {
        this.bank_account = bank_account;
    }


}
