package com.Timbua.backend.service;

import com.Timbua.backend.model.Quote;
import com.Timbua.backend.model.QuotationRequest;
import com.Timbua.backend.repository.QuoteRepository;
import com.Timbua.backend.repository.QuotationRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuotationRequestRepository requestRepository;

    public QuoteService(QuoteRepository quoteRepository, QuotationRequestRepository requestRepository) {
        this.quoteRepository = quoteRepository;
        this.requestRepository = requestRepository;
    }

    public Quote submitQuote(Long quotationRequestId, Quote quote) {
        QuotationRequest request = requestRepository.findById(quotationRequestId)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found"));
        quote.setQuotationRequest(request);
        return quoteRepository.save(quote);
    }

    public List<Quote> getQuotesByRequest(Long quotationRequestId) {
        return quoteRepository.findByQuotationRequestId(quotationRequestId);
    }

    public Quote updateQuoteStatus(Long quoteId, Quote.Status status) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));
        quote.setStatus(status);
        return quoteRepository.save(quote);
    }
}
