package com.example.demo.src.youtube_premium.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.math.*;
import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor






public class GetPremiumRes {
//    private int id;


    private int freeTrial;
    private Timestamp paymentDate;
    private String cardCompany;
    private String cardNumber;
    private int status;




}
