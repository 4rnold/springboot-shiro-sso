package com.arnold.ssoserver.common.authentication;

import com.arnold.ssoserver.system.entity.User;
import com.arnold.ssoserver.system.enums.UserStateEnum;
import com.arnold.ssoserver.system.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 通过@bean配置，因为要配置HashedCredentialsMatcher
 */
//@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    IUserService userService;

    /**
     * 通过user找到role 通过 role找到对应的permission
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
//        String username = (String) token.getPrincipal();
//        String password = (String) token.getCredentials();

        UsernamePasswordToken usertoken = (UsernamePasswordToken) token;
        String username = usertoken.getUsername();
        String password = new String(usertoken.getPassword());


        User user = userService.findUserByName(username);
        if (user == null) {
            throw new UnknownAccountException("账号未注册");
        }
        if (!user.getStatus().equals(UserStateEnum.NORMAL)){
            throw new LockedAccountException("账号已被锁定");
        }


        return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(StringUtils.lowerCase(user.getUsername())), getName());
    }
}
