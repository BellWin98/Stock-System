package com.bellwin.stock.api;

import com.bellwin.stock.api.dto.QuoteResponse;
import com.bellwin.stock.query.QuoteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteQueryService quoteQueryService;

    @GetMapping("/{symbol}")
    public QuoteResponse getQuote(@PathVariable String symbol) {
        return quoteQueryService.getQuote(symbol);
    }
}
