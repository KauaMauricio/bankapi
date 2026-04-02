package com.example.bankapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testResetEndpoint() throws Exception {
        mockMvc.perform(post("/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void testGetBalanceForNonExistingAccount() throws Exception {
        mockMvc.perform(get("/balance?account_id=1234"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    void testCreateAccountWithInitialBalance() throws Exception {
        String requestBody = "{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":10}";
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(10));
    }

    @Test
    @Sql(statements = {"DELETE FROM Account", "INSERT INTO Account (id, balance) VALUES ('100', 10)"})
    void testDepositIntoExistingAccount() throws Exception {
        String requestBody = "{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":10}";
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(20));
    }

    @Test
    @Sql(statements = {"DELETE FROM Account", "INSERT INTO Account (id, balance) VALUES ('100', 20)"})
    void testGetBalanceForExistingAccount() throws Exception {
        mockMvc.perform(get("/balance?account_id=100"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    void testWithdrawFromNonExistingAccount() throws Exception {
        String requestBody = "{\"type\":\"withdraw\", \"origin\":\"200\", \"amount\":10}";
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    @Sql(statements = {"DELETE FROM Account", "INSERT INTO Account (id, balance) VALUES ('100', 20)"})
    void testWithdrawFromExistingAccount() throws Exception {
        String requestBody = "{\"type\":\"withdraw\", \"origin\":\"100\", \"amount\":5}";
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.id").value("100"))
                .andExpect(jsonPath("$.origin.balance").value(15));
    }

    @Test
    @Sql(statements = {"DELETE FROM Account", "INSERT INTO Account (id, balance) VALUES ('100', 15)"})
    void testTransferFromExistingAccount() throws Exception {
        String requestBody = "{\"type\":\"transfer\", \"origin\":\"100\", \"amount\":15, \"destination\":\"300\"}";
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.id").value("100"))
                .andExpect(jsonPath("$.origin.balance").value(0))
                .andExpect(jsonPath("$.destination.id").value("300"))
                .andExpect(jsonPath("$.destination.balance").value(15));
    }

    @Test
    void testTransferFromNonExistingAccount() throws Exception {
        String requestBody = "{\"type\":\"transfer\", \"origin\":\"200\", \"amount\":15, \"destination\":\"300\"}";
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }
}
