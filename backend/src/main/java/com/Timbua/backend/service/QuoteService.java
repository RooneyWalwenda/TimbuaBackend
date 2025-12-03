package com.Timbua.backend.service;

import com.Timbua.backend.model.Quote;
import com.Timbua.backend.model.QuotationRequest;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.repository.QuoteRepository;
import com.Timbua.backend.repository.QuotationRequestRepository;
import com.Timbua.backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuotationRequestRepository requestRepository;
    private final SupplierRepository supplierRepository;

    public QuoteService(QuoteRepository quoteRepository,
                        QuotationRequestRepository requestRepository,
                        SupplierRepository supplierRepository) {
        this.quoteRepository = quoteRepository;
        this.requestRepository = requestRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Quote submitQuote(Long quotationRequestId, Long supplierId, Quote quote) {
        // Get the quotation request
        QuotationRequest request = requestRepository.findById(quotationRequestId)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found"));

        // Get the supplier
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Check if supplier is invited to this request
        boolean isInvited = request.getInvitedSuppliers().stream()
                .anyMatch(s -> s.getId().equals(supplierId));

        if (!isInvited) {
            throw new RuntimeException("Supplier is not invited to this quotation request");
        }

        // Check if supplier already submitted a quote for this request
        boolean alreadySubmitted = quoteRepository.existsByQuotationRequestIdAndSupplierId(
                quotationRequestId, supplierId);

        if (alreadySubmitted) {
            throw new RuntimeException("Supplier already submitted a quote for this request");
        }

        // Set relationships and dates
        quote.setQuotationRequest(request);
        quote.setSupplier(supplier);
        quote.setSubmittedDate(LocalDateTime.now());
        quote.setStatus(Quote.Status.PENDING);

        // Update request status if it's the first quote
        if (request.getStatus() == QuotationRequest.Status.PENDING) {
            request.setStatus(QuotationRequest.Status.QUOTED);
            requestRepository.save(request);
        }

        return quoteRepository.save(quote);
    }

    public List<Quote> getQuotesByRequest(Long quotationRequestId) {
        return quoteRepository.findByQuotationRequestId(quotationRequestId);
    }

    public List<Quote> getQuotesBySupplier(Long supplierId) {
        return quoteRepository.findBySupplierId(supplierId);
    }

    public Quote getQuoteById(Long quoteId) {
        return quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));
    }

    @Transactional
    public Quote acceptQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        // Set quote as accepted
        quote.setStatus(Quote.Status.ACCEPTED);

        // Update request status
        QuotationRequest request = quote.getQuotationRequest();
        request.setStatus(QuotationRequest.Status.ACCEPTED);
        requestRepository.save(request);

        // Reject all other quotes for this request
        List<Quote> otherQuotes = quoteRepository.findByQuotationRequestIdAndIdNot(
                request.getId(), quoteId);

        for (Quote otherQuote : otherQuotes) {
            otherQuote.setStatus(Quote.Status.REJECTED);
            quoteRepository.save(otherQuote);
        }

        return quoteRepository.save(quote);
    }

    @Transactional
    public Quote rejectQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        quote.setStatus(Quote.Status.REJECTED);
        return quoteRepository.save(quote);
    }
}
