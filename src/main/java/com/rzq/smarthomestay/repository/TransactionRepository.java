package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.Room;
import com.rzq.smarthomestay.entity.Transaction;
import com.rzq.smarthomestay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {
    public Optional<Transaction> findByIdAndStatusNotIn(String id, List<String> excludedStatuses);
    public Optional<Transaction> findByIdAndCreatedBy(String id, User createdBy);

    @Query("SELECT " +
            "CASE " +
            "WHEN SUM(t.numberOfRooms) IS NULL THEN 0 " +
            "ELSE SUM(t.numberOfRooms) " +
            "END " +
            "FROM Transaction t " +
            "JOIN t.room r " +
            "WHERE t.room = :room " +
            "AND t.status NOT IN :status "+
            "AND :date "+
            "BETWEEN t.checkinDate AND (t.checkoutDate - 1) "+
            "AND t.id NOT IN ( "+
            "SELECT "+
            "t2.id "+
            "FROM Transaction t2 "+
            "WHERE t2.status = 'Waiting Payment' "+
            "AND t2.virtualAccountExpiredAt < :dateTime "+
            ")")
    public Integer countBookedRoom(List<String> status, Room room, LocalDate date, LocalDateTime dateTime);
}
