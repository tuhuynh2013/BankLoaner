package com.example.tuhuynh.myapplication.agent;

import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.user.User;

public class AgentProfile extends User {

    private BankInfo workBank;

    public AgentProfile() {
        super();
    }

    public AgentProfile(int id, String name, String surname, String identity, String gender, String phone, String address, String role,
                        BankInfo bankInfo) {
        super(id, name, surname, identity, gender, phone, address, role);
        setWorkBank(bankInfo);
    }

    public AgentProfile(BankInfo bankInfo) {
        setWorkBank(bankInfo);
    }

    public BankInfo getWorkBank() {
        return workBank;
    }

    public void setWorkBank(BankInfo workBank) {
        this.workBank = workBank;
    }


}
