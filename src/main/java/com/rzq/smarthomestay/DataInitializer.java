package com.rzq.smarthomestay;

import com.rzq.smarthomestay.entity.User;
import com.rzq.smarthomestay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User();
        user.setName("Admin");
        user.setUsername("admin");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setIsEmployees(true);

        if(!userRepository.existsById(user.getUsername())){
            userRepository.save(user);
        }
    }
}
