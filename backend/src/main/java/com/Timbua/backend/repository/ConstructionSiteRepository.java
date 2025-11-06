package com.Timbua.backend.repository;

import com.Timbua.backend.model.ConstructionSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstructionSiteRepository extends JpaRepository<ConstructionSite, Long> {
}
