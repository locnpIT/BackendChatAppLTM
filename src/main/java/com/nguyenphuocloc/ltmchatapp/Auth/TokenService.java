package com.nguyenphuocloc.ltmchatapp.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.JWT.JwtUtility;

import lombok.Value;

@Service
public class TokenService {
    
    @Autowired JwtUtility jwtUtil;

    @Autowired PasswordEncoder passwordEncoder;


    public AuthResponse generateTokens(User user){
        String accessToken = jwtUtil.generateAccessToken(user);
        
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        

        return authResponse;

    }


}
