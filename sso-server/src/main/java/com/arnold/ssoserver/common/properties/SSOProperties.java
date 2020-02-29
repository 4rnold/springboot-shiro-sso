package com.arnold.ssoserver.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
//@SpringBootConfiguration
@EnableConfigurationProperties
@Component
@PropertySource(value = "classpath:SSOServer.properties")
@ConfigurationProperties(prefix = "ssoserver")
public class SSOProperties {
    private ShiroProperties shiro = new ShiroProperties();
    private ValidateCodeProperties code = new ValidateCodeProperties();
}
