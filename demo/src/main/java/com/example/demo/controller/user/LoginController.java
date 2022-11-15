package com.example.demo.controller.user;

import com.example.demo.service.Impl.user.LoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author Cheng
 */

@RestController
public class LoginController {

    @Autowired
    private LoginServiceImpl loginService;

    @PostMapping ("/user/account/token/")
    public Map<String,String> getToken(@RequestParam Map<String,String> map){
        String username =map.get("username");
        String password = map.get("password");
        return loginService.getToken(username,password);
    }
}
