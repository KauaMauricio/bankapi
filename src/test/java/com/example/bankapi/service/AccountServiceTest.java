package com.example.bankapi.service;

import com.example.bankapi.model.Account;
import com.example.bankapi.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        reset(accountRepository);
    }

    @Test
    void testReset() {
        accountService.reset();
        verify(accountRepository, times(1)).deleteAll();
    }

    @Test
    void testGetBalanceExistingAccount() {
        String accountId = "123";
        Account account = new Account(accountId, 100);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.getBalance(accountId);

        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetBalanceNonExistingAccount() {
        String accountId = "123";
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.getBalance(accountId);

        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testDepositNewAccount() {
        String accountId = "100";
        int amount = 10;
        Account newAccount = new Account(accountId, amount);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        Account result = accountService.deposit(accountId, amount);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(amount, result.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testDepositExistingAccount() {
        String accountId = "100";
        int initialBalance = 10;
        int depositAmount = 10;
        Account existingAccount = new Account(accountId, initialBalance);
        Account updatedAccount = new Account(accountId, initialBalance + depositAmount);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        Account result = accountService.deposit(accountId, depositAmount);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(initialBalance + depositAmount, result.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testWithdrawExistingAccount() {
        String accountId = "100";
        int initialBalance = 20;
        int withdrawAmount = 5;
        Account existingAccount = new Account(accountId, initialBalance);
        Account updatedAccount = new Account(accountId, initialBalance - withdrawAmount);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        Optional<Account> result = accountService.withdraw(accountId, withdrawAmount);

        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().getId());
        assertEquals(initialBalance - withdrawAmount, result.get().getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testWithdrawNonExistingAccount() {
        String accountId = "200";
        int withdrawAmount = 10;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.withdraw(accountId, withdrawAmount);

        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testTransferExistingAccounts() {
        String originId = "100";
        String destinationId = "300";
        int originInitialBalance = 15;
        int destinationInitialBalance = 0;
        int transferAmount = 15;

        Account originAccount = new Account(originId, originInitialBalance);
        Account destinationAccount = new Account(destinationId, destinationInitialBalance);

        when(accountRepository.findById(originId)).thenReturn(Optional.of(originAccount));
        when(accountRepository.findById(destinationId)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved account

        Optional<Map<String, Account>> result = accountService.transfer(originId, destinationId, transferAmount);

        assertTrue(result.isPresent());
        Map<String, Account> accounts = result.get();
        assertEquals(0, accounts.get("origin").getBalance());
        assertEquals(15, accounts.get("destination").getBalance());

        verify(accountRepository, times(1)).findById(originId);
        verify(accountRepository, times(1)).findById(destinationId);
        verify(accountRepository, times(2)).save(any(Account.class)); // Save origin and destination
    }

    @Test
    void testTransferFromNonExistingOriginAccount() {
        String originId = "200";
        String destinationId = "300";
        int transferAmount = 15;

        when(accountRepository.findById(originId)).thenReturn(Optional.empty());

        Optional<Map<String, Account>> result = accountService.transfer(originId, destinationId, transferAmount);

        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findById(originId);
        verify(accountRepository, never()).findById(destinationId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testTransferToNewDestinationAccount() {
        String originId = "100";
        String destinationId = "300";
        int originInitialBalance = 15;
        int transferAmount = 15;

        Account originAccount = new Account(originId, originInitialBalance);

        when(accountRepository.findById(originId)).thenReturn(Optional.of(originAccount));
        when(accountRepository.findById(destinationId)).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved account

        Optional<Map<String, Account>> result = accountService.transfer(originId, destinationId, transferAmount);

        assertTrue(result.isPresent());
        Map<String, Account> accounts = result.get();
        assertEquals(0, accounts.get("origin").getBalance());
        assertEquals(15, accounts.get("destination").getBalance());

        verify(accountRepository, times(1)).findById(originId);
        verify(accountRepository, times(1)).findById(destinationId);
        verify(accountRepository, times(2)).save(any(Account.class)); // Save origin and new destination
    }
}
