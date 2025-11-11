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

    // New methods for county and sub-county queries
    List<MaterialSite> findByCountyContainingIgnoreCase(String county);

    List<MaterialSite> findBySubCountyContainingIgnoreCase(String subCounty);

    List<MaterialSite> findByCountyAndSubCountyContainingIgnoreCase(String county, String subCounty);

    @Query("SELECT DISTINCT ms.county FROM MaterialSite ms WHERE ms.county IS NOT NULL ORDER BY ms.county")
    List<String> findDistinctCounties();

    @Query("SELECT DISTINCT ms.subCounty FROM MaterialSite ms WHERE ms.subCounty IS NOT NULL ORDER BY ms.subCounty")
    List<String> findDistinctSubCounties();

    @Query("SELECT DISTINCT ms.subCounty FROM MaterialSite ms WHERE ms.county = :county AND ms.subCounty IS NOT NULL ORDER BY ms.subCounty")
    List<String> findSubCountiesByCounty(String county);
}
