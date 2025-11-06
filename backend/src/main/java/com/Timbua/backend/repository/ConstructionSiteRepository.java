package com.Timbua.backend.repository;

import com.Timbua.backend.model.ConstructionSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConstructionSiteRepository extends JpaRepository<ConstructionSite, Long> {
    @Query("SELECT cs FROM ConstructionSite cs WHERE cs.contractor.id = :contractorId")
    List<ConstructionSite> findByContractorId(@Param("contractorId") Long contractorId);
}