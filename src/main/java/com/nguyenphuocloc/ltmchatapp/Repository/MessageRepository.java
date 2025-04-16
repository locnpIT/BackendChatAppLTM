package com.nguyenphuocloc.ltmchatapp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nguyenphuocloc.ltmchatapp.Entity.Message;
import com.nguyenphuocloc.ltmchatapp.Response.MessageResponse;

public interface MessageRepository extends JpaRepository<Message, Long>{

    @Query("SELECT new com.nguyenphuocloc.ltmchatapp.Response.MessageResponse(" +
       "m.id, m.contentOfMessage, m.createdAt, m.chat.id, m.user.id) " +
       "FROM Message m WHERE m.chat.id = :chatId")
    List<MessageResponse> findAllMessageResponsesByChatId(@Param("chatId") Long chatId);

    
}
