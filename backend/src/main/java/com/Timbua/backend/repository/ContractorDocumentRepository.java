package com.Timbua.backend.repository;

import com.Timbua.backend.model.ContractorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractorDocumentRepository extends JpaRepository<ContractorDocument, Long> {
    List<ContractorDocument> findByContractorId(Long contractorId);
    List<ContractorDocument> findByContractorIdAndDocumentType(Long contractorId, String documentType);
    List<ContractorDocument> findByStatus(ContractorDocument.Status status);
}