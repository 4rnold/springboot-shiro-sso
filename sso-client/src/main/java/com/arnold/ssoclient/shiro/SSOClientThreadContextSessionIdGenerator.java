package com.arnold.ssoclient.shiro;

import com.arnold.ssoclient.consts.SSOClientConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.util.ThreadContext;

import java.io.Serializable;

public class SSOClientThreadContextSessionIdGenerator extends JavaUuidSessionIdGenerator {


    @Override
    public Serializable generateId(Session session) {
        String sessionId = (String) ThreadContext.get(SSOClientConstant.SSO_SERVER_SESSION_ID_KEY);
        if (StringUtils.isNotBlank(sessionId)) {
            return sessionId;
        } else {
            return super.generateId(session);
        }
    }
}
