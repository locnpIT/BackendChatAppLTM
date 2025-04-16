package com.nguyenphuocloc.ltmchatapp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nguyenphuocloc.ltmchatapp.Entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long>{

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId")
    List<Message> findAllByChatId(@Param("chatId") Long chatId);
    
}
