package com.arnold.ssoserver.system.controller;

import com.arnold.ssocore.dto.ResponseResult;
import com.arnold.ssocore.dto.ResponseStatusCode;
import com.arnold.ssocore.dto.SSOUserInfoDTO;
import com.arnold.ssoserver.common.entity.SSOResponse;
import com.arnold.ssoserver.common.properties.SSOProperties;
import com.arnold.ssoserver.common.service.RedisService;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.arnold.ssoserver.common.consts.SSOServerConstant.LOGOUTURL_KEY;

@RequestMapping("/api")
@RestController
public class SSOServerApiController {

    @Autowired
    RedisService redisService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SessionDAO sessionDAO;

    @Autowired
    RememberMeManager rememberMeManager;

    @Autowired
    SSOProperties ssoProperties;

    //server client 相同sessionId
/*    @RequestMapping("/token-verify")
    public ResponseResult<SSOUserInfoDTO> tokenVerify(String token, String clientLogoutUrl) {
        SSOUserInfoDTO ssoUserInfo = (SSOUserInfoDTO) redisService.get(token);
        redisService.del(token);
        if (ssoUserInfo != null) {
            //判断sessionId状态
            Session session = null;
            try {
                session = sessionDAO.readSession(ssoUserInfo.getSessionId());
            } catch (UnknownSessionException e) {
                //失效
                return ResponseResult.fail(ResponseStatusCode.ARGUMENT_EXCEPTION, "sesssion expired", null, null);
            }
            //验证成功
            //保存sessionID：logoutUrls
            redisService.sSet(LOGOUT_CACHE_PREFIX + ssoUserInfo.getSessionId(), clientLogoutUrl);

            return ResponseResult.ok(ssoUserInfo, null);
        } else {
            return ResponseResult.fail(ResponseStatusCode.ARGUMENT_EXCEPTION, "token not found", null, null);
        }
    }*/

    @RequestMapping("/token-verify")
    public ResponseResult<SSOUserInfoDTO> tokenVerify(String token, String clientLogoutUrl) {
        SSOUserInfoDTO ssoUserInfo = (SSOUserInfoDTO) redisService.get(token);
        redisService.del(token);
        if (ssoUserInfo != null) {
            //判断sessionId状态
            Session session = null;
            try {
                session = sessionDAO.readSession(ssoUserInfo.getServerSessionId());
            } catch (UnknownSessionException e) {
                //失效
                return ResponseResult.fail(ResponseStatusCode.ARGUMENT_EXCEPTION, "sesssion expired", null, null);
            }
            //验证成功
            //保存所有client logout地址
            redisService.sSet(LOGOUTURL_KEY, clientLogoutUrl);

            return ResponseResult.ok(ssoUserInfo, null);
        } else {
            return ResponseResult.fail(ResponseStatusCode.ARGUMENT_EXCEPTION, "token not found", null, null);
        }
    }

    /**
     * 要保证先退出sso-server
     */
    @RequestMapping("/logout-urls")
    public SSOResponse getClientLogoutUrls() {
        Set<Object> clientLogoutUrls = redisService.sGet(LOGOUTURL_KEY);
        LinkedHashSet<Object> urls = new LinkedHashSet<>();
        urls.add(ssoProperties.getShiro().getLogoutUrl());
        urls.addAll(clientLogoutUrls);
        return new SSOResponse().success().data(urls);
    }


}
