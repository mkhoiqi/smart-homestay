package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomCategoryRepository extends JpaRepository<RoomCategory, String> {
    public boolean existsByName(String name);
}
