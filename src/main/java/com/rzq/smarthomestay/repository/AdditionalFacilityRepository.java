package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.AdditionalFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdditionalFacilityRepository extends JpaRepository<AdditionalFacility, String>, JpaSpecificationExecutor<AdditionalFacility> {
    public boolean existsByName(String name);
    public boolean existsByNameAndIdNot(String name, String id);
    public Optional<AdditionalFacility> findByIdAndDeletedAtIsNull(String id);
}
