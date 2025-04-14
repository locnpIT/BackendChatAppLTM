package com.nguyenphuocloc.ltmchatapp.Security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired UserRepository userRepo;

    @Override 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> findByUsername =  userRepo.findByUsername(username);
        if(!findByUsername.isPresent()){
            throw new UsernameNotFoundException("No user found with the given username");
        }
        return new CustomUserDetails(findByUsername.get());
        

    }

    

}
