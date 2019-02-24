package com.example.tuhuynh.myapplication.asynctask;

import com.example.tuhuynh.myapplication.bank.BankInfo;

import java.util.List;


public interface GetBanksCallBack {
    void responseFromGetBanks(List<BankInfo> banks);
}
