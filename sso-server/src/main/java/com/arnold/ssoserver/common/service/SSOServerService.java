//package com.arnold.ssoserver.common.service;
//
//import com.arnold.ssocore.dto.ResponseResult;
//import com.arnold.ssocore.dto.ResponseStatusCode;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//import java.util.Set;
//
//import static com.arnold.ssoserver.common.consts.SSOConstant.LOGOUT_CACHE_PREFIX;
//
///**
// * 【废弃】
// * 使用resttemplate通知各子节点logout
// */
//@Service
//public class SSOServerService {
//
//    @Autowired
//    RestTemplate restTemplate;
//
//    @Autowired
//    RedisService redisService;
//
//
//    private Boolean sendLogoutRequest(String logoutLink, String sessionId) {
//        ResponseResult responseResult = restTemplate.postForObject(logoutLink + "?sessionId=" + sessionId, null, ResponseResult.class);
//        if (responseResult.getStatus() == ResponseStatusCode.OK) {
//            return true;
//        }
//        return false;
//    }
//
///*    public void logoutAllClient(String sessionId) {
//        //向各节点发送logout
//        Set<Object> logoutLinks = redisService.sGet(LOGOUT_CACHE_PREFIX + sessionId);
//
//        for (Object logoutLink : logoutLinks) {
//            sendLogoutRequest((String) logoutLink, sessionId);
//        }
//        redisService.del(LOGOUT_CACHE_PREFIX + sessionId);
//    }*/
//
//    public void logoutAllClient(String sessionId) {
//        //向各节点发送logout
//        Map<Object, Object> logoutUrlSessionIdMap = redisService.hmget(LOGOUT_CACHE_PREFIX + sessionId);
//        for (Map.Entry<Object, Object> entry : logoutUrlSessionIdMap.entrySet()) {
//            sendLogoutRequest((String) entry.getKey(), (String) entry.getValue());
//        }
//
////        for (Object logoutLink : logoutLinks) {
////            sendLogoutRequest((String) logoutLink, sessionId);
////        }
//        redisService.del(LOGOUT_CACHE_PREFIX + sessionId);
//    }
//}
