package com.nguyenphuocloc.ltmchatapp.Controller;

import com.nguyenphuocloc.ltmchatapp.Entity.Chat;
import com.nguyenphuocloc.ltmchatapp.Entity.Message;
import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.Models.WebSocketChatMessage;
import com.nguyenphuocloc.ltmchatapp.Repository.ChatRepository;
import com.nguyenphuocloc.ltmchatapp.Repository.MessageRepository;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;
import com.nguyenphuocloc.ltmchatapp.Response.MessageResponse;
import com.nguyenphuocloc.ltmchatapp.Security.CustomUserDetails;
import com.nguyenphuocloc.ltmchatapp.Services.WebSocketSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Controller
public class WebSocketChatController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private WebSocketSessionService sessionService;

    @MessageMapping("/chat/{roomId}/addUser")
    public MessageResponse addUser(@DestinationVariable Long roomId, @Payload WebSocketChatMessage chatMessage,
                                   SimpMessageHeaderAccessor headerAccessor, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("nguoi dung chua xac thuc co gan tham gia phong{}", roomId);
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User joiningUser = userDetails.getUser();

        logger.info("Nguoi dung '{}' (ID: {}) Da tham gia phong {}", joiningUser.getUsername(), joiningUser.getId(), roomId);

        String sessionId = headerAccessor.getSessionId();
        sessionService.addUserToRoom(sessionId, String.valueOf(roomId));

        headerAccessor.getSessionAttributes().put("username", joiningUser.getUsername());
        headerAccessor.getSessionAttributes().put("roomId", roomId);

        return new MessageResponse(
                null,
                joiningUser.getFullname() + " da tham gia phong",
                new Date(),
                roomId,
                null
        );
    }

    @MessageMapping("/chat/{roomId}/leaveUser")
    public void handleWebSocketDisconnect(@DestinationVariable Long roomId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String sessionId = authentication.getName();

            logger.info("nguoi dung voi sessionId '{}' roi khoi phong {}", sessionId, roomId);

            sessionService.removeUserFromRoom(sessionId);

            MessageResponse messageResponse = new MessageResponse(
                    null,
                    "User da roi khoi phong",
                    new Date(),
                    roomId,
                    null
            );

            messagingTemplate.convertAndSend(String.format("/topic/chat/%d", roomId), messageResponse);
        } else {
            logger.warn("khong xac thuc da roi khoi phong {}", roomId);
        }
    }

    @MessageMapping("/chat/{roomId}/sendMessage")
    @Transactional
    public MessageResponse sendMessage(@DestinationVariable Long roomId, @Payload WebSocketChatMessage chatMessage, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("nguoi dunng chua xac thuc co gan gui tin nhan toi phong {}", roomId);
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        Optional<Chat> currentChat = chatRepository.findById(roomId);
        if (!currentChat.isPresent()) {
            logger.error("Phong chat ID {} khong ton tai", roomId);
            return null;
        }

        logger.info("Ngưoi dung '{}' gui tin nhan toi phong {}: {}", currentUser.getUsername(), roomId, chatMessage.getContent());

        Message newMessage = new Message();
        newMessage.setUser(currentUser);
        newMessage.setChat(currentChat.get());
        newMessage.setContentOfMessage(chatMessage.getContent());
        newMessage.setCreatedAt(new Date());
        newMessage.setIsReport(false);

        newMessage = messageRepository.save(newMessage);

        MessageResponse response = new MessageResponse(
                newMessage.getId(),
                newMessage.getContentOfMessage(),
                newMessage.getCreatedAt(),
                roomId,
                currentUser.getId()
        );

        messagingTemplate.convertAndSend(String.format("/topic/chat/%d", roomId), response);

        return response;
    }
}

// upload code to githubbbbbb
