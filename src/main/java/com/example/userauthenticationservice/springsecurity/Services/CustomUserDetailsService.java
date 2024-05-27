package com.example.userauthenticationservice.springsecurity.Services;

import com.example.userauthenticationservice.Repositories.UserRepository;
import com.example.userauthenticationservice.model.User;
import com.example.userauthenticationservice.springsecurity.models.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ThreadPoolTaskSchedulerBuilder threadPoolTaskSchedulerBuilder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if(optionalUser.isEmpty())
        {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(optionalUser.get());
    }
}
