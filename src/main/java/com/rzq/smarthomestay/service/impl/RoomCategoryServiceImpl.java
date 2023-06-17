package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.RoomCategory;
import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.model.RoomCategoryCreateRequest;
import com.rzq.smarthomestay.model.RoomCategoryCreateResponse;
import com.rzq.smarthomestay.model.RoomCategoryGetResponse;
import com.rzq.smarthomestay.repository.RoomCategoryRepository;
import com.rzq.smarthomestay.service.RoomCategoryService;
import com.rzq.smarthomestay.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RoomCategoryServiceImpl implements RoomCategoryService {

    @Autowired
    ValidationService validationService;

    @Autowired
    RoomCategoryRepository roomCategoryRepository;

    @Override
    public RoomCategoryCreateResponse create(String token, RoomCategoryCreateRequest request) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        validationService.validate(request);
        validationService.validateDuplicateRoomCategoryName(request.getName());

        RoomCategory roomCategory = new RoomCategory();
        roomCategory.setId(UUID.randomUUID().toString());
        roomCategory.setName(request.getName());

        roomCategoryRepository.save(roomCategory);
        return toRoomCategoryCreateResponse(roomCategory);
    }

    @Override
    public RoomCategoryCreateResponse update(String token, String id, RoomCategoryCreateRequest request) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        validationService.validate(request);
        validationService.validateDuplicateRoomCategoryName(request.getName());

        RoomCategory roomCategory = roomCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );

        roomCategory.setName(request.getName());
        roomCategoryRepository.save(roomCategory);

        return toRoomCategoryCreateResponse(roomCategory);
    }

    @Override
    public RoomCategoryGetResponse getById(String token, String id) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        RoomCategory roomCategory = roomCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );
        return toRoomCategoryGetResponse(roomCategory);
    }

    @Override
    public RoomCategoryGetResponse archive(String token, String id) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        RoomCategory roomCategory = roomCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );

        if(roomCategory.getDeletedAt()!=null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found");
        }

        roomCategory.setDeletedAt(LocalDateTime.now());
        roomCategoryRepository.save(roomCategory);

        return toRoomCategoryGetResponse(roomCategory);
    }

    @Override
    public RoomCategoryGetResponse publish(String token, String id) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        RoomCategory roomCategory = roomCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );

        if(roomCategory.getDeletedAt()==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found");
        }

        roomCategory.setDeletedAt(null);
        roomCategoryRepository.save(roomCategory);

        return toRoomCategoryGetResponse(roomCategory);
    }

    private RoomCategoryCreateResponse toRoomCategoryCreateResponse(RoomCategory roomCategory){
        return RoomCategoryCreateResponse.builder()
                .id(roomCategory.getId())
                .name(roomCategory.getName()).build();
    }

    private RoomCategoryGetResponse toRoomCategoryGetResponse(RoomCategory roomCategory){
        return RoomCategoryGetResponse.builder()
                .id(roomCategory.getId())
                .name(roomCategory.getName())
                .deletedAt(roomCategory.getDeletedAt()).build();
    }
}
