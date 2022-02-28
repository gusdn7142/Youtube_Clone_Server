package com.example.demo.src.channel.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor


public class GetMyVideo {

    private int id;
    private String thumbNail;
    private String video;
    private String title;
    private String description;
    private Timestamp uploadDate;
    private int videoOpen;
    private int likeCount;
    private int hateCount;
    private int views;
    private int commentCount;
    private int status;
    private Timestamp createAt;
    private Timestamp updateAt;
    private int channelId;
}
