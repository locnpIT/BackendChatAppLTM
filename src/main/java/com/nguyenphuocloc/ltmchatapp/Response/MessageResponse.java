package com.nguyenphuocloc.ltmchatapp.Response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private Long messageId;
    private String contentOfMessage;
    private Date createdAt;
    private Long chatId;
    private Long userId;
    private String senderFullname; 


    public MessageResponse(Long messageId, String contentOfMessage, Date createdAt, Long chatId, Long userId){
        this.messageId = messageId;
        this.contentOfMessage = contentOfMessage;
        this.createdAt = createdAt;
        this.chatId = chatId;
        this.userId = userId;
    }
}
