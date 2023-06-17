package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.AdditionalFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalFacilityRepository extends JpaRepository<AdditionalFacility, String> {
    public boolean existsByName(String name);
}
