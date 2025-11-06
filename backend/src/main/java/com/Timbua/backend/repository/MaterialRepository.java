package com.Timbua.backend.repository;

import com.Timbua.backend.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findBySupplierId(Long supplierId);
    List<Material> findByCategory(String category);
}
