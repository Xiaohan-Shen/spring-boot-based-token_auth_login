package token_based_auth.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import token_based_auth.service.LoginService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@WebFilter(filterName = "loginFilter", urlPatterns = {"/*"})
public class tokenFilter implements Filter {

    // TODO: logout 应该也是需要处于登录状态才能调用的把？
    //  不然一个人可以在一台电脑上疯狂访问登出接口，让一个账号一直无法正常登录。
    private static final String[] LETGO_URI = {"login", "logout", "register"};

    @Autowired
    LoginService loginService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();
        String token = request.getHeader("token");
        if (exist(servletPath, Arrays.asList(LETGO_URI))) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (token != null){
            if (loginService.isTokenValid(token)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            setErrorMsg(response, -1, "Token expired, please log in again");
        } else {
            setErrorMsg(response, -1, "Please log in first");
        }
    }

    private boolean exist(String servletPath, List<String> uri) {
        for (String u : uri) {
            if (servletPath.contains(u)) {
                return true;
            }
        }
        return false;
    }

    private void setErrorMsg(HttpServletResponse response, int code, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", msg);
        JSONObject jsonObj = new JSONObject(map);
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();
            out.append(jsonObj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
