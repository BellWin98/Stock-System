package com.bellwin.stock.config;

import com.bellwin.stock.domain.Account;
import com.bellwin.stock.domain.AccountRepository;
import com.bellwin.stock.domain.Instrument;
import com.bellwin.stock.domain.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final InstrumentRepository instrumentRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (accountRepository.count() == 0) {
            accountRepository.save(Account.builder().cashBalance(10_000_000L).build());
            log.info("Seeded demo account id=1 with 10,000,000 KRW");
        }

        if (instrumentRepository.count() == 0) {
            instrumentRepository.save(Instrument.builder().symbol("005930").name("삼성전자").lastPrice(70_000L).build());
            instrumentRepository.save(Instrument.builder().symbol("000660").name("SK하이닉스").lastPrice(180_000L).build());
            instrumentRepository.save(Instrument.builder().symbol("035420").name("NAVER").lastPrice(210_000L).build());
            instrumentRepository.save(Instrument.builder().symbol("005380").name("현대차").lastPrice(250_000L).build());
            log.info("Seeded instruments");
        }
    }
}
