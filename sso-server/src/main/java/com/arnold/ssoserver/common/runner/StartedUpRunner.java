//package com.arnold.ssoserver.common.runner;
//
//import com.arnold.ssoserver.common.consts.SSOServerConstant;
//import com.arnold.ssoserver.common.properties.FebsProperties;
//import com.arnold.ssoserver.common.service.RedisService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class StartedUpRunner implements ApplicationRunner {
//
//    @Autowired
//    FebsProperties febsProperties;
//
//    @Autowired
//    RedisService redisService;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        //先添加sso server 的logout地址
//        redisService.(SSOServerConstant.LOGOUTURL_KEY, febsProperties.getShiro().getLogoutUrl());
//
///*        InetAddress address = InetAddress.getLocalHost();
//        String url = String.format("http://%s:%s", address.getHostAddress(), port);
//        String loginUrl = febsProperties.getShiro().getLoginUrl();
//        if (StringUtils.isNotBlank(contextPath))
//            url += contextPath;
//        if (StringUtils.isNotBlank(loginUrl))
//            url += loginUrl;
//        if (auto && StringUtils.equalsIgnoreCase(active, "dev")) {
//            String os = System.getProperty("os.name");
//            // 默认为 windows时才自动打开页面
//            if (StringUtils.containsIgnoreCase(os, "windows")) {
//                //使用默认浏览器打开系统登录页
//                Runtime.getRuntime().exec("cmd  /c  start " + url);
//            }
//        }*/
//    }
//}
