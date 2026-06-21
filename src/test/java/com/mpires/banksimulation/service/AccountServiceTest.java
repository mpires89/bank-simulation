package com.mpires.banksimulation.service;

import com.mpires.banksimulation.dto.EventRequest;
import com.mpires.banksimulation.dto.EventResponse;
import com.mpires.banksimulation.entity.Account;
import com.mpires.banksimulation.enums.ErrorMessage;
import com.mpires.banksimulation.enums.EventType;
import com.mpires.banksimulation.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    @Test
    public void testReset() {
        service.reset();
        verify(repository, times(1)).deleteAll();
    }

    @Test
    public void testGetAccount() {
        Account mockAccount = new Account("100", 20);
        when(repository.findById("100")).thenReturn(Optional.of(mockAccount));

        Account account = service.getAccount("100");
        assertNotNull(account);
        assertEquals(20, account.getBalance());
    }

    @Test
    public void testProcessDeposit() {
        EventRequest request = new EventRequest(EventType.DEPOSIT, "100", null, 10);
        when(repository.findById("100")).thenReturn(Optional.empty());
        when(repository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        EventResponse response = service.processEvent(request);

        assertNotNull(response.getDestination());
        assertEquals(10, response.getDestination().getBalance());
        verify(repository, times(1)).save(any(Account.class));
    }

    @Test
    public void testProcessWithdrawSuccess() {
        EventRequest request = new EventRequest(EventType.WITHDRAW, null, "100", 5);
        Account existingAccount = new Account("100", 20);
        when(repository.findById("100")).thenReturn(Optional.of(existingAccount));
        when(repository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        EventResponse response = service.processEvent(request);

        assertNotNull(response.getOrigin());
        assertEquals(15, response.getOrigin().getBalance());
        verify(repository, times(1)).save(any(Account.class));
    }

    @Test
    public void testProcessWithdrawNotFound() {
        EventRequest request = new EventRequest(EventType.WITHDRAW, null, "100", 5);
        when(repository.findById("100")).thenReturn(Optional.empty());

        EventResponse response = service.processEvent(request);

        assertNull(response);
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    public void testProcessWithdrawInsufficientBalance() {
        EventRequest request = new EventRequest(EventType.WITHDRAW, null, "100", 50);
        Account existingAccount = new Account("100", 20);
        when(repository.findById("100")).thenReturn(Optional.of(existingAccount));

        EventResponse response = service.processEvent(request);

        assertNull(response);
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    public void testProcessTransferSuccess() {
        EventRequest request = new EventRequest(EventType.TRANSFER, "300", "100", 15);
        Account originAccount = new Account("100", 20);
        when(repository.findById("100")).thenReturn(Optional.of(originAccount));
        when(repository.findById("300")).thenReturn(Optional.empty());
        when(repository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        EventResponse response = service.processEvent(request);

        assertNotNull(response.getOrigin());
        assertNotNull(response.getDestination());
        assertEquals(5, response.getOrigin().getBalance());
        assertEquals(15, response.getDestination().getBalance());
        verify(repository, times(2)).save(any(Account.class));
    }

    @Test
    public void testProcessNegativeAmount() {
        EventRequest request = new EventRequest(EventType.DEPOSIT, "100", null, -10);

        EventResponse response = service.processEvent(request);

        assertNull(response);
    }

    @Test
    public void testProcessZeroAmount() {
        EventRequest request = new EventRequest(EventType.DEPOSIT, "100", null, 0);

        EventResponse response = service.processEvent(request);

        assertNull(response);
    }
}
