package com.nguyenphuocloc.ltmchatapp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.Response.ReportedMessageUserDTO;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    public Optional<User> findByUsername(String username);

    
    @Query("SELECT u.id AS userId, u.username AS username, u.fullname AS fullname, " +
           "m.id AS messageId, m.contentOfMessage AS contentOfMessage " +
           "FROM User u JOIN u.messages m WHERE m.isReport = true")
    List<ReportedMessageUserDTO> findReportedMessagesWithUserInfo();

}
