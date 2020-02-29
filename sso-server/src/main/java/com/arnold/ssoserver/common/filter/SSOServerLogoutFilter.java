package com.arnold.ssoserver.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * sso-server logout页面统一调用
 */
@Slf4j
public class SSOServerLogoutFilter extends LogoutFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletResponse res = (HttpServletResponse) response;

        Subject subject = getSubject(request, res);

        // Check if POST only logout is enabled
        if (isPostOnlyLogout()) {

            // check if the current request's method is a POST, if not redirect
            if (!WebUtils.toHttp(request).getMethod().toUpperCase(Locale.ENGLISH).equals("POST")) {
                return onLogoutRequestNotAPost(request, res);
            }
        }

        String redirectUrl = getRedirectUrl(request, res, subject);
        //try/catch added for SHIRO-298:
        try {
            subject.logout();
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }
        //用不用加cors
        //返回了什么东西
//        issueRedirect(request, res, redirectUrl);
        return false;
    }
}
