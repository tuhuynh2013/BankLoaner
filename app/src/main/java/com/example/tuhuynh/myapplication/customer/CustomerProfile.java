package com.example.tuhuynh.myapplication.customer;

import com.example.tuhuynh.myapplication.user.User;
import com.example.tuhuynh.myapplication.util.CustomUtil;

public class CustomerProfile extends User {

    private String employment;
    private String company;
    private Long salary;
    private String bankAccount;

    public CustomerProfile() {
        super();
    }

    public CustomerProfile(String employment, String company, Long salary, String bankAccount) {
        setEmployment(employment);
        setCompany(company);
        setSalary(salary);
        setBankAccount(bankAccount);
    }

    public CustomerProfile(int id, String name, String surname, String identity, String gender, String phone, String address, String role,
                           String employment, String company, Long salary, String bankAccount) {
        super(id, name, surname, identity, gender, phone, address, role);
        setEmployment(employment);
        setCompany(company);
        setSalary(salary);
        setBankAccount(bankAccount);
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

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public boolean isMissingInfo() {
        return !CustomUtil.hasMeaning(this.getIdentity()) &&
                !CustomUtil.hasMeaning(this.getPhone()) &&
                !CustomUtil.hasMeaning(this.getEmployment()) &&
                !CustomUtil.hasMeaning(this.getCompany()) &&
                !CustomUtil.hasMeaning(this.getSalary().toString()) &&
                !CustomUtil.hasMeaning(this.getBankAccount());
    }

}
