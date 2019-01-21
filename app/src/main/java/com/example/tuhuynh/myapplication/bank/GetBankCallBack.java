package com.example.tuhuynh.myapplication.bank;

import java.util.List;

public interface GetBankCallBack {
    void responseFromAsync(List<BankInfo> banks);
}
