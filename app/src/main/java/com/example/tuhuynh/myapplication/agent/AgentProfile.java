package com.example.tuhuynh.myapplication.agent;

import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.user.UserProfile;


public class AgentProfile extends UserProfile {

    private BankInfo workBank;

    public AgentProfile() {
        super();
    }

    public AgentProfile(UserProfile userProfile,
                        BankInfo bankInfo) {
        super(userProfile.getId(), userProfile.getName(), userProfile.getSurname(), userProfile.getEmail(),
                userProfile.getIdentity(), userProfile.getGender(), userProfile.getPhone(),
                userProfile.getAddress(), userProfile.getRole());
        setWorkBank(bankInfo);
    }

    public AgentProfile(BankInfo bankInfo) {
        setWorkBank(bankInfo);
    }

    public BankInfo getWorkBank() {
        return workBank;
    }

    private void setWorkBank(BankInfo workBank) {
        this.workBank = workBank;
    }


}
