package com.nguyenphuocloc.ltmchatapp.Services;

import org.springframework.stereotype.Service;

import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    
    
}
