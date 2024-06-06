package com.example.userauthenticationservice.Services;

import com.example.userauthenticationservice.Repositories.UserRepository;
import com.example.userauthenticationservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id){
        Optional<User> optionalUser = userRepository.findByIdEquals(id);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }
        return null;
    }
}
