package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {
}
