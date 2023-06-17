package com.rzq.smarthomestay.repository;

import com.rzq.smarthomestay.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    public boolean existsByRoomNumber(String roomNumber);
}
