package com.example.demo.utils;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class JwtService {

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////    /
    /*
    일반 계정 JWT 발급
    @param userIdx
    @return String
     */
    public String createJwt(int userIdx){  //jwt 생성
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")  //헤더 (type)
                .claim("userIdx",userIdx)      //페이로드 (유저의 idx (primary 키)값)
                .setIssuedAt(now)               //페이로드 (발급 시간)
                .setExpiration(new Date(System.currentTimeMillis()+(1000*60*60*24*365)))  //페이로드 (파기 시간 (ex, 1년).  개발 단계에서는 클라이언트 개발자가 테스트를 원활하게 하기 위해 길게 주는게 좋다)
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)                 //서명 (헤더의 alg인 HS256 알고리즘 사용, 비밀키로 JWT_SECRET_KEY 사용)
                .compact();
    }


    /*
    카카오 계정 JWT 발급
    @param userIdx
    @return String
     */
    public String createKakaoJwt(int userIdx){  //jwt 생성
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")  //헤더 (type)
                .claim("userIdx",userIdx)      //페이로드 (유저의 idx (primary 키)값)
                .setIssuedAt(now)               //페이로드 (발급 시간)
                .setExpiration(new Date(System.currentTimeMillis()+(1000*21599)))  //카카오 토큰 21599초 (6시간) 고정!!
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)                 //서명 (헤더의 alg인 HS256 알고리즘 사용, 비밀키로 JWT_SECRET_KEY 사용)
                .compact();
    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /*짭... 카카오 계정 jwt 발급 */
//    public String createKakaoJwt(int userIdx, long expires_in ){  //jwt 생성
//
//        System.out.println("함수에 넣은 토큰 만료시간 :" + expires_in);  //나중에 토큰 만료시간을 이걸로 바꾸자!!!
//
//        Date now = new Date();
//        return Jwts.builder()
//                .setHeaderParam("type","jwt")  //헤더 (type)
//                .claim("userIdx",userIdx)      //페이로드 (유저의 idx (primary 키)값)
//                .setIssuedAt(now)               //페이로드 (발급 시간)
//                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))  //나중에 이부분좀 바꾸자!!
//                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)                 //서명 (헤더의 alg인 HS256 알고리즘 사용, 비밀키로 JWT_SECRET_KEY 사용)
//                .compact();
//    }           //System.currentTimeMillis()+1*(1000*60*60*24*365)



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");    //X-ACCESS-TOKEN의 키에 대한 값을 가져옴 (헤더에 넣어주어야 한다)
    }

    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserIdx() throws BaseException{
        //1. JWT 추출
        String accessToken = getJwt();   //
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()  //유효한 토큰인지 확인,  즉 로그인시 부여한 jwt 토큰인지 확인
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출  (위의 과정에서 문제가 없다면 수행)
        return claims.getBody().get("userIdx",Integer.class);
    }





/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    /*
//     DB에서 받아온 JWT를 통해 userIdx를 추출
//    @return int
//    @throws BaseException
//     */
//    public int getUserIdx2(String accessToken) throws BaseException{
//
//        // 2. JWT parsing
//        Jws<Claims> claims;
//        try{
//            claims = Jwts.parser()  //유효한 토큰인지 확인,  즉 로그인시 부여한 jwt 토큰인지 확인
//                    .setSigningKey(Secret.JWT_SECRET_KEY)
//                    .parseClaimsJws(accessToken);
//        } catch (Exception ignored) {
//            throw new BaseException(INVALID_JWT);
//        }
//
//        // 3. userIdx 추출  (위의 과정에서 문제가 없다면 수행)
//        return claims.getBody().get("userIdx",Integer.class);
//    }










