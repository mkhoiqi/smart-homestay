package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.AdditionalFacility;
import com.rzq.smarthomestay.entity.AdditionalFacilityAudit;
import com.rzq.smarthomestay.entity.Room;
import com.rzq.smarthomestay.entity.RoomAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomAuditRepository extends JpaRepository<RoomAudit, String> {
    @Query("SELECT " +
            "ra " +
            "FROM RoomAudit ra " +
            "JOIN ra.room r " +
            "WHERE ra.room = :room " +
            "AND ra.createdAt < :createdAt "+
            "ORDER BY ra.createdAt DESC ")
    public List<RoomAudit> getRoomAuditBeforeTransaction(Room room, LocalDateTime createdAt);
}
