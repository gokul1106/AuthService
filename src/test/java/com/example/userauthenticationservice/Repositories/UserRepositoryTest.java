package com.example.userauthenticationservice.Repositories;

import com.example.userauthenticationservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void addUsersToDB(){
        User user = new User();
        user.setEmail("gokul");
        user.setPassword(bCryptPasswordEncoder.encode("12345"));
        userRepository.save(user);
    }
}