package com.Timbua.backend.repository;

import com.Timbua.backend.model.QuotationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuotationRequestRepository extends JpaRepository<QuotationRequest, Long> {
    List<QuotationRequest> findByContractorId(Long contractorId);
    List<QuotationRequest> findBySupplierId(Long supplierId);
}
