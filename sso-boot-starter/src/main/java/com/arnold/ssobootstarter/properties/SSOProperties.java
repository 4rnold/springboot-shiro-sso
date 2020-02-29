package com.arnold.ssobootstarter.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
//@PropertySource(value = "classpath:application.properties")
//@ConfigurationProperties(prefix = "sso")
public class SSOProperties {

//    @Value("${server:server}")
//    private String ssoServer;

    @Value("${sso.server.url}")
    private String serverUrl;

    @Value("${sso.server.token-verify-path:/api/token-verify}")
    private String ssoServerTokenVerifyPath;

    @Value("${sso.server.login-path:/sso/login}")
    private String ssoServerLoginPath;

    @Value("${sso.server.logout-path:/sso/logout}")
    private String ssoServerLogoutPath;


    @Value("${sso.client.login-path:/client-login}")
    private String ssoClientLoginPath;

    //由server 统一调用
    @Value("${sso.client.logout-path:/client-logout}")
    private String ssoClientLogoutPath;

    //跳转到sso-server
    @Value("${sso.client.redirect-server-logout-path:/logout}")
    private String ssoRedirectLogoutUrl;

    @Value("${sso.client.excluded-paths:}")
    private String ssoExcludedPaths;
}
