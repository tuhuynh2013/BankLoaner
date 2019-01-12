package com.example.tuhuynh.myapplication.Agent;

import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.user.User;

public class AgentProfile extends User {

    private BankInfo workBank;

    public AgentProfile() {
        super();
    }

    public BankInfo getWorkBank() {
        return workBank;
    }

    public void setWorkBank(BankInfo workBank) {
        this.workBank = workBank;
    }


}
