package com.mpires.banksimulation.controller;

import com.mpires.banksimulation.dto.EventRequest;
import com.mpires.banksimulation.dto.EventResponse;
import com.mpires.banksimulation.entity.Account;
import com.mpires.banksimulation.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BankController {

    private final AccountService service;

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        service.reset();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/balance")
    public ResponseEntity<String> getBalance(@RequestParam("account_id") String accountId) {
        Account account = service.getAccount(accountId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
        }
        return ResponseEntity.ok(String.valueOf(account.getBalance()));
    }

    @PostMapping("/event")
    public ResponseEntity<?> handleEvent(@RequestBody EventRequest request) {
        EventResponse response = service.processEvent(request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
