package com.example.demo.service.user;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import com.example.demo.dao.user.UserRepository;
import com.example.demo.web.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final AllUsersDao allUsers;

    public UserServiceImpl(
            UserRepository repository,
            AllUsersDao allUsers
    ) {
        this.repository = repository;
        this.allUsers = allUsers;
    }

    @Override
    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User getRandomUser() {
        List<User> list = repository.findAll();
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public User addUser(User newUser) {
        return repository.save(newUser);
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User updateUser(Long id, User newUser) {
        return repository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    user.setPoints(newUser.getPoints());
                    return repository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Map<String, User> getUsersTokens() {
        return allUsers.getUsersTokens();
    }

    @Override
    public Map<String, String> getAllUsersInfo() {
        return allUsers.getAllUsersInfo();
    }

}
