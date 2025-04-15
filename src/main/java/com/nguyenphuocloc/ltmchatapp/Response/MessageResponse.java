package com.nguyenphuocloc.ltmchatapp.Response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    private Long messageId;
    
    private String contentOfMessage;

    private Date createdAt;

    private Long chatId;

    private Long userId;
}
