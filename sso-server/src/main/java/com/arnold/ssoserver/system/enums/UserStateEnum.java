package com.arnold.ssoserver.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserStateEnum {
    NORMAL(1, "正常"),
    FROZEN(0, "冻结");

    @EnumValue
    private int stateCode;
    private String stateString;

    UserStateEnum(int stateCode, String stateString) {
        this.stateCode = stateCode;
        this.stateString = stateString;
    }
}
