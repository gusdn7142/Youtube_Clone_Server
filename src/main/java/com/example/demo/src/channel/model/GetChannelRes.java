package com.example.demo.src.channel.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;   //Timestamp를 사용하기 위한 정의

@Getter
@Setter
@AllArgsConstructor


public class GetChannelRes {
    private int id;
    private String name;
    private String url;
    private String image;
    private String description;
    private String systemId;
    private int views;
    private int subscriberCount;
    private String country;
    private int status;
    private Timestamp crateAt;
    private Timestamp updateAt;
    private int userId;


}
