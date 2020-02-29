package com.arnold.ssobootstarter.autoConfigure;

import com.arnold.ssobootstarter.properties.SSOProperties;
import com.arnold.ssoclient.Realm.SSOClientRealm;
import com.arnold.ssoclient.filter.SSOClientLoginFilter;
import com.arnold.ssoclient.filter.SSOClientRedirectLoginFilter;
import com.arnold.ssoclient.filter.SSOClientCORSLogoutFilter;
import com.arnold.ssoclient.filter.SSOClientRedirectLogoutFilter;
import com.arnold.ssoclient.service.UserTokenService;
import com.arnold.ssoclient.shiro.SSOClientThreadContextSessionIdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"com.arnold.ssoclient.service", "com.arnold.ssobootstarter.properties","com.arnold.ssoclient.controller"})
@ConditionalOnProperty(prefix = "sso", name = "enable", havingValue = "true")
@Import(RestTemplateConfig.class)
//@EnableConfigurationProperties(SSOProperties.class)
public class SSOAutoConfiguration {

    @Autowired
    private SSOProperties ssoProperties;


    @Value("${sso.redis.host:}")
    private String host;
    @Value("${sso.redis.port:6379}")
    private int port;
    @Value("${sso.redis.password:}")
    private String password;
    @Value("${sso.redis.timeout:5000}")
    private int timeout;
    @Value("${sso.redis.database:0}")
    private int database;

    @Value("${sso.session.timeoutsec:-1}")
    private int sessionTimeoutSec;
    @Value("${sso.cookie.timeoutsec:-1}")
    private int cookieTimeoutSec;


    @Bean
    SSOClientRealm ssoRealm() {
        return new SSOClientRealm();
    }


    @Bean
    @ConditionalOnProperty(prefix = "sso.redis", name = "host")
    public SecurityManager securityManager(SSOClientRealm ssoClientRealm, CacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(ssoClientRealm);
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityManager.class)
    public SecurityManager defaultSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(ssoRealm());
        securityManager.setSessionManager(defaultWebSessionManager());
        return securityManager;
    }

    private DefaultWebSessionManager defaultWebSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(sessionTimeoutSec * 1000L);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        MemorySessionDAO sessionDAO = (MemorySessionDAO) sessionManager.getSessionDAO();
        sessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return sessionManager;
    }

    private SessionIdGenerator sessionIdGenerator() {
        return new SSOClientThreadContextSessionIdGenerator();
    }


    /**
     * shiro 中配置 redis 缓存
     *
     * @return RedisManager
     */
    @ConditionalOnProperty(prefix = "sso.redis", name = "host")
    public RedisManager redisManager() {

        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host + ":" + port);
        if (StringUtils.isNotBlank(password))
            redisManager.setPassword(password);
        redisManager.setTimeout(timeout);
        redisManager.setDatabase(database);
        return redisManager;
    }

    @Bean
    @ConditionalOnBean(RedisManager.class)
    public RedisCacheManager reidsCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    @Bean
    @ConditionalOnBean(RedisCacheManager.class)
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return redisSessionDAO;
    }


    @Bean
    @ConditionalOnBean(RedisSessionDAO.class)
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(sessionTimeoutSec * 1000L);
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }


    @Autowired
    UserTokenService userTokenService;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl(ssoProperties.getSsoClientLoginPath());
        shiroFilterFactoryBean.setSuccessUrl("/index");//只对AuthenticationFilter及其父类有用
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");

        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        filtersMap.put("SSOClientLoginFilter", new SSOClientLoginFilter(userTokenService, ssoProperties.getServerUrl(), ssoProperties.getSsoServerTokenVerifyPath(), ssoProperties.getSsoServerLoginPath(), ssoProperties.getSsoClientLoginPath(), ssoProperties.getSsoClientLogoutPath()));

        filtersMap.put("SSOClientLoginRefererFilter", new SSOClientRedirectLoginFilter());

        filtersMap.put("SSOClientRedirectLogoutFilter",new SSOClientRedirectLogoutFilter(ssoProperties.getServerUrl() + ssoProperties.getSsoServerLogoutPath()));

        filtersMap.put("SSOClientLogoutFilter", new SSOClientCORSLogoutFilter(ssoProperties.getServerUrl()));

        shiroFilterFactoryBean.setFilters(filtersMap);

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //设置免认证url
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(ssoProperties.getSsoExcludedPaths(), ",");
        for (String anonUrl : anonUrls) {
            filterChainDefinitionMap.put(anonUrl, "anon");
        }
        //server端请求用
//        filterChainDefinitionMap.put(SSO_CLIENT_LOGOUT_PATH, "anon");
//        filterChainDefinitionMap.put(ssoProperties.getSsoClientLogoutPath(), "SSOClientLogoutFilter");
//        filterChainDefinitionMap.put("/logout", "logout");
        //SSOClientLoginRefererFilter用来保存referer，供client端login使用，根据referer返回。（例如主页上的登录按钮，登录后跳转回主页）
        filterChainDefinitionMap.put("/login", "SSOClientLoginRefererFilter,SSOClientLoginFilter");
        //服务端调用client退出地址
        filterChainDefinitionMap.put(ssoProperties.getSsoClientLogoutPath(),"SSOClientLogoutFilter");
        //跳转server统一logout
        filterChainDefinitionMap.put(ssoProperties.getSsoRedirectLogoutUrl(),"SSOClientRedirectLogoutFilter");
        filterChainDefinitionMap.put("/**", "SSOClientLoginFilter");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }




}
