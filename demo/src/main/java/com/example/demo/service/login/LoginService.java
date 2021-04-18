package com.example.demo.service.login;

import java.util.Map;

public interface LoginService {
    Map.Entry<String, Long> loginUser(String username, String password);
}
