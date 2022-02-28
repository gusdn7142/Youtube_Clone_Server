package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.example.demo.config.BaseResponseStatus.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})   //JSON 직렬화 순서 지정

public class BaseResponse<T> {
    @JsonProperty("isSuccess")   //key를 매핑
    private final Boolean isSuccess;
    private final String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;
//    private int result2;  //추가적으로 보낼 값이 있을떄 사용 (보류)


    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

//    //요청 성공시 추가적으로 보낼 값이 있을떄 사용 (result2) (보류)
//    public BaseResponse(T result, int result2) {
//        this.isSuccess = SUCCESS.isSuccess();
//        this.message = SUCCESS.getMessage();
//        this.code = SUCCESS.getCode();
//        this.result = result + result2 ;
////        this.result2 = result2;
//    }



    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }




}

