package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.FacilityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityAuditRepository extends JpaRepository<FacilityAudit, String> {
}
