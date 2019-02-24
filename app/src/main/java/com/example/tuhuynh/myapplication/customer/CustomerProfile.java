package com.example.tuhuynh.myapplication.customer;

import com.example.tuhuynh.myapplication.user.UserProfile;
import com.example.tuhuynh.myapplication.util.CustomUtil;


public class CustomerProfile extends UserProfile {

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

    // Uses for GetUserProfileAsync
    public CustomerProfile(UserProfile userProfile,
                           String employment, String company, Long salary, String bankAccount) {

        super(userProfile.getId(), userProfile.getName(), userProfile.getSurname(), userProfile.getEmail(),
                userProfile.getIdentity(), userProfile.getGender(), userProfile.getPhone(),
                userProfile.getAddress(), userProfile.getRole());
        setEmployment(employment);
        setCompany(company);
        setSalary(salary);
        setBankAccount(bankAccount);
    }

    public String getEmployment() {
        return employment;
    }

    private void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getCompany() {
        return company;
    }

    private void setCompany(String company) {
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

    private void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    boolean isMissingInfo() {
        return !CustomUtil.hasMeaning(this.getPhone()) ||
                !CustomUtil.hasMeaning(this.getEmployment()) ||
                !CustomUtil.hasMeaning(this.getCompany()) ||
                !CustomUtil.hasMeaning(this.getSalary().toString()) ||
                !CustomUtil.hasMeaning(this.getBankAccount());
    }

}
