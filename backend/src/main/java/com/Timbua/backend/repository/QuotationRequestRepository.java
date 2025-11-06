package com.Timbua.backend.repository;

import com.Timbua.backend.model.QuotationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuotationRequestRepository extends JpaRepository<QuotationRequest, Long> {
    @Query("SELECT qr FROM QuotationRequest qr WHERE qr.contractor.id = :contractorId")
    List<QuotationRequest> findByContractorId(@Param("contractorId") Long contractorId);

    List<QuotationRequest> findBySupplierId(Long supplierId);
}