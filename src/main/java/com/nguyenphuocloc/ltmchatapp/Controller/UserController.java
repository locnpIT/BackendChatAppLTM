package com.nguyenphuocloc.ltmchatapp.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;
import com.nguyenphuocloc.ltmchatapp.Request.UserStatus;
import com.nguyenphuocloc.ltmchatapp.Response.ReportedMessageUserDTO;
import com.nguyenphuocloc.ltmchatapp.Response.UserResponse;


@RestController
@RequestMapping("/api/admin")
public class UserController {
    @Autowired UserRepository userRepository;

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserStatus roleOfUser, Authentication authentication){

        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found user with id " + id);
        }
        User currentUser = optionalUser.get();
        currentUser.setRole(roleOfUser.getRole());
        currentUser = userRepository.save(currentUser);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(currentUser.getId());
        userResponse.setFullname(currentUser.getFullname());
        userResponse.setRole(currentUser.getRole());
        userResponse.setUsername(currentUser.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);


    }

    @GetMapping("/reportlist")
    public List<ReportedMessageUserDTO> getReportedMessages() {
        return userRepository.findReportedMessagesWithUserInfo();
    }


}
