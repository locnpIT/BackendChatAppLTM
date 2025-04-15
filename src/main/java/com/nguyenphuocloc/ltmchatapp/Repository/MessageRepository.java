package com.nguyenphuocloc.ltmchatapp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenphuocloc.ltmchatapp.Entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long>{
    
}
