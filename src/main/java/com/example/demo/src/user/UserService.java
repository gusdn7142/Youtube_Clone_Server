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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 회원가입 -  createUser() */
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {

        //닉네임 중복 검사 (의미적 Varidation 처리 - 조회는 Provider)
        if(userProvider.checkNickName(postUserReq.getNickName()) ==1){              //닉네임이 중복이 되면 결과값인 1과 매핑이 되어 중복 여부를 판단 가능
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }

        //이메일 중복 검사 (의미적 Varidation 처리 - 조회는 Provider)
        else if(userProvider.checkEmail(postUserReq.getEmail()) ==1){              //이메일이 중복이 되면 결과값인 1과 매핑이 되어 중복 여부를 판단 가능
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        //패스워드 암호화 수행
        String pwd;
        try{
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());       //AES 함수는 건들지 말것.
            postUserReq.setPassword(pwd);   //postUserReq 객체의 패스워드 변수에 암호화된 패스워드를 저장
        } catch (Exception ignored) {       //패스워드 암호화 실패시 다음의 에러 응답코드 반환
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        //유저 생성 (userDao.java로 이동)
        try{
            int userIdx = userDao.createUser(postUserReq);
            return new PostUserRes(userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR_CREATE_USER);  //유저 생성 실패 에러
        }

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 로그인시 jwt토큰을 DB에 저장 - saveJwt() */
    public void saveJwt(PostLoginRes postLoginRes) throws BaseException {    //UserController.java에서 객체 값( useridx, jwt)을 받아와서...

        //userDao.java에서 쿼리문 수행결과로 받아온 (0 or 1)값을 result 변수에 저장
        int result = userDao.saveJwt(postLoginRes);

        if(result == 0){
            throw new BaseException(SAVE_FAIL_jwt);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
        }
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 카카오 계정 회원가입 - createKakaoUser() */
    public PostUserRes createKakaoUser(PostKakaoUserReq postKakaoUserReq) throws BaseException {

        //이메일 중복 검사 (의미적 Varidation 처리 - 조회는 Provider)
        if(userProvider.checkEmail(postKakaoUserReq.getEmail()) ==1){              //이메일이 중복이 되면 결과값인 1과 매핑이 되어 중복 여부를 판단 가능
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        //카카오 유저 계정 생성
        try{
            int userIdx = userDao.createKakaoUser(postKakaoUserReq);
            return new PostUserRes(userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR_CREATE_KAKAO_USER);
        }


    }
///// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 유저정보 변경 - modifyInfo()  */
    public void modifyInfo(PatchUserReq patchUserReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...

        //닉네임 값 변경
        if(patchUserReq.getNickName() != null){
            //유저 정보 변경
            int result = userDao.modifyNickName(patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);   //DB에서 nickName 값의 수정 실패시이면 에러코드 반환
            }
        }
        //패스워드 값 변경
        if(patchUserReq.getPassword() != null){

            //패스워드 암호화 수행
            String pwd;
            try{
                pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchUserReq.getPassword());       //AES 함수는 건들지 말것.
                patchUserReq.setPassword(pwd);   //postUserReq 객체의 패스워드 변수에 암호화된 패스워드를 저장
            } catch (Exception ignored) {       //패스워드 암호화 실패시 다음의 에러 응답코드 반환
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }

            //패스워드 정보 변경
            int result = userDao.modifyPassword(patchUserReq);

            if(result == 0){
                throw new BaseException(MODIFY_FAIL_PASSWORD);   //DB에서 Password 값의 수정 실패시이면 에러코드 반환
            }
        }
        //이미지 값 변경
        if(patchUserReq.getImage() != null){
            //패스워드 정보 변경
            int result = userDao.modifyImage(patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_IMAGE);   //DB에서 Password 값의 수정 실패시이면 에러코드 반환
            }
        }

        
    }




//    /* 짭... 유저정보 변경 - modifyInfo()  */
//    public void modifyInfo(PatchUserReq patchUserReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...
//        try{
//            //유저 정보 변경
//            int result = userDao.modifyInfo(patchUserReq);
//            if(result == 0){
//                throw new BaseException(MODIFY_FAIL_USERNAME);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
//            }
//        } catch(Exception exception){
//            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
//        }
//    }





/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저정보 삭제(비활성화) - modifyNickName()  */
    public void deleteUser(PatchUserReq patchUserReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...
        try{
            //유저 정보 변경
            int result = userDao.deleteUser(patchUserReq);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_USER);   //유저 정보 삭제(비활성화)를 실패했습니다.
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
        }
    }
/////// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저 로그아웃 - logout()  */
    public void logout(PatchUserReq patchUserReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...
        try{
            //유저 로그아웃
            int result = userDao.logout(patchUserReq);
            if(result == 0){  //이 부분은 없어도 될듯
                throw new BaseException(logout_FAIL_USER);   //이미 로그아웃 되었습니다.
            }
        } catch(Exception exception){
            throw new BaseException(logout_FAIL_USER);   //쿼리문 자체의 오류.
        }
    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /* 유저 자동 로그아웃 - autoLogout()  */
//    public void autoLogout(PatchUserReq patchUserReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...
//        try{
//            //유저 자동 로그아웃
//            int result = userDao.autoLogout(patchUserReq);
//            if(result == 0){
//                throw new BaseException(logout_FAIL_USER);   //이미 로그아웃 되었습니다.
//            }
//        } catch(Exception exception){
//            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
//        }
//    }

































//    public void checkUserStatus(PostLoginReq postLoginReq) throws BaseException{
//        //userDao.java에서 쿼리문 수행결과로 받아온 (0 or 1)값을 result 변수에 저장
//        User user = userDao.checkUserStatus(postLoginReq);
//
//        //유저가 비활성화 상태일떄 에러 코드 응답
//        if(user.getStatus() != 1){
//            throw new BaseException(INACTIVE_USER_STATUS);   //계정이 활성화 상태가 아닙니다.
//        }
//    }




















 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////











    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////




























}
