package com.example.demo.service.user;

import com.example.demo.models.dto.User;
import org.springframework.data.util.Pair;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Map;

public interface UserService {
    User getUserById(Long id, String token);
    Map.Entry<String, Long> getRandomUser();

    Map<String, Long> getAll();

    void validateToken(Long id, String token);

    User updateUsername(Long id, String newUsername, String token);

    User updatePassword(Long id, String newPassword, String token);

    User updatePoints(Long id, Long newPoints, String token);

    User updatePicture(Long id, byte[] newPicture, String token);

    User getUserByUsername(String username, String token);

    User updateUsername(String username, String newUsername, String token);

    User updatePassword(String password, String newPassword, String token);

    User updatePoints(String username, Long newPoints, String token);

    byte[] getPictureById(Long id);
}
