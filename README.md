# 简介
基于springboot、shiro的单点登录系统

# 演示
![](https://raw.githubusercontent.com/4rnold/Blog/pic/img/QQ%E5%BD%95%E5%B1%8F20200229154121.gif)

# 使用
客户端只需添加starter引用，配置文件中增加相应配置即可。
```xml
  <dependency>
      <groupId>com.arnold</groupId>
      <artifactId>sso-boot-starter</artifactId>
      <version>0.0.1-SNAPSHOT</version>
  </dependency>
```

# 技术
- springboot
- shiro
- mybatis-plus
- mysql
- redis
- thymeleaf

# 参考
- 登录系统修改自 [wuyouzhuguli/FEBS-Shiro](https://github.com/wuyouzhuguli/FEBS-Shiro)
- [Taobao SSO 跨域登录过程解析 - 未分类 - 清秋月](https://discourse.qingqiuyue.com/t/topic/347/1)
- [阿里（淘宝、天猫、一淘）、京东SSO分析 - Java开发 - 程序喵](http://www.ibloger.net/article/3052.html)
