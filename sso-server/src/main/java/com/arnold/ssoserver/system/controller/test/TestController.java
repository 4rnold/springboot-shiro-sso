package com.arnold.ssoserver.system.controller.test;

import com.arnold.ssoserver.common.entity.SSOResponse;
import com.arnold.ssoserver.common.utils.CheckUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;

@Controller
public class TestController {

    @RequestMapping("/test/testlocale")
    @ResponseBody
    public SSOResponse test() {
        CheckUtil.notNull(null, "value.is.null", null);
        return new SSOResponse();
    }

    @RequestMapping("/test/testlocale2")
    @ResponseBody
    public Object test2() {
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println(111);
        System.out.println(locale.toString());
        return locale;
    }
}
