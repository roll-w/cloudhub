package com.example.demo.service.user;

import java.util.Map;

/**
 * @Author Cheng
 */
public interface RegisterService {

    Map<String,String> register(String username, String password, String confirmedPassword);
}