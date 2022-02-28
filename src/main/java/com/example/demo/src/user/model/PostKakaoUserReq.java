package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PostKakaoUserReq {

    private String nickName;
    private String email;
    private String image;
    private String systemId;
    private String accountType;
    private String password;
    ///////////////////////////////////

    private String access_token;
//    private int expires_in;   //5시간 후 만료...



}
