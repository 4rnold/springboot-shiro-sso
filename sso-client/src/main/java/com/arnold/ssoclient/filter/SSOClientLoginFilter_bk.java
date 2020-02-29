//package com.arnold.ssoclient.filter;
//
//import com.arnold.ssoclient.consts.SSOClientConstant;
//import com.arnold.ssoclient.service.UserTokenService;
//import com.arnold.ssocore.consts.SSOConstant;
//import com.arnold.ssocore.dto.ResponseResult;
//import com.arnold.ssocore.dto.ResponseStatusCode;
//import com.arnold.ssocore.dto.SSOUserInfoDTO;
//import org.apache.http.HttpHeaders;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.subject.SimplePrincipalCollection;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.util.ThreadContext;
//import org.apache.shiro.web.filter.authc.UserFilter;
//import org.apache.shiro.web.subject.WebSubject;
//import org.apache.shiro.web.util.SavedRequest;
//import org.apache.shiro.web.util.WebUtils;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//import static com.arnold.ssocore.consts.SSOConstant.SSO_CLIENT_LOGOUT_PATH;
//import static org.apache.shiro.web.util.WebUtils.SAVED_REQUEST_KEY;
//
///**
// * server client sessionid 一致，但做法不科学。
// * 对应SSOClientThreadContextSessionIdGenerator
// */
//public class SSOClientLoginFilter_bk extends UserFilter {
//
//    private UserTokenService userTokenService;
//
//    private String ssoServerUrl;
//
//    private String ssoServerTokenVerifyPath;
//
//    //server端登录地址
//    private String ssoServerLoginPath;
//
//    private String ssoClientLoginPath;
//
//
//    public SSOClientLoginFilter_bk(UserTokenService userTokenService, String ssoServerUrl, String ssoServerTokenVerifyPath, String ssoServerLoginPath, String ssoClientLoginPath) {
//        this.userTokenService = userTokenService;
//        this.ssoServerUrl = ssoServerUrl;
//        this.ssoServerTokenVerifyPath = ssoServerTokenVerifyPath;
//        this.ssoServerLoginPath = ssoServerLoginPath;
//        this.ssoClientLoginPath = ssoClientLoginPath;
//    }
//
//    @Override
//    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        Subject subject = getSubject(request, response);
//        // If principal is not null, then the user is known and should be allowed access.
//        return subject.getPrincipal() != null;
//    }
//
//    //http://localhost:9091/sso-login?token=0acf4db3-b8dd-47cd-9246-7a30990c5c9e
//    @Override
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        //判断是否是登录地址
//        if (isLoginRequest(request, response)) {
//            //获取之前的subject
//            Subject oldSubject = SecurityUtils.getSubject();
//            Session oldSession = oldSubject.getSession();
//            SavedRequest savedRequestKey = (SavedRequest) oldSession.getAttribute(SAVED_REQUEST_KEY);
//
//            String token = request.getParameter(SSOConstant.REDIRECT_URL_TOKEN_NAME);
//            HttpServletRequest req = (HttpServletRequest) request;
//
//            //构造logout url
//            String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
//            //回调logout地址
//            String callbackClientLogoutUrl = baseUrl + SSO_CLIENT_LOGOUT_PATH;
//
//            //验证token有效性
//            ResponseResult<SSOUserInfoDTO> responseResult = userTokenService.tokenVerify(ssoServerUrl + ssoServerTokenVerifyPath, token, callbackClientLogoutUrl);
//            if (responseResult.getStatus() != ResponseStatusCode.OK) {
//                return false;
//            }
//            SSOUserInfoDTO userInfo = responseResult.getData();
//            //设置server sessionid 到 threadContext，以便SessionIdGenerator获取sessionId
//            ThreadContext.put(SSOClientConstant.SSO_SERVER_SESSION_ID_KEY, userInfo.getSessionId());
//
//            //构造新的subject
//            PrincipalCollection principals = new SimplePrincipalCollection(
//                    userInfo.getUserName(), "ssoFilter");
//            WebSubject.Builder builder = new WebSubject.Builder(request, response);
//            builder.principals(principals);
//            // 已认证
//            builder.authenticated(true);
//            // 设置新的sessionId，与SessionIdGenerator众生成的id一致
//            builder.sessionId(userInfo.getSessionId());
//            WebSubject subject = builder.buildWebSubject();
//            ThreadContext.bind(subject);
//
//            WebUtils.redirectToSavedRequest(request, response, savedRequestKey.getRequestUrl());
//            //false不在继续执行
//            return false;
//        } else {
//            return super.onAccessDenied(request, response);
//        }
//    }
//
//    @Override
//    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
//        HttpServletRequest req = (HttpServletRequest) request;
//        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
//        //server回调client地址
//        String ssoClientLoginCallbackUrl = baseUrl + ssoClientLoginPath;
//
//        String redirectUrl = ssoServerUrl + ssoServerLoginPath + "?" + SSOConstant.REDIRECT_URL_TAG + "=" + ssoClientLoginCallbackUrl;
//
//        WebUtils.issueRedirect(request, response, redirectUrl);
//    }
//}
