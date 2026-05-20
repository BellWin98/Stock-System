package com.bellwin.stock.api;

import com.bellwin.stock.api.dto.InstrumentResponse;
import com.bellwin.stock.domain.InstrumentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentRepository instrumentRepository;

    @GetMapping
    public List<InstrumentResponse> listInstruments() {
        return instrumentRepository.findAll().stream()
                .map(i -> new InstrumentResponse(i.getSymbol(), i.getName(), i.getLastPrice()))
                .toList();
    }
}
