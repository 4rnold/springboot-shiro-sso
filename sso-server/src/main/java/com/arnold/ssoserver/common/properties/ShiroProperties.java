package com.arnold.ssoserver.common.properties;

import lombok.Data;

@Data
public class ShiroProperties {
    private long sessionTimeout;
    private int cookieTimeout;
    private String anonUrl;
    private String loginUrl;
    private String successUrl;
    private String logoutUrl;
    private String unauthorizedUrl;
}
