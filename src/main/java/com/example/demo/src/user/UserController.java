package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.demo.src.user.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.context.request.RequestContextHolder;    //header에서 토큰 가져올떄 사용
import org.springframework.web.context.request.ServletRequestAttributes; //header에서 토큰 가져올떄 사용
import javax.servlet.http.HttpServletRequest; //header에서 토큰 가져올떄 사용


import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController    //메서드의 반환 결과를 JSON 형태로 반환
@RequestMapping("/users")   //요청 URL을 어떤 method가 처리할지 mapping해주는 Annotation,  요청 받는 형식을 정의하지 않는다면, 자동적으로 GET으로 설정, 함수별 개별 설정 가능

public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired    //타입에 따라서 알앗거 Bean을 주입해준다. (자주 사용하는 객체를 Singleton 객체로 생성해놓고 어디서든 불러서 쓸 수 있는 것을 Spring 에서 Bean 이라는 이름을 붙인 것)
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }




///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 유저 생성 (회원가입) API
     * [POST] /users
     * @return BaseResponse<PostUserRes>  => idx 값 리턴
     */

    /* 회원가입 -  createUser() */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        //입력한 닉네임이 NULL인지 검사 (형식적 Validation)
        if(postUserReq.getNickName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        //입력한 이메일이 NULL인지 검사 (형식적 Validation)
        else if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //입력한 패스워드가 NULL인지 검사 (형식적 Validation)
        else if(postUserReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        //이메일 정규표현식 적용 (형식적 Validation)                //이메일 외에 다른 부분들도 적용 필요!!!
        if(!isRegexEmail(postUserReq.getEmail())){             //일단은 이해 안되어도 넘어가자
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        //패스워드 정규표현식 적용 (생략)
        if(!isRegexPassword(postUserReq.getPassword())){             //문자, 숫자 포함 6~12자리
            return new BaseResponse<>(POST_USERS_INVALID_Password);
        }


        //Http body에 필수 칼럼들이 모두 입력이 되었다면.
        //userService 클래스의  createUser() 함수로 입력한 값이 저장되어 있는 postUserReq를 전송
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));  //그냥 에러
        }
    }





