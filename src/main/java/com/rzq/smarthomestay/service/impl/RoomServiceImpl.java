package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.Facility;
import com.rzq.smarthomestay.entity.Room;
import com.rzq.smarthomestay.entity.RoomCategory;
import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.exception.CustomException;
import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.repository.FacilityRepository;
import com.rzq.smarthomestay.repository.RoomCategoryRepository;
import com.rzq.smarthomestay.repository.RoomRepository;
import com.rzq.smarthomestay.service.RoomService;
import com.rzq.smarthomestay.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    ValidationService validationService;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomCategoryRepository roomCategoryRepository;

    @Autowired
    FacilityRepository facilityRepository;

    @Override
    public RoomCreateResponse create(String token, RoomCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        RoomCategory roomCategory = roomCategoryRepository.findByIdAndDeletedAtIsNull(request.getRoomCategoryId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "room_category_id", "not found")
        );

        Set<Facility> facilities = new HashSet<>();
        for(String facilityId: request.getFacilities()){
            Facility facility = facilityRepository.findByIdAndDeletedAtIsNull(facilityId).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "facilities", "Facility not found")
            );

            facilities.add(facility);
        }

        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setRoomCategory(roomCategory);
        room.setNumberOfRooms(request.getNumberOfRooms());
        room.setPrice(request.getPrice());
        room.setFacilities(facilities);

        roomRepository.save(room);

        return toRoomCreateResponse(room);
    }

    @Override
    public RoomCreateResponse update(String token, String id, RoomCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        RoomCategory roomCategory = roomCategoryRepository.findByIdAndDeletedAtIsNull(request.getRoomCategoryId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "room_category_id", "not found")
        );

        Set<Facility> facilities = new HashSet<>();
        for(String facilityId: request.getFacilities()){
            Facility facility = facilityRepository.findByIdAndDeletedAtIsNull(facilityId).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "facilities", "Facility not found")
            );

            facilities.add(facility);
        }

        Room room = roomRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found")
        );

        room.setRoomCategory(roomCategory);
        room.setNumberOfRooms(request.getNumberOfRooms());
        room.setPrice(request.getPrice());
        room.setFacilities(facilities);

        roomRepository.save(room);

        return toRoomCreateResponse(room);
    }

    @Override
    public RoomGetResponse getById(String token, String id) {
        User user = validationService.validateToken(token);

        if(!user.getIsEmployees()){
            Room room = roomRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found")
            );

            return toRoomGetResponse(room);
        }

        Room room = roomRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found")
        );

        return toRoomGetResponse(room);
    }

    @Override
    public List<RoomGetResponse> getAll(String token, String roomCategoryId) {
        User user = validationService.validateToken(token);

        RoomCategory roomCategory = new RoomCategory();
        System.out.println("INI SERVICENYAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("roomCategoryId: "+roomCategoryId);
        if(Objects.nonNull(roomCategoryId)){
            System.out.println("Sini");
            roomCategory = roomCategoryRepository.findByIdAndDeletedAtIsNull(roomCategoryId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
            );
        }

        System.out.println("LUASRRRRRRRRRRRRR");
        List<Room> rooms = new ArrayList<>();

        if(!user.getIsEmployees()){
            RoomCategory finalRoomCategory = roomCategory;
            Specification<Room> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if(Objects.nonNull(roomCategoryId)){
                    predicates.add(builder.equal(root.get("roomCategory"), finalRoomCategory));
                }

                predicates.add(builder.isNull(root.get("deletedAt")));
                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };
            rooms = roomRepository.findAll(specification);
        } else{
            RoomCategory finalRoomCategory = roomCategory;
            Specification<Room> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if(Objects.nonNull(roomCategoryId)){
                    predicates.add(builder.equal(root.get("roomCategory"), finalRoomCategory));
                }

                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };
            rooms = roomRepository.findAll(specification);
        }

        if(rooms.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        return rooms.stream()
                .map(room -> toRoomGetResponse(room))
                .collect(Collectors.toList());
    }

    @Override
    public RoomGetResponse archive(String token, String id) {
        User user = validationService.validateToken(token);

        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Room room = roomRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found")
        );

        room.setDeletedAt(LocalDateTime.now());
        roomRepository.save(room);
        return toRoomGetResponse(room);
    }

    @Override
    public RoomGetResponse publish(String token, String id) {
        User user = validationService.validateToken(token);

        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Room room = roomRepository.findByIdAndDeletedAtIsNotNull(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found")
        );

        room.setDeletedAt(null);
        roomRepository.save(room);
        return toRoomGetResponse(room);
    }

    private RoomCreateResponse toRoomCreateResponse(Room room){
        RoomCategoryCreateResponse roomCategory = new RoomCategoryCreateResponse();
        roomCategory.setId(room.getRoomCategory().getId());
        roomCategory.setName(room.getRoomCategory().getName());

        Set<FacilityCreateResponse> facilities = new HashSet<>();
        for(Facility facility: room.getFacilities()){
            FacilityCreateResponse facilityCreateResponse = new FacilityCreateResponse();
            facilityCreateResponse.setId(facility.getId());
            facilityCreateResponse.setName(facility.getName());
            facilities.add(facilityCreateResponse);
        }


        return RoomCreateResponse.builder()
                .id(room.getId())
                .roomCategory(roomCategory)
                .numberOfRooms(room.getNumberOfRooms())
                .price(room.getPrice())
                .facilities(facilities).build();
    }

    private RoomGetResponse toRoomGetResponse(Room room){
        RoomCategoryCreateResponse roomCategory = new RoomCategoryCreateResponse();
        roomCategory.setId(room.getRoomCategory().getId());
        roomCategory.setName(room.getRoomCategory().getName());

        Set<FacilityCreateResponse> facilities = new HashSet<>();
        for(Facility facility: room.getFacilities()){
            FacilityCreateResponse facilityCreateResponse = new FacilityCreateResponse();
            facilityCreateResponse.setId(facility.getId());
            facilityCreateResponse.setName(facility.getName());
            facilities.add(facilityCreateResponse);
        }


        return RoomGetResponse.builder()
                .id(room.getId())
                .roomCategory(roomCategory)
                .numberOfRooms(room.getNumberOfRooms())
                .price(room.getPrice())
                .facilities(facilities)
                .deletedAt(room.getDeletedAt()).build();
    }
}
