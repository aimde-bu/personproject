package com.ssm.controller;

import com.ssm.JWT.JwtUtil;
import com.ssm.dao.UserDao;
import com.ssm.domain.Result;
import com.ssm.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result login(@RequestBody User  user) {

        User responeuser = userDao.login(user.getUsername(), user.getPassword());
        System.out.println(user);
        if (responeuser != null) {
            Map<String,Object> claims = new HashMap<>();
            claims.put("username", responeuser.getUsername());
            String token = jwtUtil.generateToken(claims);
            System.out.println(token);
            return new Result(200, "登录成功",  token );
        } else {
            return new Result(500, "登录失败", null);
        }
    }
}
