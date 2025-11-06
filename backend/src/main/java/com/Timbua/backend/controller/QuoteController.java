package com.Timbua.backend.controller;

import com.Timbua.backend.model.Quote;
import com.Timbua.backend.service.QuoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "*")
public class QuoteController {

    private final QuoteService service;

    public QuoteController(QuoteService service) {
        this.service = service;
    }

    @PostMapping("/{quotationRequestId}")
    public Quote submitQuote(@PathVariable Long quotationRequestId, @RequestBody Quote quote) {
        return service.submitQuote(quotationRequestId, quote);
    }

    @GetMapping("/request/{quotationRequestId}")
    public List<Quote> getQuotesByRequest(@PathVariable Long quotationRequestId) {
        return service.getQuotesByRequest(quotationRequestId);
    }

    @PutMapping("/{quoteId}/status")
    public Quote updateQuoteStatus(@PathVariable Long quoteId, @RequestParam Quote.Status status) {
        return service.updateQuoteStatus(quoteId, status);
    }
}
