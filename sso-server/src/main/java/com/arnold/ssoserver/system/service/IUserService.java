package com.arnold.ssoserver.system.service;

import com.arnold.ssoserver.system.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ARNOLD
 * @since 2020-02-06
 */
public interface IUserService extends IService<User> {

    public User findUserByName(String name);

    public void createUser(User user);
}
