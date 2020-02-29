package com.arnold.ssoserver.system.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestMapping
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

//    @CrossOrigin(allowCredentials="true",maxAge = 3600)
//    @RequestMapping("/if-login")
//    @ResponseBody
//    public boolean ifLogin() {
//        Subject subject = SecurityUtils.getSubject();
//        //包括cookie登录
//        boolean remembered = subject.isRemembered();
//        return true;
//    }

}
