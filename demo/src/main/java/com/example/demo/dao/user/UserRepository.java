package com.example.demo.dao.user;

import com.example.demo.models.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}