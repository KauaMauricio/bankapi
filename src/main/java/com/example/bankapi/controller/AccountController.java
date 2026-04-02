package com.example.bankapi.controller;

import com.example.bankapi.dto.DepositResponse;
import com.example.bankapi.dto.TransferResponse;
import com.example.bankapi.dto.WithdrawResponse;
import com.example.bankapi.model.EventRequest;
import com.example.bankapi.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        service.reset();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> balance(@RequestParam String account_id) {
        return service.getBalance(account_id)
                .<ResponseEntity<Object>>map(acc -> ResponseEntity.ok(acc.getBalance()))
                .orElse(ResponseEntity.status(404).body(0));
    }

    @PostMapping("/event")
    public ResponseEntity<Object> event(@RequestBody EventRequest req) {
        switch (req.getType()) {
            case "deposit":
                var dest = service.deposit(req.getDestination(), req.getAmount());
                return ResponseEntity.status(201).body(new DepositResponse(dest));

            case "withdraw":
                return service.withdraw(req.getOrigin(), req.getAmount())
                        .<ResponseEntity<Object>>map(acc -> ResponseEntity.status(201).body(new WithdrawResponse(acc)))
                        .orElse(ResponseEntity.status(404).body(0));

            case "transfer":
                return service.transfer(req.getOrigin(), req.getDestination(), req.getAmount())
                        .<ResponseEntity<Object>>map(map -> ResponseEntity.status(201).body(
                                new TransferResponse(map.get("origin"), map.get("destination"))
                        ))
                        .orElse(ResponseEntity.status(404).body(0));

            default:
                return ResponseEntity.badRequest().body("Invalid operation");
        }
    }


}
