package com.bellwin.stock.api;

import com.bellwin.stock.api.dto.AccountResponse;
import com.bellwin.stock.query.BalanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final BalanceQueryService balanceQueryService;

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id) {
        return balanceQueryService.getAccount(id);
    }
}
