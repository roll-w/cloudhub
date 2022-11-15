package com.example.demo.service.user;

import java.util.Map;

/**
 * @Author Cheng
 */
public interface LoginService {
    Map<String,String> getToken(String username,String password);
}