package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;   //Timestamp를 사용하기 위한 정의

@Getter
@Setter
@AllArgsConstructor


public class GetUserRes {
    private int id;
    private String nickName;
    private String email;
    private String password;
    private String image;
    private String systemId;
    private String accountType;
    private int status;
    private Timestamp crateAt;
    private Timestamp updateAt;
    //timestamp 생략



//    private int userIdx;
//    private String userName;
//    private String ID;
//    private String email;
//    private String password;
}
