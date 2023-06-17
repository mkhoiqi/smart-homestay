package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.model.UserLoginRequest;
import com.rzq.smarthomestay.model.UserRegisterRequest;
import com.rzq.smarthomestay.model.UserTokenResponse;
import com.rzq.smarthomestay.repository.UserRepository;
import com.rzq.smarthomestay.service.UserService;
import com.rzq.smarthomestay.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    ValidationService validationService;

    @Autowired
    UserRepository userRepository;

    @Override
    public void register(UserRegisterRequest request) {
        validationService.validate(request);
        validationService.validateDuplicateUsername(request.getUsername());

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setUsername(request.getUsername());
        newUser.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        newUser.setIsEmployees(false);

        userRepository.save(newUser);



    }

    @Override
    public void register(String token, UserRegisterRequest request) {
        System.out.println("Masuk service");
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        validationService.validate(request);
        validationService.validateDuplicateUsername(request.getUsername());

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setUsername(request.getUsername());
        System.out.println("Set Password Nih");
        newUser.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        newUser.setIsEmployees(true);

        userRepository.save(newUser);
    }

    @Override
    public UserTokenResponse login(UserLoginRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Username/Password")
        );

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());

            Long tokenExpiredAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24);
            user.setTokenExpiredAt(tokenExpiredAt);

            userRepository.save(user);

            return UserTokenResponse.builder()
                    .token(user.getToken())
                    .tokenExpiredAt(user.getTokenExpiredAt()).build();
        } else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Username/Password");
        }
    }

    @Override
    public void logout(String token) {
        User user = validationService.validateToken(token);
        user.setTokenExpiredAt(null);
        user.setToken(null);

        userRepository.save(user);
    }
}