//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 유저 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{

            //입력한 이메일이 NULL인지 검사
            if(postLoginReq.getEmail() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            //입력한 패스워드가 NULL인지 검사
            else if(postLoginReq.getPassword() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

            /* 로그인 진행 (이메일과 패스워드가 전송) */
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);  //postLoginRes 변수에 userIdx와 jwt를 리턴

            //JWT 토큰 만료시간 확인
            //jwtService.getJwtContents(postLoginRes.getJwt());


            // jwt토큰을 DB에 저장 (중복 부여는 고려하지 않아도 된다...)
            userService.saveJwt(postLoginRes);



            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 카카오 유저 회원가입 API
     * [POST] /users/kakao
     * @return BaseResponse<PostUserRes>  => idx 값 리턴
     */

    /* POST 방식 - Body 사용 - 카카오 회원가입 */
    @ResponseBody
    @PostMapping("/kakao")
    public BaseResponse<PostUserRes> createKakaoUser(@RequestBody PostKakaoUserReq postKakaoUserReq) {   //BaseResponse<PostUserRes>

        /* 토큰으로 카카오 서버에 사용자 자원 요청*/
        RestTemplate rt = new RestTemplate();

        //Header 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ postKakaoUserReq.getAccess_token()) ;     //토큰 입력
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8") ;  //전송한 데이터가 key, value 형태인것을 알려줌

        //Header를 엔티티에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);  //얘가 header값과 body 데이터를 가진 엔티티가 된다

        //Http 요청
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",      //요청할 주소
                HttpMethod.POST,             //post 형식
                kakaoProfileRequest,          //header값
                String.class
        );
        /////////////////////////////////////////////
//        System.out.println("------------- 출력----------");
//        System.out.println(oauthToken.getAccess_token());
//        System.out.println(response.getBody());


        //카카오 서버에서 가져온 정보 객체에 저장
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUserProfile kakaoUserProfile = null;

        try{
            kakaoUserProfile = objectMapper.readValue(response.getBody(),KakaoUserProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        //그냥 예시로 출력해본거..
//        System.out.println("\n");
//        System.out.println("카카오 토큰 값 : " + postKakaoUserReq.getAccess_token() );
//        System.out.println("카카오 토큰 만료시간 : " + postKakaoUserReq.getExpires_in() );

//        Date date = new Date(postKakaoUserReq.getExpires_in());  //뭔가이상하다.
//        System.out.println("카카오 토큰 만료시각 : " + date);

//        System.out.println("\n");
//        System.out.println("카카오 유저 아이디(번호) : " + kakaoUserProfile.getId() );
//        System.out.println("카카오 유저 닉네임: " + kakaoUserProfile.getProperties().getNickname() );
//        System.out.println("카카오 유저 이메일 : " + kakaoUserProfile.getKakao_account().getEmail() );
//        System.out.println("카카오 유저 이미지: " + kakaoUserProfile.getProperties().getProfile_image() );
        ////////////////////////////////////////////////////////////////////////////////////////////

        //사용자 정보를 뽑아옴.
        postKakaoUserReq.setEmail(kakaoUserProfile.getKakao_account().getEmail()); //이메일 삽입
        postKakaoUserReq.setNickName(kakaoUserProfile.getProperties().getNickname());  //닉네임 삽임
        postKakaoUserReq.setImage(kakaoUserProfile.getProperties().getProfile_image()); //이미지 삽입


        //카카오 회원가입 진행
        try{
            PostUserRes postUserRes = userService.createKakaoUser(postKakaoUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }






//        return 1;

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 카카오 유저 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login/kakao")
    public BaseResponse<PostLoginRes> logInKakao (@RequestBody PostKakaoLoginReq postKakaoLoginReq) {  //BaseResponse<PostLoginRes>


        /* 토큰으로 카카오 서버에 사용자 자원 요청*/
        RestTemplate rt = new RestTemplate();

        //Header 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ postKakaoLoginReq.getAccess_token()) ;     //토큰 입력
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8") ;  //전송한 데이터가 key, value 형태인것을 알려줌

        //Header를 엔티티에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);  //얘가 header값과 body 데이터를 가진 엔티티가 된다

        //Http 요청
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",      //요청할 주소
                HttpMethod.POST,             //post 형식
                kakaoProfileRequest,          //header값
                String.class
        );
        //////////////////////////////////////////////


//        System.out.println("------------- 출력----------");
//        System.out.println(oauthToken.getAccess_token());
//        System.out.println(response.getBody());


        //카카오 서버에서 가져온 정보 객체에 저장
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUserProfile kakaoUserProfile = null;

        try{
            kakaoUserProfile = objectMapper.readValue(response.getBody(),KakaoUserProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //그냥 예시로 출력해본거..
//        System.out.println("\n");
//        System.out.println("카카오 토큰 값 : " + postKakaoLoginReq.getAccess_token() );
//        System.out.println("카카오 토큰 만료시간 : " + postKakaoLoginReq.getExpires_in() );

//        Date date = new Date(postKakaoLoginReq.getExpires_in());  //뭔가이상하다.
//        System.out.println("카카오 토큰 만료시각 : " + date);

//        System.out.println("\n");
//        System.out.println("카카오 유저 아이디(번호) : " + kakaoUserProfile.getId() );
//        System.out.println("카카오 유저 닉네임: " + kakaoUserProfile.getProperties().getNickname() );
//        System.out.println("카카오 유저 이메일 : " + kakaoUserProfile.getKakao_account().getEmail() );
//        System.out.println("카카오 유저 이미지: " + kakaoUserProfile.getProperties().getProfile_image() );
        ////////////////////////////////////////////////////////////////////////////////////////////


        /* 로그인 진행 : 이메일과 패스워드가 저장된 postLoginReq객체를 UserProvider.java로 전송 */
        try {
            //이메일 정보만 가져옴.
            postKakaoLoginReq.setEmail(kakaoUserProfile.getKakao_account().getEmail()); //이메일 삽입

            /* 로그인 진행 : 이메일이 저장된 postKakaoLoginReq객체를 UserProvider.java로 전송 */
            PostLoginRes postLoginRes = userProvider.kakaoLogIn(postKakaoLoginReq);


            // jwt토큰을 DB에 저장 (중복 부여는 고려하지 않아도 된다...)
            userService.saveJwt(postLoginRes);
            return new BaseResponse<>(postLoginRes);

        }

        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 모든 유저 조회 API
     * [GET] /users
     * 시스템 ID로 검색 조회 가능 API
     * [GET] /users? systemId=
     * @return BaseResponse<List<GetUserRes>>
     */



    /* GET 방식 - Query String (쿼리 스트링)*/
    @ResponseBody   //JSON 혹은 xml 로 요청에 응답할수 있게 해주는 Annotation
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String email ) {
        try{

            //이메일을 입력 했다면 해당 유저 조회
            if(email != null){
                List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(email);   //전체 유저 조회
                return new BaseResponse<>(getUsersRes);
            }
            //전체 유저 조회
            List<GetUserRes> getUsersRes = userProvider.getUsers();
            return new BaseResponse<>(getUsersRes);





        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


//    /* GET 방식 - Query String (쿼리 스트링)*/   보류!!!
//    @ResponseBody   //JSON 혹은 xml 로 요청에 응답할수 있게 해주는 Annotation
//    @GetMapping("") // (GET) 127.0.0.1:9000/users
//    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String systemId) {   //required(쿼리 스트링 유뮤) = false 일 경우에도 에러 반환 X
//        try{                                                                                         //리스트 형식
//            if(systemId == null){ //시스템ID 값이 없는 경우
//                List<GetUserRes> getUsersRes = userProvider.getUsers();   //전체 유저 조회
//                return new BaseResponse<>(getUsersRes);
//            }
//
//            // Get Users (시스템ID 값이 존재하면)
//            List<GetUserRes> getUsersRes = userProvider.getUsersBySystemId(systemId);    //Provider가 시스템ID 값을 필터링
//            return new BaseResponse<>(getUsersRes);
//
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 특정 유저 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */

    /* GET 방식 - Path-variable (패스 베리어블) */
    @ResponseBody             // JSON 혹은 xml 로 요청에 응답할수 있게 해주는 Annotation
    @GetMapping("/{id}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("id") int userIdx) {              //BaseResponse<GetUserRes>


        try {
            /* 접근 제한 구현 */
            //DB에서 JWT를 가져와 사용자의 IDX를 추출
            //String jwt = userProvider.getUserToken(userIdx);
            //int userIdxByJwt = jwtService.getUserIdx2(jwt);

            //클라이언트에서 받아온 토큰에서 Idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //로그아웃된 유저 (만료된 토큰 접근)인지 확인
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); //토큰을 가져온다.
            userProvider.checkByUser(request.getHeader("X-ACCESS-TOKEN"));
            /*접근 제한 끝 */





        //특정 유저 정보 얻어오기 - getUser()
        GetUserRes getUserRes = userProvider.getUser(userIdx);
        return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * 짭......회원 정보 변경 API
//     * [PATCH] /users/:Id
//     * @return BaseResponse<String>
//     */
//
//    @ResponseBody
//    @PatchMapping("/{id}")  //jwt가 탈취될수 있기 때문에 path-variable 방식 사용
//    public BaseResponse<String> modifyNickName(@PathVariable("id") int userIdx, @RequestBody User user){  //@PatchUserReq patchUserReq으로 변경 가능
//
//
//        try {
//            //DB에서 사용자의 JWT 추출
//            String jwt = userProvider.getUserToken(userIdx);
////            int userIdxByJwt = jwtService.getUserIdx();
//
//            //가져온 토큰에서 Idx 추출
//            int userIdxByJwt = jwtService.getUserIdx2(jwt);
////            System.out.println(userIdxByJwt);
//
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//
//        //유저 정보 수정
//        PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getNickName());  //jwt토큰의 Idx 값과 내가 path-valiable로 입력한 id가 일치하면, patchUserReq 객체에 id 값과 nickName 값 저장.   getNickName에서 N을 대문자로 써야한다.
//        //위의 문장을 이렇게 바꿀수도 있다 =>  patchUserReq = new PatchUserReq(userIdx,patchUserReq.getNickName());
//
//        userService.modifyNickName(patchUserReq);  //userService.java로 patchUserReq객체 값 전송
//
//        String result = "회원 정보 변경이 완료되었습니다.";   //정보 변경 성공시 메시지 지정
//        return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
//        }
//    }



    /**
     * 회원 정보 변경 API
     * [PATCH] /users/:Id
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}")  //jwt가 탈취될수 있기 때문에 path-variable 방식 사용
    public BaseResponse<String> modifyInfo(@PathVariable("id") int userIdx, @RequestBody PatchUserReq patchUserReq){


        try {
            /* 접근 제한 구현 */
            //DB에서 JWT를 가져와 사용자의 IDX를 추출
            //String jwt = userProvider.getUserToken(userIdx);
            //int userIdxByJwt = jwtService.getUserIdx2(jwt);

            //클라이언트에서 받아온 토큰에서 Idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //로그아웃된 유저 (만료된 토큰 접근)인지 확인
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); //토큰을 가져온다.
            userProvider.checkByUser(request.getHeader("X-ACCESS-TOKEN"));
            /*접근 제한 끝 */


            //유저 정보를 객체에 넣음
            patchUserReq.setUserIdx(userIdx);

            //유저 정보 변경
            userService.modifyInfo(patchUserReq);  //userService.java로 patchUserReq객체 값 전송



            String result = "회원 정보 변경이 완료되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }
    }




//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 회원 정보 삭제 (비활성화 - 현재 로그인한 상태에서만 가능) API
     * [PATCH] /users/:Id/status
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}/status")
    public BaseResponse<String> deleteUser(@PathVariable("id") int userIdx){   //BaseResponse<String>

        try {

            /* 접근 제한 구현 */
            //DB에서 JWT를 가져와 사용자의 IDX를 추출
            //String jwt = userProvider.getUserToken(userIdx);
            //int userIdxByJwt = jwtService.getUserIdx2(jwt);

            //클라이언트에서 받아온 토큰에서 Idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //로그아웃된 유저 (만료된 토큰 접근)인지 확인
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); //토큰을 가져온다.
            userProvider.checkByUser(request.getHeader("X-ACCESS-TOKEN"));
            /*접근 제한 끝 */


            PatchUserReq patchUserReq = new PatchUserReq(userIdx,null,null,null);

            //유저 정보 삭제 (비활성화)
            userService.deleteUser(patchUserReq);  //userService.java로 patchUserReq객체 값 전송


            String result = "유저 상태가 삭제(비활성화) 되었습니다. 로그아웃 처리가 필요합니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }





    }

///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 유저 로그아웃 API
     * [PATCH] /users/logout/:id
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}/logout")
    public BaseResponse<String> logout (@PathVariable("id") int userIdx){   //BaseResponse<String>

        try {


            /* 접근 제한 구현 */
            //DB에서 JWT를 가져와 사용자의 IDX를 추출
            //String jwt = userProvider.getUserToken(userIdx);
            //int userIdxByJwt = jwtService.getUserIdx2(jwt);

            //클라이언트에서 받아온 토큰에서 Idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

//            //로그아웃된 유저 (만료된 토큰 접근)인지 확인
//            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); //토큰을 가져온다.
//            userProvider.checkByUser(request.getHeader("X-ACCESS-TOKEN"));
            /*접근 제한 끝 */




            PatchUserReq patchUserReq = new PatchUserReq(userIdx,null,null,null);
            //유저 로그아웃
            userService.logout(patchUserReq);  //userService.java로 patchUserReq객체 값 전송


            String result = "유저가 로그아웃되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }





    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 유저 자동 로그아웃 API  - 토큰 시간에 따라 만료시킴
     * [PATCH] /users/auto-logout/:id
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}/auto-logout")
    public BaseResponse<String> autoLogout (@PathVariable("id") int userIdx){   //BaseResponse<String>

        try {
//            /* 접근 제한 구현 */
//            //DB에서 JWT를 가져와 사용자의 IDX를 추출
//            //String jwt = userProvider.getUserToken(userIdx);
//            //int userIdxByJwt = jwtService.getUserIdx2(jwt);
//
//            //클라이언트에서 받아온 토큰에서 Idx 추출
//            int userIdxByJwt = jwtService.getUserIdx();
//
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//
//            //로그아웃된 유저 (만료된 토큰 접근)인지 확인
//            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); //토큰을 가져온다.
//            userProvider.checkByUser(request.getHeader("X-ACCESS-TOKEN"));
//            /*접근 제한 끝 */



            //토큰을 가져와서 만료 여부 확인
            int checkJwtTime = 0;
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); //토큰을 가져온다.
            checkJwtTime = jwtService.checkJwtTime(request.getHeader("X-ACCESS-TOKEN"));
//            System.out.println("checkJwtTime 값 : " + checkJwtTime);


            //토큰 만료시간 확인
//            jwtService.getJwtContents(request.getHeader("X-ACCESS-TOKEN"));


            PatchUserReq patchUserReq = new PatchUserReq(userIdx,null,null,null);

            //유저 자동 로그아웃
            String result = "";
            if(checkJwtTime == 1) {
                userService.logout(patchUserReq);  //userService.java로 patchUserReq객체 값 전송
                result = "jwt 토큰이 만료되어 유저가 자동 로그아웃 되었습니다.";
            }
            else{
//                result = "아직 유저의 jwt 토큰이 만료되지 않아 자동 로그아웃에 실패했습니다.";
                throw new BaseException(auto_logout_FAIL_USER);   //아직 유저의 jwt 토큰이 만료되지 않아 자동 로그아웃에 실패했습니다."
            }


            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }
    }





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 유저가 소유한 채널 조회 API
     * [GET] /users/:id/channels
     * @return BaseResponse<GetUserRes>
     */

    /* GET 방식 - Path-variable (패스 베리어블) */
    @ResponseBody             // JSON 혹은 xml 로 요청에 응답할수 있게 해주는 Annotation
    @GetMapping("{id}/channels") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<List<GetUserChannelRes>> getUserChannel (@PathVariable("id") int userIdx  ) {   //BaseResponse<GetUserRes>

       //접근 제한 부분
        try {

            /* 접근 제한 구현 */
            //DB에서 JWT를 가져와 사용자의 IDX를 추출
            //String jwt = userProvider.getUserToken(userIdx);
            //int userIdxByJwt = jwtService.getUserIdx2(jwt);

            //클라이언트에서 받아온 토큰에서 Idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //로그아웃된 유저 (만료된 토큰 접근)인지 확인
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); //토큰을 가져온다.
            userProvider.checkByUser(request.getHeader("X-ACCESS-TOKEN"));
            /*접근 제한 끝 */


        //유저가 소유한 채널 정보 얻기 - getUser()
            List<GetUserChannelRes> getUserChannelRes = userProvider.getUserChannel(userIdx);
            return new BaseResponse<>(getUserChannelRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }










































/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

























}
