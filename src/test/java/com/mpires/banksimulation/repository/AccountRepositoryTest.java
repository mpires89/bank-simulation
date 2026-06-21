package com.mpires.banksimulation.repository;

import com.mpires.banksimulation.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    public void testSaveAndFindAccount() {
        Account account = new Account("100", 50);
        repository.save(account);

        Optional<Account> foundAccount = repository.findById("100");

        assertTrue(foundAccount.isPresent());
        assertEquals("100", foundAccount.get().getId());
        assertEquals(50, foundAccount.get().getBalance());
    }

    @Test
    public void testDeleteAll() {
        Account account = new Account("100", 50);
        repository.save(account);
        repository.deleteAll();

        Optional<Account> foundAccount = repository.findById("100");
        assertFalse(foundAccount.isPresent());
    }
}
