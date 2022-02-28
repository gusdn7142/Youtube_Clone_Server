package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PostKakaoLoginReq {

    private String nickName;
    private String email;
    private String password;
    private String image;
    private String systemId;
    private String accountType;

    private String access_token;
//    private int expires_in;   //6시간 후 만료... (고정이기 때문에 입력받을 필요는 없다.)



}
