package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.exception.CustomException;
import com.rzq.smarthomestay.repository.AdditionalFacilityRepository;
import com.rzq.smarthomestay.repository.RoomCategoryRepository;
import com.rzq.smarthomestay.repository.UserRepository;
import com.rzq.smarthomestay.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class ValidationServiceImpl implements ValidationService {
    @Autowired
    private Validator validator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @Autowired
    private AdditionalFacilityRepository additionalFacilityRepository;

    @Override
    public void validate(Object request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size()>0){
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    @Override
    public User validateToken(String token) {
        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if(user.getTokenExpiredAt()<System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return user;
    }

    @Override
    public void validateIsEmployees(User user) {
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @Override
    public void validateDuplicateUsername(String username) {
        if(userRepository.existsById(username)){
            throw new CustomException(HttpStatus.BAD_REQUEST, "username", "already registered");
        }
    }

    @Override
    public void validateDuplicateRoomCategoryName(String name) {
        if(roomCategoryRepository.existsByName(name)){
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", "already created");
        }
    }

    @Override
    public void validateDuplicateAdditionalFacilityName(String name) {
        if(additionalFacilityRepository.existsByName(name)){
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", "already created");
        }
    }
}
