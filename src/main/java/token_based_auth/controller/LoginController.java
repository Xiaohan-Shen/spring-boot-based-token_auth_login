package token_based_auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import token_based_auth.dao.UserAccountsDao;
import token_based_auth.service.LoginService;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    LoginService tokenService;

    @RequestMapping("/test")
    public Map<String, Object> test(ServletRequest request){
        Map<String, Object> map = new HashMap<>();
        tokenService.refreshToken(request, map);
        map.put("code", "1");
        map.put("message", "test invoked by user");
        return map;
    }

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
        return map;
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
        return map;
    }

    // 是不是只有在登录状态才能调用这个接口？
    @RequestMapping("/logout")
    public Map<String, Object> logout(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        tokenService.deleteToken(params.get("username").toString());
        map.put("code", 1);
        map.put("message", "You are logged out successfully!");
        return map;
    }
}

