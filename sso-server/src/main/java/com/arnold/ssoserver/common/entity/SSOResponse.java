package com.arnold.ssoserver.common.entity;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * @author MrBird
 */
public class SSOResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = -8713837118340960775L;

    public SSOResponse code(HttpStatus status) {
        this.put("code", status.value());
        return this;
    }

    public SSOResponse message(String message) {
        this.put("message", message);
        return this;
    }

    public SSOResponse data(Object data) {
        this.put("data", data);
        return this;
    }

    public SSOResponse success() {
        this.code(HttpStatus.OK);
        return this;
    }

    public SSOResponse fail() {
        this.code(HttpStatus.INTERNAL_SERVER_ERROR);
        return this;
    }

    @Override
    public SSOResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}