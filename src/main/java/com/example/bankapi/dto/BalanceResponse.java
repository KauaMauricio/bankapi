package com.example.bankapi.dto;

public class BalanceResponse {
    private int balance;

    public BalanceResponse(int balance) {
        this.balance = balance;
    }
    public int getBalance() { return balance; }

}
