package com.bellwin.stock.query;

import com.bellwin.stock.api.dto.AccountResponse;
import com.bellwin.stock.domain.Account;
import com.bellwin.stock.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BalanceQueryService {

    private final AccountRepository accountRepository;

    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        return new AccountResponse(
                account.getId(),
                account.getCashBalance(),
                account.getLockedMargin(),
                account.availableBalance()
        );
    }
}
