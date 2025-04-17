package com.nguyenphuocloc.ltmchatapp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nguyenphuocloc.ltmchatapp.Entity.Chat;
import com.nguyenphuocloc.ltmchatapp.Response.ChatResponse;


public interface ChatRepository extends JpaRepository<Chat, Long>{
    // public Optional<Chat> findById(Long id);
      @Query("SELECT c.id AS id, c.nameOfChat AS nameOfChat, c.userInChat AS userInChat FROM Chat c")
    List<ChatResponse> getAllChat();
}
