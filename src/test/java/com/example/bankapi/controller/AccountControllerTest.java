package com.example.bankapi.controller;

import com.example.bankapi.model.Account;
import com.example.bankapi.model.EventRequest;
import com.example.bankapi.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void testReset() {
        ResponseEntity<String> response = accountController.reset();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("OK", response.getBody());
        verify(accountService, times(1)).reset();
    }

    @Test
    void testBalanceExistingAccount() {
        String accountId = "100";
        int balance = 20;
        Account account = new Account(accountId, balance);
        when(accountService.getBalance(accountId)).thenReturn(Optional.of(account));

        ResponseEntity<Object> response = accountController.balance(accountId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(balance, response.getBody());
        verify(accountService, times(1)).getBalance(accountId);
    }

    @Test
    void testBalanceNonExistingAccount() {
        String accountId = "1234";
        when(accountService.getBalance(accountId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = accountController.balance(accountId);

        assertEquals(404, response.getStatusCode().value());
        assertEquals(0, response.getBody());
        verify(accountService, times(1)).getBalance(accountId);
    }

    @Test
    void testEventDeposit() {
        EventRequest req = new EventRequest();
        req.setType("deposit");
        req.setDestination("100");
        req.setAmount(10);
        Account destination = new Account("100", 10);
        when(accountService.deposit("100", 10)).thenReturn(destination);

        ResponseEntity<Object> response = accountController.event(req);

        assertEquals(201, response.getStatusCode().value());
        verify(accountService, times(1)).deposit("100", 10);
    }

    @Test
    void testEventWithdrawExistingAccount() {
        EventRequest req = new EventRequest();
        req.setType("withdraw");
        req.setOrigin("100");
        req.setAmount(5);
        Account origin = new Account("100", 15);
        when(accountService.withdraw("100", 5)).thenReturn(Optional.of(origin));

        ResponseEntity<Object> response = accountController.event(req);

        assertEquals(201, response.getStatusCode().value());
        verify(accountService, times(1)).withdraw("100", 5);
    }

    @Test
    void testEventWithdrawNonExistingAccount() {
        EventRequest req = new EventRequest();
        req.setType("withdraw");
        req.setOrigin("200");
        req.setAmount(10);
        when(accountService.withdraw("200", 10)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = accountController.event(req);

        assertEquals(404, response.getStatusCode().value());
        assertEquals(0, response.getBody());
        verify(accountService, times(1)).withdraw("200", 10);
    }

    @Test
    void testEventTransferExistingAccount() {
        EventRequest req = new EventRequest();
        req.setType("transfer");
        req.setOrigin("100");
        req.setDestination("300");
        req.setAmount(15);
        Account origin = new Account("100", 0);
        Account destination = new Account("300", 15);
        when(accountService.transfer("100", "300", 15)).thenReturn(Optional.of(Map.of("origin", origin, "destination", destination)));

        ResponseEntity<Object> response = accountController.event(req);

        assertEquals(201, response.getStatusCode().value());
        verify(accountService, times(1)).transfer("100", "300", 15);
    }

    @Test
    void testEventTransferNonExistingAccount() {
        EventRequest req = new EventRequest();
        req.setType("transfer");
        req.setOrigin("200");
        req.setDestination("300");
        req.setAmount(15);
        when(accountService.transfer("200", "300", 15)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = accountController.event(req);

        assertEquals(404, response.getStatusCode().value());
        assertEquals(0, response.getBody());
        verify(accountService, times(1)).transfer("200", "300", 15);
    }

    @Test
    void testEventInvalidType() {
        EventRequest req = new EventRequest();
        req.setType("invalid");

        ResponseEntity<Object> response = accountController.event(req);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid operation", response.getBody());
        verifyNoInteractions(accountService);
    }
}
