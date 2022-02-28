package com.example.demo.src.channel.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor


public class PostChannelReq {

//    private int id;
    private String name;
    private String url;
    private String image;
    private String systemId;
    private String description;
    private String country;


}
