package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.RoomAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomAuditRepository extends JpaRepository<RoomAudit, String> {
}
