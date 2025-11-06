package com.Timbua.backend.repository;

import com.Timbua.backend.model.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Long> {
    Optional<Contractor> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);
    List<Contractor> findByStatus(Contractor.Status status);
    List<Contractor> findByIsVerifiedTrue();
    List<Contractor> findBySpecialization(String specialization);
}