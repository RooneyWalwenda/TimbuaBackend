package com.Timbua.backend.repository;

import com.Timbua.backend.model.MaterialSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialSiteRepository extends JpaRepository<MaterialSite, Long> {

    List<MaterialSite> findByMaterialContainingIgnoreCase(String material);

    List<MaterialSite> findByMaterialLocationContainingIgnoreCase(String location);

    List<MaterialSite> findByOwnerOfMaterialContainingIgnoreCase(String owner);

    @Query("SELECT DISTINCT ms.material FROM MaterialSite ms")
    List<String> findDistinctMaterials();

    @Query("SELECT DISTINCT ms.materialLocation FROM MaterialSite ms")
    List<String> findDistinctLocations();
}