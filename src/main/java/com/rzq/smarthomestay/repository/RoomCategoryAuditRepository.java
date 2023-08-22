package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.RoomCategoryAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomCategoryAuditRepository extends JpaRepository<RoomCategoryAudit, String> {
}
