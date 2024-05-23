package com.example.userauthenticationservice.controllers;

import com.example.userauthenticationservice.Dto.LoginRequestDto;
import com.example.userauthenticationservice.Dto.LogoutRequestDto;
import com.example.userauthenticationservice.Dto.SignupRequestDto;
import com.example.userauthenticationservice.Dto.UserDto;
import com.example.userauthenticationservice.Services.AuthService;
import com.example.userauthenticationservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequestDto requestDto){
        try{
            User user = authService.signUp(requestDto.getEmail(),requestDto.getPassword());
            return new ResponseEntity<UserDto>(getUserDto(user), HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto requestDto){
        return  null;
    }

    @PostMapping("/logout")
    public ResponseEntity<UserDto> logout(@RequestBody LogoutRequestDto requestDto){
        return null;
    }

    private UserDto getUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoleSet(user.getRoleSet());
        return userDto;
    }
}
