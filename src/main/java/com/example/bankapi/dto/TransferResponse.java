package com.example.bankapi.dto;

import com.example.bankapi.model.Account;

public class TransferResponse {
        private Account origin;
        private Account destination;

        public TransferResponse(Account origin, Account destination) {
            this.origin = origin;
            this.destination = destination;
        }
        public Account getOrigin() { return origin; }
        public Account getDestination() { return destination; }

}
