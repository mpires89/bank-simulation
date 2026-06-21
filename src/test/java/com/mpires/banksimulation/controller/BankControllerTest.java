package com.mpires.banksimulation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc.perform(post("/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void testGetBalanceForNonExistingAccount() throws Exception {
        mockMvc.perform(get("/balance?account_id=1234"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    public void testCreateAccountWithInitialBalance() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"destination\": {\"id\":\"100\", \"balance\":10}}"));
    }

    @Test
    public void testDepositIntoExistingAccount() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":10}"));

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"destination\": {\"id\":\"100\", \"balance\":20}}"));
    }

    @Test
    public void testGetBalanceForExistingAccount() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":20}"));

        mockMvc.perform(get("/balance?account_id=100"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    public void testWithdrawFromNonExistingAccount() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"withdraw\", \"origin\":\"200\", \"amount\":10}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    public void testWithdrawFromExistingAccount() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":20}"));

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"withdraw\", \"origin\":\"100\", \"amount\":5}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"origin\": {\"id\":\"100\", \"balance\":15}}"));
    }

    @Test
    public void testTransferFromExistingAccount() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":15}"));

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\", \"origin\":\"100\", \"amount\":15, \"destination\":\"300\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"origin\": {\"id\":\"100\", \"balance\":0}, \"destination\": {\"id\":\"300\", \"balance\":15}}"));
    }

    @Test
    public void testTransferFromNonExistingAccount() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\", \"origin\":\"200\", \"amount\":15, \"destination\":\"300\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }
}
