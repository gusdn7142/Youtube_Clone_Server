package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class ValidationRegex {


    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  //Pattern.CASE_INSENSITIVE : 대소문자 구분 안함.
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }


    public static boolean isRegexPassword(String target) {
        String regex = "^[A-Za-z0-9]{6,12}$";     //비밀번호 (숫자, 문자 포함의 6~12자리 이내)
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  //Pattern.CASE_INSENSITIVE : 대소문자 구분 안함.
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }










}

