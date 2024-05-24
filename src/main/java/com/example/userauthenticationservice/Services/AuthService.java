package com.example.userauthenticationservice.Services;

import com.example.userauthenticationservice.Repositories.SessionRepository;
import com.example.userauthenticationservice.Repositories.UserRepository;
import com.example.userauthenticationservice.model.Session;
import com.example.userauthenticationservice.model.SessionState;
import com.example.userauthenticationservice.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SessionRepository sessionRepository;

    public User signUp(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return null;
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public Pair<MultiValueMap<String,String>,User> login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
            return null;

        if(!bCryptPasswordEncoder.matches(password,optionalUser.get().getPassword()))
            return null;

        //Token Generation
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email",optionalUser.get().getEmail());
        claims.put("roles",optionalUser.get().getRoleSet());
        claims.put("iat",new Date(System.currentTimeMillis()));
        claims.put("expiry",new Date(System.currentTimeMillis() + 1000000));

        MacAlgorithm algorithm = Jwts.SIG.HS256;
        SecretKey secretKey = algorithm.key().build();

        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, token);

        //Session create and save
        Session session = new Session();
        session.setSessionState(SessionState.ACTIVE);
        session.setUser(optionalUser.get());
        session.setToken(token);
        sessionRepository.save(session);

        return new Pair(headers,optionalUser.get());
    }
}
