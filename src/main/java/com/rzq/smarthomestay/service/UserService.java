package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.model.UserLoginRequest;
import com.rzq.smarthomestay.model.UserRegisterRequest;
import com.rzq.smarthomestay.model.UserTokenResponse;

public interface UserService {
    public void register(UserRegisterRequest request);

    public void register(String token, UserRegisterRequest request);

    public UserTokenResponse login(UserLoginRequest request);
    public void logout(String token);
}
