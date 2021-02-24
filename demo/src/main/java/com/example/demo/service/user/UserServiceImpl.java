package com.example.demo.service.user;

import com.example.demo.models.dto.User;
import com.example.demo.dao.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public void updateUser(Long id, User user) {

    }

    @Override
    public void deleteUserById(Long id) {

    }
}
