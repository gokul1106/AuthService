package com.example.userauthenticationservice.Services;

import com.example.userauthenticationservice.Dto.EmailDto;
import com.example.userauthenticationservice.Repositories.SessionRepository;
import com.example.userauthenticationservice.Repositories.UserRepository;
import com.example.userauthenticationservice.client.KafkaProducerClient;
import com.example.userauthenticationservice.model.Session;
import com.example.userauthenticationservice.model.SessionState;
import com.example.userauthenticationservice.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.log.Log;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
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
    @Autowired
    private SecretKey secretKey;
    @Autowired
    private KafkaProducerClient kafkaProducerClient;
    @Autowired
    private ObjectMapper objectMapper;

    public User signUp(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return null;
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        EmailDto emailDto = new EmailDto();
        emailDto.setTo(user.getEmail());
        emailDto.setFrom("thats.right.00000@gmail.com");
        emailDto.setSubject("User Registration done successfully");
        emailDto.setBody("Welcome!! Your user is registered successfully.");
        try {
            kafkaProducerClient.sendMessage("sendMail",objectMapper.writeValueAsString(emailDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
        Long currentTimeInMillis = System.currentTimeMillis();
        claims.put("iat",currentTimeInMillis);
        claims.put("expiry", (currentTimeInMillis + 1000000));

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

    public boolean validateToken(String token, Long userId){
        System.out.println("User service values: " + token + "  " + userId);

        Optional<Session> session = sessionRepository.findByTokenAndUser_Id(token,userId);

        if(session.isEmpty()){
            System.out.println("User or session token not found");
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Long expiry = (Long)claims.get("expiry");
        Long currentTimeInMillis = System.currentTimeMillis();

        if(currentTimeInMillis > expiry){
            System.out.println("Session expired");
            return false;
        }

        Optional<User> user = userRepository.findById(userId);
        String userEmail = user.get().getEmail();
        if(!claims.get("email").equals(userEmail)){
            System.out.println("Emails don't match");
            return false;
        }

        return true;
    }
}