/////////////////////////////////////////////////////////////////////
    //토큰 정보(만료시각) 확인 메서드
    public void getJwtContents(String jwt) {  //Claims
        Jws<Claims> claims;
        claims = Jwts.parser()  //유효한 토큰인지 확인,  즉 로그인시 부여한 jwt 토큰인지 확인
                .setSigningKey(Secret.JWT_SECRET_KEY)
                .parseClaimsJws(jwt);
        //try문 추가해야 만료되도 오류 안난다!!


        System.out.println("\n 현재!! 날짜!! ");                         //현재 날짜 및 시간 확인
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println("\n 현재!! 날짜를 초로 표시!! ");                         //현재 날짜 및 시간 확인
        System.out.println(new Date(System.currentTimeMillis()).getTime()/1000);


//        System.out.println(claims);
//        System.out.println("\n");
//        System.out.println(claims.getBody());
        System.out.println("\n jwt 토큰 만료시각 (카카오 jwt는 현재 날짜와 6시간 차이)");
        System.out.println(claims.getBody().getExpiration());                    //토큰 정보 확인
        System.out.println("\n jwt 토큰 만료시각 (날짜를 초로 표시)!!");
        System.out.println(claims.getBody().getExpiration().getTime()/1000);   //토큰의 날짜를 초로 변환

        System.out.println("\n");
        System.out.println("\n");


//        System.out.println("\n 밀리세컨드 ");
//        System.out.println(System.currentTimeMillis());
//
//        System.out.println("\n 밀리세컨드 +1");
//        System.out.println(System.currentTimeMillis()+1);
//
//        System.out.println("\n 밀리세컨드 연산");
//        System.out.println(System.currentTimeMillis()+1*(1000*60*60*24*365));
//
//        System.out.println("\n 밀리세컨드 -> 날짜 변환");
//        System.out.println(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)));
//
//        System.out.println("\n 날짜 -> 밀리세컨드 변환");
//        System.out.println(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)).getTime());
//        System.out.println("\n");


//        return claims;
    }




///////////////////////////////////////////////////////////////////////
    //토큰 만료 여부 확인 메서드  (만료시간이 지났을 경우 토큰을 만료시킴)
    public int checkJwtTime(String jwt) {  //Claims
        Jws<Claims> claims;
        try {
            claims = Jwts.parser()  //유효한 토큰인지 확인,  즉 로그인시 부여한 jwt 토큰인지 확인
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(jwt);
            return 0;  //토큰이 만료가 되지 않았다면 0 반환
        }
        catch (Exception ignored){
//            throw new BaseException(INVALID_JWT);
            return 1;  //토큰이 만료 되었다면 1 반환
        }

    }









































//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /*
//    짭.... JWT에서 토큰 만료시간 추출  - 앞에거 복사해서 썼음... 안쓰일것 같긴 한데..   Controller의 유저변경에서 사용중.
//    @return int
//    @throws BaseException
//     */
//    public Date getExpiration(String accessToken) throws BaseException {
//        //1. JWT 추출
////        String accessToken = getJwt();   //
////        if(accessToken == null || accessToken.length() == 0){
////            throw new BaseException(EMPTY_JWT);
////        }
//
//        // 2. JWT parsing
//        Jws<Claims> claims;
//        try {
//            claims = Jwts.parser()  //유효한 토큰인지 확인,  즉 로그인시 부여한 jwt 토큰인지 확인
//                    .setSigningKey(Secret.JWT_SECRET_KEY)
//                    .parseClaimsJws(accessToken);
//        } catch (Exception ignored) {
//            throw new BaseException(INVALID_JWT);
//        }
//
//        // 3. userIdx 추출  (위의 과정에서 문제가 없다면 수행)
//
////        System.out.println("토큰 만료 시간 : " + claims.getBody().getExpiration()  ); //토큰 만료시간
////        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd a HH:mm:ss");
////        System.out.println(simpleDateFormat.format(claims.getBody().getExpiration()));
////        System.out.println(simpleDateFormat.format(today));
//
//        Date today = new Date();
//        Date d1 = claims.getBody().getExpiration();
//
//
////        Date d2 = today;
//
////        long diff = d1.getTime() - d2.getTime();
////        long diffSeconds = diff / 1000;
////        System.out.println("Time in seconds: " + diffSeconds + " seconds.");
//
//
//        return d1;
//    }


































}
