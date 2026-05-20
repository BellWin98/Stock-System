package com.bellwin.stock.query;

import com.bellwin.stock.api.dto.QuoteResponse;
import com.bellwin.stock.market.MarketQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class QuoteQueryService {

    private final MarketQuoteService marketQuoteService;

    public QuoteResponse getQuote(String symbol) {
        try {
            return marketQuoteService.getQuote(symbol);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
