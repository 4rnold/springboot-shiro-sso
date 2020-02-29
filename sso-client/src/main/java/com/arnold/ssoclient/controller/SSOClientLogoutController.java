//package com.arnold.ssoclient.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.session.SessionException;
//import org.apache.shiro.subject.Subject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Slf4j
//public class SSOClientLogoutController {
//
////    @CrossOrigin(allowCredentials="true",maxAge = 3600)
////    @RequestMapping(value = "${sso.client.logout-path:/logout}")
////    public void logout() {
////        Subject subject = SecurityUtils.getSubject();
////        try {
////            subject.logout();
////        } catch (SessionException ise) {
////            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
////        }
////    }
//}
