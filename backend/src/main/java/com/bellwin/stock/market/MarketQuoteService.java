package com.bellwin.stock.market;

import com.bellwin.stock.api.dto.QuoteResponse;
import com.bellwin.stock.domain.Instrument;
import com.bellwin.stock.domain.InstrumentRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketQuoteService {

    private static final String QUOTE_KEY_PREFIX = "quote:";

    private final StringRedisTemplate redisTemplate;
    private final InstrumentRepository instrumentRepository;

    public void updateQuote(String symbol, long lastPrice, int volume) {
        String key = QUOTE_KEY_PREFIX + symbol;
        redisTemplate.opsForHash().put(key, "lastPrice", String.valueOf(lastPrice));
        redisTemplate.opsForHash().put(key, "volume", String.valueOf(volume));
        redisTemplate.opsForHash().put(key, "updatedAt", Instant.now().toString());

        instrumentRepository.findById(symbol).ifPresent(instrument -> {
            instrument.updateLastPrice(lastPrice);
            instrumentRepository.save(instrument);
        });
    }

    public QuoteResponse getQuote(String symbol) {
        String key = QUOTE_KEY_PREFIX + symbol;
        String cachedPrice = (String) redisTemplate.opsForHash().get(key, "lastPrice");
        String cachedVolume = (String) redisTemplate.opsForHash().get(key, "volume");
        String cachedAt = (String) redisTemplate.opsForHash().get(key, "updatedAt");

        Instrument instrument = instrumentRepository.findById(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Unknown symbol: " + symbol));

        long lastPrice = cachedPrice != null ? Long.parseLong(cachedPrice) : instrument.getLastPrice();
        int volume = cachedVolume != null ? Integer.parseInt(cachedVolume) : 0;
        Instant updatedAt = cachedAt != null ? Instant.parse(cachedAt) : Instant.now();

        return new QuoteResponse(symbol, instrument.getName(), lastPrice, volume, updatedAt);
    }
}
