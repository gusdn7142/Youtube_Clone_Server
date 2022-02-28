package com.example.demo.src.video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.sql.*;   //Timestamp를 사용하기 위한 정의

@Getter
@Setter
@AllArgsConstructor
public class PatchVideoReq {

    private int videoId;
    private String thumbNail;
    private String video;
    private String title;
    private String description;
    private Integer videoOpen;

}
