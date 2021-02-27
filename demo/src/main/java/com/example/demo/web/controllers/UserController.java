package com.example.demo.web.controllers;

import java.util.List;
import java.util.Random;

import com.example.demo.web.exceptions.UserNotFoundException;
import com.example.demo.dao.UserRepository;
import com.example.demo.models.dto.User;
import com.example.demo.service.user.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/users")
    List<User> all() {
        return userService.getAll();
    }
    // end::get-aggregate-root[]

    @GetMapping("/random")
    User randomUser() {
        return userService.getRandomUser();
    }

    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        return userService.addUser(newUser);
    }

    // Single item

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {
        return userService.replaceUser(id, newUser);
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}