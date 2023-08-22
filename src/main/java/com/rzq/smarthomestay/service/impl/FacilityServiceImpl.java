package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.Facility;
import com.rzq.smarthomestay.entity.FacilityAudit;
import com.rzq.smarthomestay.entity.RoomCategory;
import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.repository.FacilityAuditRepository;
import com.rzq.smarthomestay.repository.FacilityRepository;
import com.rzq.smarthomestay.service.FacilityService;
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
public class FacilityServiceImpl implements FacilityService {

    @Autowired
    ValidationService validationService;

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    FacilityAuditRepository facilityAuditRepository;

    @Override
    public FacilityCreateResponse create(String token, FacilityCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        validationService.validateDuplicateFacilityName(request.getName());

        Facility facility = new Facility();
        facility.setId(UUID.randomUUID().toString());
        facility.setName(request.getName());

        facilityRepository.save(facility);


        FacilityAudit facilityAudit = new FacilityAudit();
        facilityAudit.setId(UUID.randomUUID().toString());
        facilityAudit.setName(request.getName());
        facilityAudit.setCreatedAt(LocalDateTime.now());
        facilityAudit.setFacility(facility);
        facilityAuditRepository.save(facilityAudit);

        return toFacilityCreateResponse(facility);
    }

    @Override
    public FacilityCreateResponse update(String token, String id, FacilityCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        validationService.validateDuplicateFacilityName(request.getName(), id);

        Facility facility = facilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        );

        facility.setName(request.getName());
        facilityRepository.save(facility);

        FacilityAudit facilityAudit = new FacilityAudit();
        facilityAudit.setId(UUID.randomUUID().toString());
        facilityAudit.setName(request.getName());
        facilityAudit.setCreatedAt(LocalDateTime.now());
        facilityAudit.setFacility(facility);
        facilityAuditRepository.save(facilityAudit);

        return toFacilityCreateResponse(facility);
    }

    @Override
    public FacilityGetDetailsResponse getById(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Facility facility = facilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        );
        return toFacilityGetDetailsResponse(facility);
    }

    @Override
    public List<FacilityGetResponse> getAll(String token) {
        User user = validationService.validateToken(token);
        List<Facility> facilities = new ArrayList<>();
        if(!user.getIsEmployees()){
            Specification<Facility> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(builder.isNull(root.get("deletedAt")));

                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };
            facilities = facilityRepository.findAll(specification);
        } else{
            Specification<Facility> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();

                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };
            facilities = facilityRepository.findAll(specification);
        }

        if(facilities.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found");
        }

        return facilities.stream()
                .map(facility -> toFacilityGetResponse(facility))
                .collect(Collectors.toList());
    }

    @Override
    public FacilityGetResponse archive(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Facility facility = facilityRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        );

        facility.setDeletedAt(LocalDateTime.now());
        facilityRepository.save(facility);
        return toFacilityGetResponse(facility);
    }

    @Override
    public FacilityGetResponse publish(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Facility facility = facilityRepository.findByIdAndDeletedAtIsNotNull(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        );

        facility.setDeletedAt(null);
        facilityRepository.save(facility);
        return toFacilityGetResponse(facility);
    }

    private FacilityCreateResponse toFacilityCreateResponse(Facility facility){
        return FacilityCreateResponse.builder()
                .id(facility.getId())
                .name(facility.getName()).build();
    }

    private FacilityGetResponse toFacilityGetResponse(Facility facility){
        return FacilityGetResponse.builder()
                .id(facility.getId())
                .name(facility.getName())
                .deletedAt(facility.getDeletedAt()).build();
    }

    private FacilityGetDetailsResponse toFacilityGetDetailsResponse(Facility facility){
        Set<FacilityAuditResponse> facilityAuditResponses = new HashSet<>();

        for(FacilityAudit facilityAudit: facility.getAudits()){
            FacilityAuditResponse facilityAuditResponse = new FacilityAuditResponse();
            facilityAuditResponse.setName(facilityAudit.getName());
            facilityAuditResponse.setCreatedAt(facilityAudit.getCreatedAt());
            facilityAuditResponses.add(facilityAuditResponse);
        }

        return FacilityGetDetailsResponse.builder()
                .id(facility.getId())
                .name(facility.getName())
                .deletedAt(facility.getDeletedAt())
                .audits(facilityAuditResponses).build();
    }
}
