package com.Timbua.backend.repository;

import com.Timbua.backend.model.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    Optional<SuperAdmin> findByEmail(String email);
}
