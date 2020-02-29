package com.example.webdemo2.controller;

import com.arnold.ssocore.dto.SSOUserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;


@RequestMapping
@Controller
public class IndexController {

    @RequestMapping(value = {"/index","/"})
    public String index(Model model) {
        Subject subject = SecurityUtils.getSubject();
        boolean authenticated = subject.isAuthenticated();
        model.addAttribute("authenticated", authenticated);
        return "index";
    }
}