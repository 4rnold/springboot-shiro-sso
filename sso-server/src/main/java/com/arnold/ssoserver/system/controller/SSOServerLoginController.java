package com.arnold.ssoserver.system.controller;


import com.arnold.ssocore.consts.SSOConstant;
import com.arnold.ssocore.dto.SSOUserInfoDTO;
import com.arnold.ssoserver.common.consts.SSOServerConstant;
import com.arnold.ssoserver.common.entity.SSOResponse;
import com.arnold.ssoserver.common.properties.SSOProperties;
import com.arnold.ssoserver.common.properties.ValidateCodeProperties;
import com.arnold.ssoserver.common.service.RedisService;
import com.arnold.ssoserver.common.service.ValidateCodeService;
import com.arnold.ssoserver.system.entity.User;
import com.arnold.ssoserver.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/sso")
public class SSOServerLoginController {

    @Autowired
    ValidateCodeService validateCodeService;

    @Autowired
    private SSOProperties properties;


    @Autowired
    IUserService userService;

    @Autowired
    RedisService redisService;


    /**
     * 只有跳转子站才知道主站有没有登录。
     * @param request
     * @return
     */
    @CrossOrigin(allowCredentials="true",maxAge = 3600)
    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        String redirectUrl = request.getParameter(SSOConstant.REDIRECT_URL_TAG);
        //如果已经登录就跳转
        Subject subject = SecurityUtils.getSubject();
        if (subject.getPrincipal() != null ){
            Serializable id = subject.getSession().getId();
            if (StringUtils.isNotBlank(redirectUrl)) {
                redirectUrl = saveTokenAndReturnRedirectUrlWithToken(redirectUrl);
                mav.setViewName("redirect:" + redirectUrl);
            } else {
                mav.setViewName("redirect:/index");
            }
        } else {
            mav.setViewName("login");
        }
        return mav;
    }

    /**
     * http://server1:8081/xxl-sso-server/login?redirect_url=http://server3:8083/xxl-sso-web-sample-springboot/
     */
    @PostMapping("/login")
    @ResponseBody
    public SSOResponse postLogin(String username, String password, String verifyCode, boolean rememberMe, HttpServletRequest request) {
        //处理有redirectUrl情况
        String redirectUrl = request.getParameter(SSOConstant.REDIRECT_URL_TAG);

        checkLoginCaptcha(request, verifyCode);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken userToken = new UsernamePasswordToken(username, password, rememberMe);
        subject.login(userToken);


        if (StringUtils.isNotBlank(redirectUrl)) {
            redirectUrl = saveTokenAndReturnRedirectUrlWithToken(redirectUrl);
            return new SSOResponse().code(HttpStatus.FOUND).data(redirectUrl);
        }

        return new SSOResponse().success();
    }

    private String saveTokenAndReturnRedirectUrlWithToken(String redirectUrl) {
        String token = UUID.randomUUID().toString();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        SSOUserInfoDTO ssoUserInfoDTO = new SSOUserInfoDTO((String) subject.getSession().getId(), user.getUsername());
        redisService.set(token, ssoUserInfoDTO, 300l);
        redirectUrl = redirectUrl + "?" + SSOConstant.REDIRECT_URL_TOKEN_NAME + "=" + token;
        return redirectUrl;
    }

    /**
     * taobao准备一个controller跨域返回taobao的cookie信息，
     * tmall通过jsonp，获取taobao cookie，前端设置cookie到tmail域名下？
     * 或者直接 taobao controller 拼装 tmall 设置cookie 地址，然后redirect过去。
     * @return
     */

    @CrossOrigin(allowCredentials="true",maxAge = 3600)
    @RequestMapping("/if-login")
    @ResponseBody
    public boolean ifLogin() {
        Subject subject = SecurityUtils.getSubject();
        //包括cookie登录
        boolean auth = subject.isAuthenticated() || subject.isRemembered();
        return auth;
    }

    //构造一个logout页面，cors访问各个client logout
    //todo 增加自定义redirect跳转
    @RequestMapping("/logout")
    public String logout(Model model, HttpServletRequest request) {
        String redirectUrl = request.getParameter(SSOConstant.REDIRECT_URL_TAG);
        model.addAttribute("redirectUrl", redirectUrl);
        return "logout";
    }


    @GetMapping("images/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        createLoginCaptcha(request, response);
    }


    void createLoginCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        ValidateCodeProperties codeProperties = properties.getCode();
        String key = SSOServerConstant.REDIS_LOGIN_CODE_PREFIX + sessionId;
        validateCodeService.createByKey(response, key, codeProperties);
    }

    void checkLoginCaptcha(HttpServletRequest request, String verifyCode) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String key = SSOServerConstant.REDIS_LOGIN_CODE_PREFIX + sessionId;
        validateCodeService.check(key, verifyCode);
    }

    @GetMapping("image/regist-captcha")
    public void registCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        createRegistCaptcha(request, response);
    }


    @RequestMapping("/regist")
    @ResponseBody
    public SSOResponse regist(String username, String password, String verifyCode, HttpServletRequest request) {
        checkRegistCaptcha(request, verifyCode);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userService.createUser(user);
        return new SSOResponse().success();
    }

    void createRegistCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        ValidateCodeProperties codeProperties = properties.getCode();
        String key = SSOServerConstant.REDIS_REGIST_CODE_PREFIX + sessionId;
        validateCodeService.createByKey(response, key, codeProperties);
    }

    void checkRegistCaptcha(HttpServletRequest request, String verifyCode) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String key = SSOServerConstant.REDIS_LOGIN_CODE_PREFIX + sessionId;
        validateCodeService.check(key, verifyCode);
    }
}
