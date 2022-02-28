package com.example.demo.src.channel.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor


public class Channel {

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
//    private int userId;


}
