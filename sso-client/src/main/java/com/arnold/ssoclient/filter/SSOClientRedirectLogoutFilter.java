package com.arnold.ssoclient.filter;

import com.arnold.ssocore.consts.SSOConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 跳转到统一logout地址
 */
public class SSOClientRedirectLogoutFilter extends AdviceFilter {

    private String serverLogoutUrl;

    public SSOClientRedirectLogoutFilter(String serverLogoutUrl) {
        this.serverLogoutUrl = serverLogoutUrl;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        String redirectUrl = req.getParameter(SSOConstant.REDIRECT_URL_TAG);
        if (StringUtils.isBlank(redirectUrl)) {
            redirectUrl = req.getHeader(HttpHeaders.REFERER);
        }
        String reUrl = serverLogoutUrl + "?" + SSOConstant.REDIRECT_URL_TAG + "=" + redirectUrl;

        WebUtils.issueRedirect(request,response, reUrl);
        return false;
    }
}
