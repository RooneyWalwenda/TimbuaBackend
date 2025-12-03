package com.Timbua.backend.repository;

import com.Timbua.backend.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findByQuotationRequestId(Long quotationRequestId);

    List<Quote> findBySupplierId(Long supplierId);

    boolean existsByQuotationRequestIdAndSupplierId(Long quotationRequestId, Long supplierId);

    List<Quote> findByQuotationRequestIdAndIdNot(Long quotationRequestId, Long quoteId);

    // Find accepted quote for a request
    List<Quote> findByQuotationRequestIdAndStatus(Long quotationRequestId, Quote.Status status);
}
