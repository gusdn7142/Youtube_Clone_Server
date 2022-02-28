package com.example.demo.src.youtube_premium.model;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor


public class PatchPremiumReq {

//    private int Id;

    private Integer freeTrial;
    private String cardCompany;
    private String cardNumber;

    private int userId;
}
