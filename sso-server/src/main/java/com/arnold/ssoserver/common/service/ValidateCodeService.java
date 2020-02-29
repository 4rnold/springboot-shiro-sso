package com.arnold.ssoserver.common.service;

import com.arnold.ssoserver.common.exception.SSOException;
import com.arnold.ssoserver.common.properties.ValidateCodeProperties;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class ValidateCodeService {


/*    @Autowired
    private FebsProperties properties;*/

    @Autowired
    RedisService redisService;

/*    public void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        ValidateCodeProperties code = properties.getCode();
        setHeader(response, code.getType());
        Captcha captcha = createCaptcha(code);
        redisService.set(FebsConstant.REDIS_CODE_PREFIX + sessionId, StringUtils.lowerCase(captcha.text()), code.getTime());
        captcha.out(response.getOutputStream());
    }*/


    public void createByKey(HttpServletResponse response, String key, ValidateCodeProperties codeProperties) throws IOException {
        setHeader(response, codeProperties.getType());
        Captcha captcha = createCaptcha(codeProperties);
        redisService.set(key, StringUtils.lowerCase(captcha.text()), codeProperties.getTime());
        captcha.out(response.getOutputStream());
    }

    public void check(String key, String value) {
        Object code = redisService.get(key);
        if (StringUtils.isBlank(value)) {
            throw new SSOException("请输入验证码");
        }
        if (code == null) {
            throw new SSOException("验证码过期");
        }
        if (!StringUtils.equalsIgnoreCase(value, String.valueOf(code))) {
            throw new SSOException("验证码错误");
        }
    }

    private Captcha createCaptcha(ValidateCodeProperties code) {
        Captcha captcha = null;
        if (StringUtils.equalsIgnoreCase(code.getType(), ValidateCodeProperties.ImageType.GIF)) {
            captcha = new GifCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        } else {
            captcha = new SpecCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        }
        captcha.setCharType(code.getCharType());
        return captcha;
    }

    private void setHeader(HttpServletResponse response, String type) {
        if (StringUtils.equalsIgnoreCase(type, ValidateCodeProperties.ImageType.GIF)) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL,"No-store");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);
    }

}
