package com.Timbua.backend.repository;

import com.Timbua.backend.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByQuotationRequestId(Long quotationRequestId);
}
