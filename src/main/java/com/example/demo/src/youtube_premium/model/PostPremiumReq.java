package com.example.demo.src.youtube_premium.model;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.math.*;
@Getter
@Setter
@AllArgsConstructor


public class PostPremiumReq {
    private int freeTrial;
    private String cardCompany;

//    private int cardNumber;

    private String cardNumber;


}
