package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.AdditionalFacilityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalFacilityAuditRepository extends JpaRepository<AdditionalFacilityAudit, String> {
}
