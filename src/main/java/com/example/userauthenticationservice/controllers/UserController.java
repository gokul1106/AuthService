package com.example.userauthenticationservice.controllers;

import com.example.userauthenticationservice.Dto.UserDto;
import com.example.userauthenticationservice.Repositories.UserRepository;
import com.example.userauthenticationservice.Services.UserService;
import com.example.userauthenticationservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{uid}")
    public UserDto getUserById(@PathVariable Long uid) {
        System.out.println("User controller - getUserById " + uid);
        User user = userService.getUserById(uid);
        return getUserDto(user);
    }

    private UserDto getUserDto(User user) {
        if(user == null)
            return null;

        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoleSet(user.getRoleSet());
        return userDto;
    }
}
