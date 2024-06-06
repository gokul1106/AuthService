package com.example.userauthenticationservice.services;

import com.example.userauthenticationservice.Services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @Test
    void signUp() {
        authService.signUp("gokul","12345");
    }
}
