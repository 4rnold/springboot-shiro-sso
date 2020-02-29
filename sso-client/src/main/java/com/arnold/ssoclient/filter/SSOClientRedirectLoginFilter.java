package com.arnold.ssoclient.filter;

import com.arnold.ssoclient.consts.SSOClientConstant;
import org.apache.http.HttpHeaders;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

// todo 增加redirect参数，登录后跳转
public class SSOClientRedirectLoginFilter extends AdviceFilter {
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        boolean authenticated = subject.isAuthenticated();
        HttpServletRequest req = (HttpServletRequest) request;
        String referer = req.getHeader(HttpHeaders.REFERER);
        //没有登录，保存referer。已经登录，跳转referer。session为空跳转/。
        if (authenticated == false) {
            session.setAttribute(SSOClientConstant.SSO_REFERER_URL_SESSION_KEY, referer);
            return true;
        } else {
            String refererUrl = (String) session.getAttribute(SSOClientConstant.SSO_REFERER_URL_SESSION_KEY);

            WebUtils.issueRedirect(request, response, refererUrl);
            return false;
        }
    }
}
