package com.Timbua.backend.repository;

import com.Timbua.backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByEmail(String email);
    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);
    Optional<Supplier> findByEmail(String email);
    List<Supplier> findByIsVerifiedTrue();
}
