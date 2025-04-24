package com.nguyenphuocloc.ltmchatapp.Controller;

import com.nguyenphuocloc.ltmchatapp.Entity.Chat;
import com.nguyenphuocloc.ltmchatapp.Entity.Message;
import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.Models.WebSocketChatMessage;
import com.nguyenphuocloc.ltmchatapp.Repository.ChatRepository;
import com.nguyenphuocloc.ltmchatapp.Repository.MessageRepository;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;
import com.nguyenphuocloc.ltmchatapp.Response.MessageResponse;
import com.nguyenphuocloc.ltmchatapp.Services.WebSocketSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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

    // Them nguoi dung vao phong chat
    @MessageMapping("/chat/{roomId}/addUser")
    public MessageResponse addUser(@DestinationVariable Long roomId,
                                   @Payload WebSocketChatMessage chatMessage,
                                   SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal == null) {
            logger.warn("Nguoi dung chua xac thuc khi tham gia phong {}", roomId);
            return new MessageResponse(null, "Ban can dang nhap de tham gia phong", new Date(), roomId, null);
        }

        String username = principal.getName();
        User joiningUser = userRepository.findByUsername(username).orElse(null);
        if (joiningUser == null) {
            logger.warn("Khong tim thay nguoi dung trong DB: {}", username);
            return new MessageResponse(null, "Khong tim thay nguoi dung trong he thong", new Date(), roomId, null);
        }

        logger.info("Nguoi dung '{}' (ID: {}) da tham gia phong {}", joiningUser.getUsername(), joiningUser.getId(), roomId);

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
    public void handleWebSocketDisconnect(@DestinationVariable Long roomId,
                                        SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                String sessionId = headerAccessor.getSessionId(); // Lấy session ID chính xác

                logger.info("Nguoi dung '{}' roi khoi phong {}", username, roomId);

                sessionService.removeUserFromRoom(sessionId);

                MessageResponse messageResponse = new MessageResponse(
                        null,
                        user.getFullname() + " da roi khoi phong",
                        new Date(),
                        roomId,
                        user.getId()
                );

                messagingTemplate.convertAndSend(String.format("/topic/chat/%d", roomId), messageResponse);
            }
        } else {
            logger.warn("Khong xac thuc khi roi khoi phong {}", roomId);
        }
    }


    // Gui tin nhan vao phong chat
    @MessageMapping("/chat/{roomId}/sendMessage")
    @Transactional
    public MessageResponse sendMessage(@DestinationVariable Long roomId,
                                    @Payload WebSocketChatMessage chatMessage,
                                    SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal == null) {
            logger.warn("Nguoi dung chua xac thuc khi gui tin nhan toi phong {}", roomId);
            return new MessageResponse(null, "Ban can dang nhap de gui tin nhan", new Date(), roomId, null);
        }

        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            logger.warn("Khong tim thay nguoi dung trong DB: {}", username);
            return new MessageResponse(null, "Khong tim thay nguoi dung trong he thong", new Date(), roomId, null);
        }

        Optional<Chat> currentChat = chatRepository.findById(roomId);
        if (currentChat.isEmpty()) {
            logger.error("Phong chat ID {} khong ton tai", roomId);
            return new MessageResponse(null, "Phong chat khong ton tai", new Date(), roomId, null);
        }

        logger.info("Nguoi dung '{}' gui tin nhan toi phong {}: {}", currentUser.getUsername(), roomId, chatMessage.getContent());

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
