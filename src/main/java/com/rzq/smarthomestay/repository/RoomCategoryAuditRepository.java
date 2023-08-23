package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.Room;
import com.rzq.smarthomestay.entity.RoomAudit;
import com.rzq.smarthomestay.entity.RoomCategory;
import com.rzq.smarthomestay.entity.RoomCategoryAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomCategoryAuditRepository extends JpaRepository<RoomCategoryAudit, String> {
    @Query("SELECT " +
            "rca " +
            "FROM RoomCategoryAudit rca " +
            "JOIN rca.roomCategory rc " +
            "WHERE rca.roomCategory = :roomCategory " +
            "AND rca.createdAt < :createdAt "+
            "ORDER BY rca.createdAt DESC ")
    public List<RoomCategoryAudit> getRoomCategoryAuditBeforeTransaction(RoomCategory roomCategory, LocalDateTime createdAt);
}
