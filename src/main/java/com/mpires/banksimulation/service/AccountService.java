package com.mpires.banksimulation.service;

import com.mpires.banksimulation.dto.EventRequest;
import com.mpires.banksimulation.dto.EventResponse;
import com.mpires.banksimulation.entity.Account;
import com.mpires.banksimulation.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    @Transactional
    public void reset() {
        repository.deleteAll();
    }

    public Account getAccount(String accountId) {
        return repository.findById(accountId).orElse(null);
    }

    @Transactional
    public EventResponse processEvent(EventRequest request) {
        if (request.getAmount() <= 0 ){
            return null;
        }
        switch (request.getType()) {
            case DEPOSIT:
                Account dest = repository.findById(request.getDestination())
                        .orElse(new Account(request.getDestination(), 0));
                dest.setBalance(dest.getBalance() + request.getAmount());
                dest = repository.save(dest);
                return new EventResponse(null, dest);

            case WITHDRAW:
                Account originW = repository.findById(request.getOrigin()).orElse(null);
                if (originW == null || (originW.getBalance() < request.getAmount())) {
                    return null;
                }
                originW.setBalance(originW.getBalance() - request.getAmount());
                originW = repository.save(originW);
                return new EventResponse(originW, null);

            case TRANSFER:
                Account originT = repository.findById(request.getOrigin()).orElse(null);
                if (originT == null || (originT.getBalance() < request.getAmount()))  {
                    return null;
                }
                Account destT = repository.findById(request.getDestination())
                        .orElse(new Account(request.getDestination(), 0));

                originT.setBalance(originT.getBalance() - request.getAmount());
                destT.setBalance(destT.getBalance() + request.getAmount());

                originT = repository.save(originT);
                destT = repository.save(destT);
                return new EventResponse(originT, destT);

            default:
                throw new IllegalArgumentException(com.mpires.banksimulation.enums.ErrorMessage.INVALID_EVENT_TYPE.getMessage());
        }
    }
}
