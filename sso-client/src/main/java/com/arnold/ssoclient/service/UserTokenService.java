package com.arnold.ssoclient.service;


import com.arnold.ssocore.dto.ResponseResult;
import com.arnold.ssocore.dto.SSOUserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Service
public class UserTokenService {


    @Autowired
    RestTemplate restTemplate;

    public ResponseResult<SSOUserInfoDTO> tokenVerify(String serverTokenVerifyUrl, String token, String clientLogoutUrl){

        LinkedMultiValueMap<String, Object> postData = new LinkedMultiValueMap<String, Object>();
        postData.add("token", token);
        postData.add("clientLogoutUrl", clientLogoutUrl);

        ParameterizedTypeReference<ResponseResult<SSOUserInfoDTO>> typeRef = new ParameterizedTypeReference<ResponseResult<SSOUserInfoDTO>>() {};

        ResponseResult<SSOUserInfoDTO> response = restTemplate.exchange(serverTokenVerifyUrl, HttpMethod.POST, new HttpEntity<>(postData), typeRef).getBody();

        return response;
    }

}
