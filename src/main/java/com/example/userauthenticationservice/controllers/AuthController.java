package com.example.userauthenticationservice.controllers;

import com.example.userauthenticationservice.Dto.*;
import com.example.userauthenticationservice.Repositories.SessionRepository;
import com.example.userauthenticationservice.Services.AuthService;
import com.example.userauthenticationservice.model.User;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private SessionRepository sessionRepository;

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
        try{
            Pair<MultiValueMap<String,String>,User> pair = authService.login(requestDto.getEmail(),requestDto.getPassword());
            return new ResponseEntity<>(getUserDto(pair.b), pair.a, HttpStatus.OK);
        }
        catch (Exception ex){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<UserDto> logout(@RequestBody LogoutRequestDto requestDto){
        return null;
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequestDto){
        Boolean isValid = authService.validateToken(validateTokenRequestDto.getToken(), validateTokenRequestDto.getUserId());
        return  new ResponseEntity<>(isValid, HttpStatus.ACCEPTED);
    }

    private UserDto getUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoleSet(user.getRoleSet());
        return userDto;
    }
}
