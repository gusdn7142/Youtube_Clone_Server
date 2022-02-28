package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor  //생성자를 자동으로 생성해주는 애노테이션
public class BaseException extends Exception {
    private BaseResponseStatus status;


    
}
