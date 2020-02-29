package com.arnold.ssocore.dto;

import java.io.Serializable;

public class ResponseResult<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1679552421651455773L;

    private int status; //状态码，根据服务实际需求自定义

    private String msg;

    private T data;

    private String url; //请求的url

    private Long host; //出现问题的根服务

    public static ResponseResult ok(Object data, String url) {
        return new ResponseResult(ResponseStatusCode.OK, data, url, null);
    }

    public static ResponseResult ok(String msg, Object data, String url) {
        return new ResponseResult(ResponseStatusCode.OK, msg, data, url, null);
    }

    public static ResponseResult ok(String msg, String url) {
        return new ResponseResult(ResponseStatusCode.OK, msg, null, url, null);
    }

    public static ResponseResult fail(int status, String msg, String url, Long host) {
        return new ResponseResult(status, msg, url, host);
    }

    public ResponseResult() {
    }

    public ResponseResult(String msg, T data, String url, Long host) {
        this.msg = msg;
        this.data = data;
        this.url = url;
        this.host = host;
    }

    public ResponseResult(int status, String msg, String url, Long host) {
        this.status = status;
        this.msg = msg;
        this.url = url;
        this.host = host;
    }

    public ResponseResult(int status, T data, String url, Long host) {
        this.status = status;
        this.data = data;
        this.url = url;
        this.host = host;
    }

    public ResponseResult(int status, String msg, T data, String url, Long host) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.url = url;
        this.host = host;
    }

    public ResponseResult(int status, String msg, T data, Long host) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.host = host;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }


}