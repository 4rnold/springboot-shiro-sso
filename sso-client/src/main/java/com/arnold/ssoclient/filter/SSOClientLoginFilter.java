package com.arnold.ssoclient.filter;

import com.arnold.ssoclient.service.UserTokenService;
import com.arnold.ssocore.consts.SSOConstant;
import com.arnold.ssocore.dto.ResponseResult;
import com.arnold.ssocore.dto.ResponseStatusCode;
import com.arnold.ssocore.dto.SSOUserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class SSOClientLoginFilter extends UserFilter {

    private UserTokenService userTokenService;

    //server地址
    private String serverUrl;

    //server token验证路径
    private String serverTokenVerifyPath;

    //server登录路径
    private String serverLoginPath;

    //client登录路径
    private String clientLoginPath;

    //client登出路径
    private String clientLogoutPath;


    public SSOClientLoginFilter(UserTokenService userTokenService, String serverUrl, String serverTokenVerifyPath, String serverLoginPath, String clientLoginPath, String clientLogoutPath) {
        this.userTokenService = userTokenService;
        this.serverUrl = serverUrl;
        this.serverTokenVerifyPath = serverTokenVerifyPath;
        this.serverLoginPath = serverLoginPath;
        this.clientLoginPath = clientLoginPath;
        this.clientLogoutPath = clientLogoutPath;
    }


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        // If principal is not null, then the user is known and should be allowed access.
        return subject.getPrincipal() != null;
    }


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //判断是否是登录地址
        if (isLoginRequest(request, response)) {

            String token = request.getParameter(SSOConstant.REDIRECT_URL_TOKEN_NAME);
            HttpServletRequest req = (HttpServletRequest) request;

            Subject subject = SecurityUtils.getSubject();

            //构造logout url
            String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            //回调logout地址
            String callbackClientLogoutUrl = baseUrl + clientLogoutPath;

            //验证token有效性
            ResponseResult<SSOUserInfoDTO> responseResult = userTokenService.tokenVerify(serverUrl + serverTokenVerifyPath, token, callbackClientLogoutUrl);
            if (responseResult.getStatus() != ResponseStatusCode.OK) {
                return false;
            }
            SSOUserInfoDTO userInfo = responseResult.getData();

            UsernamePasswordToken userToken = new UsernamePasswordToken(userInfo.getUserName(), "");

            Session session = subject.getSession();

            //https://issues.apache.org/jira/browse/SHIRO-170
            //防止session fixation attack，改变sessionid，保留session内容。
            //retain Session attributes to put in the new session after login:
            Map attributes = new LinkedHashMap();
            Collection<Object> keys = session.getAttributeKeys();
            for( Object key : keys) {
                Object value = session.getAttribute(key);
                if (value != null) {
                    attributes.put(key, value);
                }
            }

            session.stop();

            //this will create a new session by default in applications that allow session state:
            subject.login(userToken);

            //restore the attributes:
            session = subject.getSession();
            for( Object key : attributes.keySet() ) {
                session.setAttribute(key, attributes.get(key));
            }


            WebUtils.redirectToSavedRequest(request, response, "/");
            //false不在继续执行
            return false;
        } else {
            return super.onAccessDenied(request, response);
        }
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        //server回调client地址
        String ssoClientLoginCallbackUrl = baseUrl + clientLoginPath;

        String redirectUrl = serverUrl + serverLoginPath + "?" + SSOConstant.REDIRECT_URL_TAG + "=" + ssoClientLoginCallbackUrl;

        WebUtils.issueRedirect(request, response, redirectUrl);
    }
}
