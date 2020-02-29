//package com.arnold.ssoclient.runner;
//
////import com.netflix.discovery.DiscoveryClient;
//
//import com.arnold.ssoclient.consts.SSOClientConstant;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.context.ApplicationContext;
//
//import java.util.List;
//
//@Slf4j
//public class SSOClientRunner implements ApplicationRunner {
//
//    @Autowired
//    private DiscoveryClient discoveryClient;
//
//    private String serverName;
//
//    public SSOClientRunner(String serverName) {
//        this.serverName = serverName;
//    }
//
//    /**
//     * 获取server的url地址
//     * @param args
//     */
//    @Override
//    public void run(ApplicationArguments args) {
//        List<ServiceInstance> instances = discoveryClient.getInstances(serverName);
//        if (instances.size() < 1) {
//            throw new RuntimeException("没有发现sso server:" + serverName);
//        } else {
//            ServiceInstance serviceInstance = instances.get(0);
//            String uri = serviceInstance.getUri().toString();
//            SSOClientConstant.SSO_SERVER_URL = uri;
//            log.info("servername:{}, discovery sso server url:{}",serverName, uri);
//        }
//    }
//}
