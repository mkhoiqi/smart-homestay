package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.AdditionalFacility;
import com.rzq.smarthomestay.entity.RoomCategory;
import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.model.AdditionalFacilityCreateRequest;
import com.rzq.smarthomestay.model.AdditionalFacilityCreateResponse;
import com.rzq.smarthomestay.model.AdditionalFacilityGetResponse;
import com.rzq.smarthomestay.repository.AdditionalFacilityRepository;
import com.rzq.smarthomestay.service.AdditionalFacilityService;
import com.rzq.smarthomestay.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdditionalFacilityServiceImpl implements AdditionalFacilityService {

    @Autowired
    ValidationService validationService;

    @Autowired
    AdditionalFacilityRepository additionalFacilityRepository;

    @Override
    public AdditionalFacilityCreateResponse create(String token, AdditionalFacilityCreateRequest request) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        validationService.validate(request);
        validationService.validateDuplicateAdditionalFacilityName(request.getName());

        AdditionalFacility additionalFacility = new AdditionalFacility();
        additionalFacility.setId(UUID.randomUUID().toString());
        additionalFacility.setName(request.getName());
        additionalFacility.setPrice(request.getPrice());

        additionalFacilityRepository.save(additionalFacility);
        return toAdditionalFacilityCreateResponse(additionalFacility);
    }

    @Override
    public AdditionalFacilityCreateResponse update(String token, String id, AdditionalFacilityCreateRequest request) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        validationService.validate(request);
        validationService.validateDuplicateAdditionalFacilityName(request.getName());

        AdditionalFacility additionalFacility = additionalFacilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found")
        );

        additionalFacility.setName(request.getName());
        additionalFacility.setPrice(request.getPrice());

        additionalFacilityRepository.save(additionalFacility);
        return toAdditionalFacilityCreateResponse(additionalFacility);
    }

    @Override
    public AdditionalFacilityGetResponse getById(String token, String id) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

        AdditionalFacility additionalFacility = additionalFacilityRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Additional Facility not found")
        );

        return toAdditionalFacilityGetResponse(additionalFacility);
    }

    @Override
    public AdditionalFacilityGetResponse archive(String token, String id) {
        User user = validationService.validateToken(token);
        validationService.validateIsEmployees(user);

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
        validationService.validateIsEmployees(user);

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
}
