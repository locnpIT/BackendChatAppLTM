package com.nguyenphuocloc.ltmchatapp.Models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder 
@ToString
public class WebSocketChatMessage {
    private MessageType type; // Loại tin nhắn (CHAT, JOIN, LEAVE)
    private String content;
    private Long senderId;
    private String senderUsername; // Thêm username người gửi
    private Long roomId;
    private Date timestamp;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}