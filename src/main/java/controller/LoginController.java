package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.LoginService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class LoginController {
    @Autowired
    LoginService tokenService;

    @RequestMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        String token = tokenService.authenticate(params.get("username").toString(), params.get("password").toString());
        if (token != null) {
            map.put("token", token);
            map.put("code", 1);
            map.put("message", "Login Successful");
        } else {
            map.put("code", 0);
            map.put("message", "Username or password is incorrect");
        }
    }

    @RequestMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        if (tokenService.register(params.get("username").toString(), params.get("password").toString())){
            map.put("code", 1);
            map.put("message", "Registration complete!");
        } else {
            map.put("code", 0);
            map.put("message", "Username already exists");
        }

    }
}

