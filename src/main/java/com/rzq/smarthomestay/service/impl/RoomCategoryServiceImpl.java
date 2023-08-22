package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.RoomCategory;
import com.rzq.smarthomestay.entity.RoomCategoryAudit;
import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.repository.RoomCategoryAuditRepository;
import com.rzq.smarthomestay.repository.RoomCategoryRepository;
import com.rzq.smarthomestay.service.RoomCategoryService;
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
public class RoomCategoryServiceImpl implements RoomCategoryService {

    @Autowired
    ValidationService validationService;

    @Autowired
    RoomCategoryRepository roomCategoryRepository;

    @Autowired
    RoomCategoryAuditRepository roomCategoryAuditRepository;

    @Override
    public RoomCategoryCreateResponse create(String token, RoomCategoryCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        validationService.validateDuplicateRoomCategoryName(request.getName());

        RoomCategory roomCategory = new RoomCategory();
        roomCategory.setId(UUID.randomUUID().toString());
        roomCategory.setName(request.getName());

        roomCategoryRepository.save(roomCategory);

        RoomCategoryAudit roomCategoryAudit = new RoomCategoryAudit();
        roomCategoryAudit.setId(UUID.randomUUID().toString());
        roomCategoryAudit.setName(roomCategory.getName());
        roomCategoryAudit.setCreatedAt(LocalDateTime.now());
        roomCategoryAudit.setRoomCategory(roomCategory);
        roomCategoryAuditRepository.save(roomCategoryAudit);

        return toRoomCategoryCreateResponse(roomCategory);
    }

    @Override
    public RoomCategoryCreateResponse update(String token, String id, RoomCategoryCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        validationService.validateDuplicateRoomCategoryName(request.getName(), id);

        RoomCategory roomCategory = roomCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );

        String oldName = roomCategory.getName();
        roomCategory.setName(request.getName());
        roomCategoryRepository.save(roomCategory);

        RoomCategoryAudit roomCategoryAudit = new RoomCategoryAudit();
        roomCategoryAudit.setId(UUID.randomUUID().toString());
        roomCategoryAudit.setName(request.getName());
        roomCategoryAudit.setCreatedAt(LocalDateTime.now());
        roomCategoryAudit.setRoomCategory(roomCategory);
        roomCategoryAuditRepository.save(roomCategoryAudit);


        return toRoomCategoryCreateResponse(roomCategory);
    }

    @Override
    public RoomCategoryGetDetailsResponse getById(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        System.out.println("ada di sini");

        RoomCategory roomCategory = roomCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );
        System.out.println("roomCategory.getId(): "+roomCategory.getId());
        System.out.println("roomCategory.getAudits(): "+roomCategory.getAudits());
        return toRoomCategoryGetDetailsResponse(roomCategory);
    }

    @Override
    public List<RoomCategoryGetResponse> getAll(String token) {
        User user = validationService.validateToken(token);
        List<RoomCategory> roomCategories = new ArrayList<>();
        if(!user.getIsEmployees()){
            Specification<RoomCategory> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(builder.isNull(root.get("deletedAt")));
                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };
            roomCategories = roomCategoryRepository.findAll(specification);
        } else{
            Specification<RoomCategory> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();

                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };


            roomCategories = roomCategoryRepository.findAll(specification);
        }
        if(roomCategories.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Room Category not found");
        }
        return roomCategories.stream()
                .map(roomCategory -> toRoomCategoryGetResponse(roomCategory))
                .collect(Collectors.toList());
    }

    @Override
    public RoomCategoryGetResponse archive(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        RoomCategory roomCategory = roomCategoryRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );

        roomCategory.setDeletedAt(LocalDateTime.now());
        roomCategoryRepository.save(roomCategory);

        return toRoomCategoryGetResponse(roomCategory);
    }

    @Override
    public RoomCategoryGetResponse publish(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        RoomCategory roomCategory = roomCategoryRepository.findByIdAndDeletedAtIsNotNull(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Category not found")
        );

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

    private RoomCategoryGetDetailsResponse toRoomCategoryGetDetailsResponse(RoomCategory roomCategory){

        Set<RoomCategoryAuditResponse> roomCategoryAuditResponses = new HashSet<>();
        for(RoomCategoryAudit roomCategoryAudit: roomCategory.getAudits()){
            RoomCategoryAuditResponse roomCategoryAuditResponse = new RoomCategoryAuditResponse();
            roomCategoryAuditResponse.setName(roomCategoryAudit.getName());
            roomCategoryAuditResponse.setCreatedAt(roomCategoryAudit.getCreatedAt());
            roomCategoryAuditResponses.add(roomCategoryAuditResponse);
        }

        return RoomCategoryGetDetailsResponse.builder()
                .id(roomCategory.getId())
                .name(roomCategory.getName())
                .deletedAt(roomCategory.getDeletedAt())
                .audits(roomCategoryAuditResponses).build();
    }
}
