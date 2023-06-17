package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String>, JpaSpecificationExecutor<Facility> {
    public boolean existsByName(String name);
    public boolean existsByNameAndIdNot(String name, String id);
    public Optional<Facility> findByIdAndDeletedAtIsNull(String id);
    public Optional<Facility> findByIdAndDeletedAtIsNotNull(String id);
}
