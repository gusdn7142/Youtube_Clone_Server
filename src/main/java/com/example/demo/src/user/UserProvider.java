package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 로그인 진행 (이메일과 패스워드 받음) */
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{

        //1.이메일이 존재하는지 검사
        int result = userDao.checkEmail(postLoginReq.getEmail());
        if (result == 0){
            throw new BaseException(NOT_EXIST_EMAIL);
        }

        
        /* 이메일을 통해 암호화된 패스워드를 가져옴 */
        User user = userDao.getUserInfo(postLoginReq);


        String password;
        //password = user.getPassword();  //임시 (패스워드 복호화 안할떄 필요)

        /* 패스워드 복호화 수행 */
        try { //DB에서 조회한 데이터중 (암호화 된) password를 복호화하여 password 변수에 저장
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR); //복호화 실패시 다음의 에러값을 반환
        }

        //2.body에 입력한 값과 DB에서 가져와 복호화된 패스워드를 비교해 일치하는지 파악
        if(postLoginReq.getPassword().equals(password)){

            //3.비활성화된 유저 확인(ex, status =0)
            user = userDao.checkUserStatus(postLoginReq.getEmail());
            //유저가 비활성화 상태일떄 에러 코드 응답
            if(user.getStatus() != 1){
                throw new BaseException(INACTIVE_USER_STATUS);   //계정이 활성화 상태가 아닙니다.
            }


            //이메일을 통해 idx 값을 가져옴
            int userIdx = userDao.getUserInfo(postLoginReq).getId();

            //userIdx를 통해 jwt토큰을 발급해 jwt 변수에 저장
            String jwt = jwtService.createJwt(userIdx);



            // jwt토큰을 DB에 저장 (Controller에서 진행)
            

            //userIdx와 jwt를 리턴
            return new PostLoginRes(userIdx,jwt);
        }


        /* body에 입력한 값과 DB에서 가져와 복호화된 패스워드를 비교해 일치하지 않으면 수행 */
        else{ //다음의 에러코드 ('로그인 실패')
            throw new BaseException(INCORRECT_TO_PASSWORD);
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 카카오 로그인 진행 - kakaoLogIn() */
    public PostLoginRes kakaoLogIn(PostKakaoLoginReq postKakaoLoginReq) throws BaseException {  //PostLoginRes   // throws BaseException

        //1. 이메일 중복 검사 (DB에 이메일이 존재하면 회원가입 필요가 없다)
        if (userDao.checkEmail(postKakaoLoginReq.getEmail()) == 1) {              //이메일이 중복이 되면 결과값인 1과 매핑이 되어 중복 여부를 판단 가능,  카카오 여부도 나중에 확인해야함
//            System.out.println("정상적으로 로그인이 가능합니다.");
            //throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        //계정이 없어서 회원가입을 해야 하는 경우
        else if(userDao.checkEmail(postKakaoLoginReq.getEmail()) == 0){
//            System.out.println("카카오 계정으로 신규 가입이 필요합니다!");
            throw new BaseException(POST_USERS_JOIN_KAKAO);  //"신규 회원가입 메시지 보냄"
        }
//        else {
//            throw new BaseException(DATABASE_ERROR);
//        }

        //2.비활성화된 유저 확인(ex, status =0)
        User user = userDao.checkUserStatus(postKakaoLoginReq.getEmail());

        //유저가 비활성화 상태일떄 에러 코드 응답
        if(user.getStatus() != 1){
            throw new BaseException(INACTIVE_USER_STATUS);   //계정이 활성화 상태가 아닙니다.
        }



        /* 로그인 수행 (useridx와 jwt 발급 후 리턴) */
        int userIdx = userDao.getKakaoUserInfo(postKakaoLoginReq).getId();      //이메일을 통해 userDao.java의 DB에서 조회한 패스워드와 idx 값을 가져와 userIdx에 저장 (userIdx는 클라이언트에게 보내주기 위해 저장?)
        System.out.println(userIdx);

        //userIdx를 통해 카카오 jwt토큰을 발급해 jwt 변수에 저장
        String jwt = jwtService.createKakaoJwt(userIdx);


        //JWT 토큰 만료시간 확인
//        jwtService.getJwtContents(jwt);



        //userIdx와 jwt를 리턴
       return new PostLoginRes(userIdx,jwt);

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //모든 유저 조회 - getUsers()
    public List<GetUserRes> getUsers() throws BaseException{
        try{  //에러가 없으면 Dao에게 넘겨줌
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) { //에러가 발생한 경우
            throw new BaseException(DATABASE_ERROR_USER_INFO); //데이터 베이스 오류를 던져줌
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //모든 유저 조회 - getUsersByEmail()
    public List<GetUserRes> getUsersByEmail(String email) throws BaseException{
//        try{  //에러가 없으면 Dao에게 넘겨줌
        List<GetUserRes> getUsersRes = userDao.getUsersByEmail(email);

        if(getUsersRes.isEmpty()) {  //List형 객체가 비어있을때를 확인하려면 isEmpty()를 사용해야 한다.
            System.out.println("이메일이 없는데???");
            throw new BaseException(EMAIL_NONE_USER_INFO);   //DB에서 이메일에 해당되는 유저를 불러오지 못했을떄...
        }
        else {
            return getUsersRes;
        }


//        }
//        catch (Exception exception) {               //에러가 발생한 경우
//            throw new BaseException(DATABASE_ERROR_USER_INFO); //데이터 베이스 오류를 던져줌
//        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /* 보류....DB에서 jwt 토큰 가져오는 함수 - getUserToken() */
//    public String getUserToken(int userIdx) throws BaseException{
//
//        try{  //유저의 ID값을 전송하여 jwt 토큰 값을 얻어냄..
//            String jwt = userDao.getUserToken(userIdx).getJwt();
////            System.out.println(jwt);
//
//            return jwt;
//        } catch(Exception exception){
//            throw new BaseException(GET_FAIL_USER_JWT);   //"유저의 jwt를 가져오지 못하였습니다."
//        }
//    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 로그아웃된 유저 (만료된 토큰 접근)인지 확인 */
    public void checkByUser(String jwt) throws BaseException{

        int checkNum = userDao.checkByUser(jwt);
//        System.out.println(checkNum);
        if(checkNum == 1){
            throw new BaseException(LOGOUT_USER_JWT);
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 특정 유저 정보 얻어오기 - getUser() */
    public GetUserRes getUser(int userIdx) throws BaseException {   //UserComtroller.java에서 userIdx값을 받아옴.
        try {   //에러가 없다면
            GetUserRes getUserRes = userDao.getUser(userIdx);  //userDao.getUser()에게 userIdx값을 그대로 넘겨줌
            return getUserRes;
        } catch (Exception exception) {    //에러가 있다면 (의미적 validation 처리)
            throw new BaseException(DATABASE_ERROR_USER_INFO);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저가 소유한 채널 정보 얻어오기 - getUserChannel() */
    public List<GetUserChannelRes> getUserChannel(int userIdx) throws BaseException {
        try {   //에러가 없다면
            List<GetUserChannelRes> getUserchannelRes = userDao.getUserChannel(userIdx);
            return getUserchannelRes;
        }
        catch (Exception exception) {    //에러가 있다면 (의미적 validation 처리)
            throw new BaseException(DATABASE_ERROR_CHANNELS_INFO);
        }
    }






























///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //닉네임 중복 체크 함수 (회원가입시)
    public int checkNickName(String nickName) throws BaseException{
        try{
            return userDao.checkNickName(nickName);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //이메일 중복 체크 함수 (회원가입시)
    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //시스템 ID 중복 체크 함수 - 삭제 예정
//    public int checkSystemId(String systemId) throws BaseException{
//        try{
//            return userDao.checkSystemId(systemId);
//        } catch (Exception exception){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }






















}
