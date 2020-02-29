package com.arnold.ssoserver.common.authentication;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.arnold.ssoserver.common.consts.ShiroConstant;
import com.arnold.ssoserver.common.filter.SSOServerLogoutFilter;
import com.arnold.ssoserver.common.properties.SSOProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Autowired
    private SSOProperties ssoProperties;

//    @Autowired
//    private SSOServerService ssoServerService;



    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password:}")
    private String password;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.database:0}")
    private int database;

    /**
     * shiro 中配置 redis 缓存
     *
     * @return RedisManager
     */
    private RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host + ":" + port);
        if (StringUtils.isNotBlank(password))
            redisManager.setPassword(password);
        redisManager.setTimeout(timeout);
        redisManager.setDatabase(database);
        return redisManager;
    }

    private RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }



    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl(ssoProperties.getShiro().getLoginUrl());
        shiroFilterFactoryBean.setSuccessUrl(ssoProperties.getShiro().getSuccessUrl());
        shiroFilterFactoryBean.setUnauthorizedUrl(ssoProperties.getShiro().getUnauthorizedUrl());

        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        filtersMap.put("SSOServerLogoutFilter", new SSOServerLogoutFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //设置免认证url
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(ssoProperties.getShiro().getAnonUrl(), ",");
        for (String anonUrl : anonUrls) {
            filterChainDefinitionMap.put(anonUrl, "anon");
        }
        filterChainDefinitionMap.put(ssoProperties.getShiro().getLogoutUrl(), "SSOServerLogoutFilter");
        filterChainDefinitionMap.put("/**", "user");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }



    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(ssoProperties.getShiro().getSessionTimeout() * 1000L);
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(ShiroConstant.ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(ShiroConstant.HASH_ITERATIONS);
        return hashedCredentialsMatcher;
    }

    @Bean
    ShiroRealm shiroRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return shiroRealm;
    }

    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm, RememberMeManager rememberMeManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager());
        securityManager.setRememberMeManager(rememberMeManager);
        return securityManager;
    }

    private SimpleCookie rememberMeCookie() {
        // 设置 cookie 名称，对应 login.html 页面的 <input type="checkbox" name="rememberMe"/>
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        // 设置 cookie 的过期时间，单位为秒，这里为一天
        cookie.setMaxAge(ssoProperties.getShiro().getCookieTimeout());
        return cookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie 加密的密钥
        String encryptKey = "sso_shiro_key";
        byte[] encryptKeyBytes = encryptKey.getBytes(StandardCharsets.UTF_8);
//        String rememberKey = Base64Utils.encodeToString(Arrays.copyOf(encryptKeyBytes, 16));
        cookieRememberMeManager.setCipherKey(Arrays.copyOf(encryptKeyBytes, 16));
        return cookieRememberMeManager;
    }

    /**
     * 用于开启 Thymeleaf 中的 shiro 标签的使用
     *
     * @return ShiroDialect shiro 方言对象
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }






/*    public static void main(String[] args) {
        String encryptKey = "我们是祖国的花朵";
        byte[] encryptKeyBytes = encryptKey.getBytes(StandardCharsets.UTF_8);
        //cookieRememberMeManager.setCipherKey传入参数为长度16位的byte[]，否则会报Unable to init cipher instance:无法初始化密码实例的错误,后面会提到原因
        String rememberKey = Base64Utils.encodeToString(Arrays.copyOf(encryptKeyBytes, 16));
        System.out.println(rememberKey);
        byte[] decode = Base64.decode(rememberKey);
        String s = new String(decode);
        System.out.println(s);

        String s1 = new String(Arrays.copyOf(encryptKeyBytes, 16));
        System.out.println(s1);
    }*/


//    @Bean
//    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//
//    }

}
