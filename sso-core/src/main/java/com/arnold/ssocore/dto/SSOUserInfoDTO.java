package com.arnold.ssocore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSOUserInfoDTO implements Serializable {

    private String serverSessionId;

    private String userName;

}
