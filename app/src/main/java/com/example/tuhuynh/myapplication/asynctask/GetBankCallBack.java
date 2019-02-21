package com.example.tuhuynh.myapplication.asynctask;

import com.example.tuhuynh.myapplication.bank.BankInfo;

import java.util.List;

public interface GetBankCallBack {
    void responseFromAsync(List<BankInfo> banks);
}
