package com.nguyenphuocloc.ltmchatapp.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenphuocloc.ltmchatapp.Entity.Chat;


public interface ChatRepository extends JpaRepository<Chat, Long>{
    // public Optional<Chat> findById(Long id);
    
}
