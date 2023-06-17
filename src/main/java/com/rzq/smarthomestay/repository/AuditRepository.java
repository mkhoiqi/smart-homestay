package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, String> {
}
