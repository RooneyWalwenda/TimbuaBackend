package com.Timbua.backend.repository;

import com.Timbua.backend.model.SupplierDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierDocumentRepository extends JpaRepository<SupplierDocument, Long> {
    List<SupplierDocument> findBySupplierId(Long supplierId);
}
