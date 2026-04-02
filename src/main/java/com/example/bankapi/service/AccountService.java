package com.example.bankapi.service;

import com.example.bankapi.model.Account;
import com.example.bankapi.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public void reset() {
        repository.deleteAll();
    }

    public Optional<Account> getBalance(String id) {
        return repository.findById(id);
    }

    @Transactional
    public Account deposit(String destination, int amount) {
        Account acc = repository.findById(destination).orElse(new Account(destination, 0));
        acc.setBalance(acc.getBalance() + amount);
        return repository.save(acc);
    }

    @Transactional
    public Optional<Account> withdraw(String origin, int amount) {
        return repository.findById(origin).map(acc -> {
            acc.setBalance(acc.getBalance() - amount);
            return repository.save(acc);
        });
    }

    @Transactional
    public Optional<Map<String, Account>> transfer(String origin, String destination, int amount) {
        Optional<Account> fromOpt = repository.findById(origin);
        if (fromOpt.isEmpty()) return Optional.empty();

        Account from = fromOpt.get();
        from.setBalance(from.getBalance() - amount);
        repository.save(from);

        Account to = repository.findById(destination).orElse(new Account(destination, 0));
        to.setBalance(to.getBalance() + amount);
        repository.save(to);

        return Optional.of(Map.of("origin", from, "destination", to));
    }
}
