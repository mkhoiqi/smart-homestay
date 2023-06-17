package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.Transaction;
import com.rzq.smarthomestay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {
    public Optional<Transaction> findByIdAndPendingUserAndStatusNotIn(String id, User user, List<String> excludedStatuses);
    public Optional<Transaction> findByIdAndCreatedBy(String id, User createdBy);
}
