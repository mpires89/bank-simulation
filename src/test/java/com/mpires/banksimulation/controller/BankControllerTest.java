package com.mpires.banksimulation.controller;

import com.mpires.banksimulation.dto.EventRequest;
import com.mpires.banksimulation.dto.EventResponse;
import com.mpires.banksimulation.entity.Account;
import com.mpires.banksimulation.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankController.class)
public class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Test
    public void testReset() throws Exception {
        doNothing().when(service).reset();
        mockMvc.perform(post("/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void testGetBalanceForNonExistingAccount() throws Exception {
        when(service.getAccount("1234")).thenReturn(null);
        mockMvc.perform(get("/balance?account_id=1234"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    public void testGetBalanceForExistingAccount() throws Exception {
        when(service.getAccount("100")).thenReturn(new Account("100", 20));
        mockMvc.perform(get("/balance?account_id=100"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    public void testCreateAccountWithInitialBalance() throws Exception {
        EventResponse mockResponse = new EventResponse(null, new Account("100", 10));
        when(service.processEvent(any(EventRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\", \"destination\":\"100\", \"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"destination\": {\"id\":\"100\", \"balance\":10}}"));
    }

    @Test
    public void testWithdrawFromNonExistingAccount() throws Exception {
        when(service.processEvent(any(EventRequest.class))).thenReturn(null);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"withdraw\", \"origin\":\"200\", \"amount\":10}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    public void testWithdrawFromExistingAccount() throws Exception {
        EventResponse mockResponse = new EventResponse(new Account("100", 15), null);
        when(service.processEvent(any(EventRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"withdraw\", \"origin\":\"100\", \"amount\":5}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"origin\": {\"id\":\"100\", \"balance\":15}}"));
    }

    @Test
    public void testTransferFromExistingAccount() throws Exception {
        EventResponse mockResponse = new EventResponse(new Account("100", 0), new Account("300", 15));
        when(service.processEvent(any(EventRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\", \"origin\":\"100\", \"amount\":15, \"destination\":\"300\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"origin\": {\"id\":\"100\", \"balance\":0}, \"destination\": {\"id\":\"300\", \"balance\":15}}"));
    }
}
