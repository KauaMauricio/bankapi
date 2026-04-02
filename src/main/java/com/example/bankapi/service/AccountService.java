package com.example.bankapi.service;

import com.example.bankapi.model.Account;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {
    private final Map<String, Account> accounts = new HashMap<>();

    public void reset() { accounts.clear(); }

    public Optional<Account> getBalance(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public Account deposit(String destination, int amount) {
        Account acc = accounts.getOrDefault(destination, new Account(destination, 0));
        acc.setBalance(acc.getBalance() + amount);
        accounts.put(destination, acc);
        return acc;
    }

    public Optional<Account> withdraw(String origin, int amount) {
        Account acc = accounts.get(origin);
        if (acc == null) return Optional.empty();
        acc.setBalance(acc.getBalance() - amount);
        return Optional.of(acc);
    }

    public Optional<Map<String, Account>> transfer(String origin, String destination, int amount) {
        Account from = accounts.get(origin);
        if (from == null) return Optional.empty();
        from.setBalance(from.getBalance() - amount);
        Account to = accounts.getOrDefault(destination, new Account(destination, 0));
        to.setBalance(to.getBalance() + amount);
        accounts.put(destination, to);
        return Optional.of(Map.of("origin", from, "destination", to));
    }

}
