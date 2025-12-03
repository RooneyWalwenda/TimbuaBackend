package com.Timbua.backend.repository;

import com.Timbua.backend.model.QuotationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationRequestRepository extends JpaRepository<QuotationRequest, Long> {

    // FIXED: Use underscore to navigate to id field of contractor
    List<QuotationRequest> findByContractor_Id(Long contractorId);

    // Alternative with @Query
    @Query("SELECT qr FROM QuotationRequest qr WHERE qr.contractor.id = :contractorId")
    List<QuotationRequest> findByContractorId(@Param("contractorId") Long contractorId);

    // Find requests where a specific supplier is invited
    @Query("SELECT qr FROM QuotationRequest qr JOIN qr.invitedSuppliers s WHERE s.id = :supplierId")
    List<QuotationRequest> findByInvitedSuppliersId(@Param("supplierId") Long supplierId);

    // Find pending requests for a supplier
    @Query("SELECT qr FROM QuotationRequest qr JOIN qr.invitedSuppliers s WHERE s.id = :supplierId AND qr.status = 'PENDING'")
    List<QuotationRequest> findPendingRequestsForSupplier(@Param("supplierId") Long supplierId);
}
