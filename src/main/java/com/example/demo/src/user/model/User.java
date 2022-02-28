package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class User {
//    private int userIdx;
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
}


