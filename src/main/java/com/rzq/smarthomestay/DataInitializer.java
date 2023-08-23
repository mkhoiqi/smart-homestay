package com.rzq.smarthomestay;

import com.rzq.smarthomestay.entity.*;
import com.rzq.smarthomestay.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdditionalFacilityRepository additionalFacilityRepository;

    @Autowired
    private AdditionalFacilityAuditRepository additionalFacilityAuditRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityAuditRepository facilityAuditRepository;

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @Autowired
    private RoomCategoryAuditRepository roomCategoryAuditRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomAuditRepository roomAuditRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //Data User Employee
        User user = new User();
        user.setName("Admin");
        user.setUsername("admin");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setIsEmployees(true);
        if(!userRepository.existsById(user.getUsername())){
            userRepository.save(user);
        }

        //Data User Guest
        User userGuest = new User();
        userGuest.setName("Guest");
        userGuest.setUsername("guest");
        userGuest.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userGuest.setIsEmployees(false);
        if(!userRepository.existsById(userGuest.getUsername())){
            userRepository.save(userGuest);
        }

        //Data Additional Facility
        AdditionalFacility additionalFacility = new AdditionalFacility();
        additionalFacility.setId(UUID.randomUUID().toString());
        additionalFacility.setName("Extra Bed");
        additionalFacility.setPrice(3000l);
        additionalFacilityRepository.save(additionalFacility);

        //Data Additional Facility Audit
        AdditionalFacilityAudit additionalFacilityAudit = new AdditionalFacilityAudit();
        additionalFacilityAudit.setId(UUID.randomUUID().toString());
        additionalFacilityAudit.setName(additionalFacility.getName());
        additionalFacilityAudit.setPrice(additionalFacility.getPrice());
        additionalFacilityAudit.setCreatedAt(LocalDateTime.now());
        additionalFacilityAudit.setAdditionalFacility(additionalFacility);
        additionalFacilityAuditRepository.save(additionalFacilityAudit);

        //Data Facility
        Facility facility = new Facility();
        facility.setId(UUID.randomUUID().toString());
        facility.setName("Kamar Mandi");
        facilityRepository.save(facility);

        //Data Facility Audit
        FacilityAudit facilityAudit = new FacilityAudit();
        facilityAudit.setId(UUID.randomUUID().toString());
        facilityAudit.setName(facility.getName());
        facilityAudit.setCreatedAt(LocalDateTime.now());
        facilityAudit.setFacility(facility);
        facilityAuditRepository.save(facilityAudit);

        //Data Room Category
        RoomCategory roomCategory = new RoomCategory();
        roomCategory.setId(UUID.randomUUID().toString());
        roomCategory.setName("Deluxe");
        roomCategoryRepository.save(roomCategory);

        //Data Room Category Audit
        RoomCategoryAudit roomCategoryAudit = new RoomCategoryAudit();
        roomCategoryAudit.setId(UUID.randomUUID().toString());
        roomCategoryAudit.setName(roomCategory.getName());
        roomCategoryAudit.setCreatedAt(LocalDateTime.now());
        roomCategoryAudit.setRoomCategory(roomCategory);
        roomCategoryAuditRepository.save(roomCategoryAudit);

        //Data Room
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setRoomCategory(roomCategory);
        room.setNumberOfRooms(3);
        room.setPrice(50000l);
        Set<Facility> facilities = new HashSet<>();
        facilities.add(facility);
        room.setFacilities(facilities);
        roomRepository.save(room);

        //Data Room Audit
        RoomAudit roomAudit = new RoomAudit();
        roomAudit.setId(UUID.randomUUID().toString());
        roomAudit.setCreatedAt(LocalDateTime.now());
        roomAudit.setNumberOfRooms(room.getNumberOfRooms());
        roomAudit.setPrice(room.getPrice());
        roomAudit.setRoomCategory(roomCategory);
        roomAudit.setRoom(room);
        roomAudit.setFacilities(facilities);
        roomAuditRepository.save(roomAudit);
    }
}
