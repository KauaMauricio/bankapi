package com.example.bankapi.dto;

import com.example.bankapi.model.Account;

public class DepositResponse {
    private Account destination;

    public DepositResponse(Account destination) {
        this.destination = destination;
    }
    public Account getDestination() { return destination; }

}
