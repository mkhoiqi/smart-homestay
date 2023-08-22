package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.*;
import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.repository.AdditionalFacilityAuditRepository;
import com.rzq.smarthomestay.repository.AdditionalFacilityRepository;
import com.rzq.smarthomestay.service.AdditionalFacilityService;
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
public class AdditionalFacilityServiceImpl implements AdditionalFacilityService {

    @Autowired
    ValidationService validationService;

    @Autowired
    AdditionalFacilityRepository additionalFacilityRepository;

    @Autowired
    AdditionalFacilityAuditRepository additionalFacilityAuditRepository;

    @Override
    public AdditionalFacilityCreateResponse create(String token, AdditionalFacilityCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        validationService.validateDuplicateAdditionalFacilityName(request.getName());

        AdditionalFacility additionalFacility = new AdditionalFacility();
        additionalFacility.setId(UUID.randomUUID().toString());
        additionalFacility.setName(request.getName());
        additionalFacility.setPrice(request.getPrice());

        additionalFacilityRepository.save(additionalFacility);

        AdditionalFacilityAudit additionalFacilityAudit = new AdditionalFacilityAudit();
        additionalFacilityAudit.setId(UUID.randomUUID().toString());
        additionalFacilityAudit.setName(request.getName());
        additionalFacilityAudit.setPrice(request.getPrice());
        additionalFacilityAudit.setCreatedAt(LocalDateTime.now());
        additionalFacilityAudit.setAdditionalFacility(additionalFacility);
        additionalFacilityAuditRepository.save(additionalFacilityAudit);

        return toAdditionalFacilityCreateResponse(additionalFacility);
    }

    @Override
    public AdditionalFacilityCreateResponse update(String token, String id, AdditionalFacilityCreateRequest request) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);
        validationService.validateDuplicateAdditionalFacilityName(request.getName(), id);

        AdditionalFacility additionalFacility = additionalFacilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found")
        );

        additionalFacility.setName(request.getName());
        additionalFacility.setPrice(request.getPrice());

        additionalFacilityRepository.save(additionalFacility);

        AdditionalFacilityAudit additionalFacilityAudit = new AdditionalFacilityAudit();
        additionalFacilityAudit.setId(UUID.randomUUID().toString());
        additionalFacilityAudit.setName(request.getName());
        additionalFacilityAudit.setPrice(request.getPrice());
        additionalFacilityAudit.setCreatedAt(LocalDateTime.now());
        additionalFacilityAudit.setAdditionalFacility(additionalFacility);
        additionalFacilityAuditRepository.save(additionalFacilityAudit);

        return toAdditionalFacilityCreateResponse(additionalFacility);
    }

    @Override
    public AdditionalFacilityGetDetailsResponse getById(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        AdditionalFacility additionalFacility = additionalFacilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found")
        );

        return toAdditionalFacilityGetDetailsResponse(additionalFacility);
    }

    @Override
    public List<AdditionalFacilityGetResponse> getAll(String token) {
        User user = validationService.validateToken(token);
        List<AdditionalFacility> additionalFacilities = new ArrayList<>();

        if(!user.getIsEmployees()){
            Specification<AdditionalFacility> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(builder.isNull(root.get("deletedAt")));

                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };
            additionalFacilities = additionalFacilityRepository.findAll(specification);
        } else{
            Specification<AdditionalFacility> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();

                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            };
            additionalFacilities = additionalFacilityRepository.findAll(specification);
        }

        if(additionalFacilities.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found");
        }

        return additionalFacilities.stream()
                .map(additionalFacility -> toAdditionalFacilityGetResponse(additionalFacility))
                .collect(Collectors.toList());
    }

    @Override
    public AdditionalFacilityGetResponse archive(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        AdditionalFacility additionalFacility = additionalFacilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found")
        );

        if(additionalFacility.getDeletedAt()!=null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found");
        }

        additionalFacility.setDeletedAt(LocalDateTime.now());
        additionalFacilityRepository.save(additionalFacility);
        return toAdditionalFacilityGetResponse(additionalFacility);
    }

    @Override
    public AdditionalFacilityGetResponse publish(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        AdditionalFacility additionalFacility = additionalFacilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found")
        );

        if(additionalFacility.getDeletedAt()==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found");
        }

        additionalFacility.setDeletedAt(null);
        additionalFacilityRepository.save(additionalFacility);
        return toAdditionalFacilityGetResponse(additionalFacility);
    }

    private AdditionalFacilityCreateResponse toAdditionalFacilityCreateResponse(AdditionalFacility additionalFacility){
        return AdditionalFacilityCreateResponse.builder()
                .id(additionalFacility.getId())
                .name(additionalFacility.getName())
                .price(additionalFacility.getPrice()).build();
    }
    private AdditionalFacilityGetResponse toAdditionalFacilityGetResponse(AdditionalFacility additionalFacility){
        return AdditionalFacilityGetResponse.builder()
                .id(additionalFacility.getId())
                .name(additionalFacility.getName())
                .price(additionalFacility.getPrice())
                .deletedAt(additionalFacility.getDeletedAt()).build();
    }
    private AdditionalFacilityGetDetailsResponse toAdditionalFacilityGetDetailsResponse(AdditionalFacility additionalFacility){
        Set<AdditionalFacilityAuditResponse> additionalFacilityAuditResponses = new HashSet<>();

        for(AdditionalFacilityAudit additionalFacilityAudit: additionalFacility.getAudits()){
            AdditionalFacilityAuditResponse additionalFacilityAuditResponse = new AdditionalFacilityAuditResponse();
            additionalFacilityAuditResponse.setName(additionalFacilityAudit.getName());
            additionalFacilityAuditResponse.setPrice(additionalFacilityAudit.getPrice());
            additionalFacilityAuditResponse.setCreatedAt(additionalFacilityAudit.getCreatedAt());
            additionalFacilityAuditResponses.add(additionalFacilityAuditResponse);
        }

        return AdditionalFacilityGetDetailsResponse.builder()
                .id(additionalFacility.getId())
                .name(additionalFacility.getName())
                .price(additionalFacility.getPrice())
                .deletedAt(additionalFacility.getDeletedAt())
                .audits(additionalFacilityAuditResponses).build();
    }
}
