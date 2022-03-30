package com.mars.controller;

import com.mars.pojo.Result;
import com.mars.pojo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * .
 * Package Name:   com.yyj.controller
 *
 * @author: YYJ
 * Date Time:      2021/5/24 18:46
 */

@Controller
public class LoginController {

    @Value("${login.test.password}")
    private String password;

    /**
     * 处理用户登录请求.
     * @param user 用户登录数据
     * @param session session对象
     * @return com.yyj.pojo.Result
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public Result login(@RequestBody User user, HttpSession session) {
        Result result = new Result();
        if (user!= null & password != null & password.equals(user.getPassword())) {
             result.setFlag(true);
             result.setMessage("登录成功！");
             session.setAttribute("username", user.getUsername());
         }else {
            result.setMessage("登录失败！");
            result.setFlag(false);
        }
        return result;
    }

    /**
     * 登录成功后跳转到聊天页面.
     */
    @GetMapping(value = "/toChatroom")
    public String toChatroom() {
        return "chat";
    }

    /**
     * 获取登录成功后存放在session域中的值.
     * @param session
     */
    @GetMapping("/getUsername")
    @ResponseBody
    public String getUsername(HttpSession session){
        return (String) session.getAttribute("username");
    }

}
