package com.nguyenphuocloc.ltmchatapp.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nguyenphuocloc.ltmchatapp.Entity.Chat;
import com.nguyenphuocloc.ltmchatapp.Entity.Message;
import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.Repository.ChatRepository;
import com.nguyenphuocloc.ltmchatapp.Repository.MessageRepository;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;
import com.nguyenphuocloc.ltmchatapp.Response.MessageResponse;
import com.nguyenphuocloc.ltmchatapp.Security.CustomUserDetails;

@Controller
@RequestMapping("/api/chat")
public class MessageController {
    
    @Autowired UserRepository userRepository;

    @Autowired ChatRepository chatRepository;

    @Autowired MessageRepository messageRepository;


    @PostMapping("/{roomId}/create")
    public ResponseEntity<?> createMessage(@PathVariable Long roomId,Authentication authentication, @RequestBody Message message){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User currentUser = userRepository.getById(userDetails.getUser().getId());
        
        Optional<Chat> currentChat = chatRepository.findById(roomId);
        if(!currentChat.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found!!!");
        }
        message.setChat(currentChat.get());
        message.setIsReport(false);
        message.setUser(currentUser);
        message = messageRepository.save(message);

        // sau này refactor chuyển vào service
        MessageResponse response = new MessageResponse();
        response.setChatId(roomId);
        response.setContentOfMessage(message.getContentOfMessage());
        response.setMessageId(message.getId());
        response.setCreatedAt(message.getCreatedAt());
        response.setUserId(message.getUser().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    
}
