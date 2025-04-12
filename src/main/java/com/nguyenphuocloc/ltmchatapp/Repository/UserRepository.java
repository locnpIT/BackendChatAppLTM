package com.nguyenphuocloc.ltmchatapp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyenphuocloc.ltmchatapp.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> fetchAllUsers();
    

}
