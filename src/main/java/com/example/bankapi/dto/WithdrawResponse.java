package com.example.bankapi.dto;

import com.example.bankapi.model.Account;

public class WithdrawResponse {
    private Account origin;

    public WithdrawResponse(Account origin) {
        this.origin = origin;
    }
    public Account getOrigin() { return origin; }

}
