package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.AdditionalFacility;
import com.rzq.smarthomestay.entity.AdditionalFacilityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdditionalFacilityAuditRepository extends JpaRepository<AdditionalFacilityAudit, String> {
    @Query("SELECT " +
            "afa " +
            "FROM AdditionalFacilityAudit afa " +
            "JOIN afa.additionalFacility af " +
            "WHERE afa.additionalFacility = :additionalFacility " +
            "AND afa.createdAt < :createdAt "+
            "ORDER BY afa.createdAt DESC ")
    public List<AdditionalFacilityAudit> getAdditionalFacilityAuditBeforeTransaction(AdditionalFacility additionalFacility, LocalDateTime createdAt);
}
