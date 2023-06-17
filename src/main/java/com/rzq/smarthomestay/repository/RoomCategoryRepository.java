package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.RoomCategory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomCategoryRepository extends JpaRepository<RoomCategory, String>, JpaSpecificationExecutor<RoomCategory> {
    public boolean existsByName(String name);

    public boolean existsByNameAndIdNot(String name, String id);

    public Optional<RoomCategory> findByIdAndDeletedAtIsNull(String id);

    public Optional<RoomCategory> findByIdAndDeletedAtIsNotNull(String id);

}
