package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Repository       //Dao class에서 쓰이며, DataBase에 접근하는 method를 가지고 있는 Class에서 쓰인다.
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 회원가입 -  createUser() */
    public int createUser(PostUserReq postUserReq){  //UserServcie.java에서 postUserReq 객체를 받아옴

        //SYSTEM_ID 임의로 생성 : 유저의 이메일중 아이디 앞 뒤 부분에 추가값
        String user_id= postUserReq.getEmail().substring(0, postUserReq.getEmail().indexOf("@"));
        postUserReq.setSystemId("YOUTUBE" + user_id + "1A3B5"); //시스템_ID 생성

        //쿼리문 생성
        String createUserQuery = "insert into USERS (NICK_NAME, EMAIL, PASSWORD, IMAGE, SYSTEM_ID) VALUES (?,?,?,?,?)";          //실제 DB 칼럼값과 같아야 함.
        Object[] createUserParams = new Object[]{postUserReq.getNickName(), postUserReq.getEmail(), postUserReq.getPassword(), postUserReq.getImage(), postUserReq.getSystemId() };  //postUserReq의 변수명과 유사해야함

        //쿼리문 수행 (유저 생성)
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        //Id 값을 반환
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*닉네임 중복 체크 (회원가입시) - checkNickName() */
    public int checkNickName(String email){
        String checkNickNameQuery = "select exists(select NICK_NAME from USERS where NICK_NAME = ?)";
        String checkNickNameParams = email;
        return this.jdbcTemplate.queryForObject(checkNickNameQuery,
                int.class,
                checkNickNameParams); //int형으로 쿼리 결과를 넘겨줌 (0,1)
    }

    //이메일 중복 체크 (회원가입시)
    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select EMAIL from USERS where EMAIL = ?)";
        String checkEmailParams = email;

//        System.out.println(email);

        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams); //int형으로 쿼리 결과를 넘겨줌 (0,1)


    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저 활성화 상태(status) 확인 함수 -  checkUserStatus()  */
    public User checkUserStatus(String email) {

        //쿼리문 생성
        String checkUserStatusQuery = "select * from USERS where EMAIL = ?";

        //body에서 입력한 이메일과 패스워드 중 이메일을 getPwdParams 변수에 반환
        String getEmailParams = email;

        //쿼리문 수행 (유저 상태 정보를 가져옴)
        return this.jdbcTemplate.queryForObject(checkUserStatusQuery,           //body에서 입력한 이메일과 매핑되는 테이블의 칼럼들을 반환.
                (rs,rowNum)-> new User(                                           //User 클래스 객체인 rs에 칼럼들을 다 넣음
                        rs.getInt("ID"),
                        rs.getString("NICK_NAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("IMAGE"),
                        rs.getString("SYSTEM_ID"),
                        rs.getString("ACCOUNT_TYPE"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT")
                ),
                getEmailParams);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 로그인시 이메일을 통해 Idx를 가져오는 함수 - getUserInfo()  */
    public User getUserInfo(PostLoginReq postLoginReq){

        //쿼리문 생성
        String getUserInfoQuery = "select ID, NICK_NAME, EMAIL, PASSWORD, IMAGE, SYSTEM_ID, ACCOUNT_TYPE ,STATUS, CREATE_AT, UPDATE_AT from USERS where EMAIL = ?";

        //이메일값 저장
        String getUserInfoParams = postLoginReq.getEmail();    //body에서 입력한 이메일과 패스워드 중 이메일을 getIdxParams 변수에 반환

        return this.jdbcTemplate.queryForObject(getUserInfoQuery,          //body에서 입력한 이메일과 매핑되는 테이블의 칼럼들을 반환.
                (rs,rowNum)-> new User(                            //User 클래스 객체인 rs에 칼럼들을 다 넣음
                        rs.getInt("ID"),
                        rs.getString("NICK_NAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("IMAGE"),
                        rs.getString("SYSTEM_ID"),
                        rs.getString("ACCOUNT_TYPE"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT")
                ),
                getUserInfoParams
        );

    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 로그인시 jwt 토큰을 DB에 저장 - saveJwt() */
    public int saveJwt(PostLoginRes postLoginRes) {

        //쿼리문 생성
        String saveJwtQuery = "insert into USER_LOGOUTS (JWT, USER_ID) VALUES (?,?) ";
        //토큰과 idx를 객체에 저장
        Object[] saveJwtParams = new Object[]{postLoginRes.getJwt(), postLoginRes.getUserIdx()};

        return this.jdbcTemplate.update(saveJwtQuery,saveJwtParams);   //닉네임 변경 쿼리문 수행
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 카카오 계정 회원가입 - createKakaoUser() */
    public int createKakaoUser(PostKakaoUserReq postKakaoUserReq){  //UserServcie.java에서 postUserReq 객체를 받아옴

        //SYSTEM_ID : 유저의 이메일중 아이디 부분과 결합
        String user_id = postKakaoUserReq.getEmail().substring(0, postKakaoUserReq.getEmail().indexOf("@"));
        postKakaoUserReq.setSystemId("KAKAO" + user_id + "1A3B5"); //시스템_ID 생성

        //카카오 계정 타입 생성
        postKakaoUserReq.setAccountType("KAKAO");

        //패스워드 생성 (추후엔 자동 생성 예정)
        postKakaoUserReq.setPassword("");

        //쿼리 생성
        String createKakaoUserQuery = "insert into USERS (NICK_NAME, EMAIL, PASSWORD, IMAGE, SYSTEM_ID, ACCOUNT_TYPE) VALUES (?,?,?,?,?,?)";

        //새로운 객체에 카카오 계정값 입력
        Object[] createKakaoUserParams = new Object[]{postKakaoUserReq.getNickName(), postKakaoUserReq.getEmail(), postKakaoUserReq.getPassword(), postKakaoUserReq.getImage(), postKakaoUserReq.getSystemId(), postKakaoUserReq.getAccountType() };  //postUserReq의 변수명과 유사해야함

        //쿼리 실행
        this.jdbcTemplate.update(createKakaoUserQuery, createKakaoUserParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);   //id를 반환
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 카카오 로그인시 이메일로 패스워드를 받아와 사용자를 검증 - getKakaoUserInfo() */
    public User getKakaoUserInfo(PostKakaoLoginReq postKakaoLoginReq){
        String getPwdQuery = "select ID, NICK_NAME, EMAIL, PASSWORD, IMAGE, SYSTEM_ID, ACCOUNT_TYPE ,STATUS, CREATE_AT, UPDATE_AT from USERS where EMAIL = ?";
        String getKakaoUserInfoParams = postKakaoLoginReq.getEmail();    //body에서 입력한 이메일과 패스워드 중 이메일을 getPwdParams 변수에 반환

        return this.jdbcTemplate.queryForObject(getPwdQuery,          //body에서 입력한 이메일과 매핑되는 테이블의 칼럼들을 반환.
                (rs,rowNum)-> new User(                            //User 클래스 객체인 rs에 칼럼들을 다 넣음
                        rs.getInt("ID"),
                        rs.getString("NICK_NAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("IMAGE"),
                        rs.getString("SYSTEM_ID"),
                        rs.getString("ACCOUNT_TYPE"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT")
                ),
                getKakaoUserInfoParams
        );

    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 모든 유저 정보 조회 */
    public List<GetUserRes> getUsers(){                  //lIST 형식으로 받아옴
        String getUsersQuery = "select * from USERS";

        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(                  //객체가 만들어지면서 다음 값을 받아옴.
                        rs.getInt("ID"),             //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getString("NICK_NAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("IMAGE"),
                        rs.getString("SYSTEM_ID"),
                        rs.getString("ACCOUNT_TYPE"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT"))
        );





    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //모든 유저 조회 - getUsersByEmail()
    public List<GetUserRes> getUsersByEmail(String email){    //lIST 형으로 받아옴

        //쿼리문과 객체 생성
        String getUsersByEmailQuery = "select * from USERS where email =?";
        String getUsersByEmailParams = email;                       //파라미터 존재

        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(                    //객체가 만들어지면서 다음 값을 받아옴.
                        rs.getInt("ID"),             //각 칼럼은 DB와 매칭이 되어야 한다.
                        rs.getString("NICK_NAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("IMAGE"),
                        rs.getString("SYSTEM_ID"),
                        rs.getString("ACCOUNT_TYPE"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT")),
                getUsersByEmailParams);                             //파라미터 보내줌.
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /* 보류.... DB에서 jwt 토큰 가져오는 함수 - getUserToken() */
//    public UserJwt getUserToken(int userIdx) {
//
//        //쿼리문 생성
//        String getUserTokenQuery = "select JWT from USER_LOGOUTS where USER_ID = ? and STATUS = 1 ";
//        int getUserTokenParams = userIdx;
//
////        System.out.println(getUserTokenParams);
//
//        //토큰을 가져온다.
//        return this.jdbcTemplate.queryForObject(getUserTokenQuery,          //body에서 입력한 이메일과 매핑되는 테이블의 칼럼들을 반환.
//                (rs,rowNum)-> new UserJwt(                            //User 클래스 객체인 rs에 칼럼들을 다 넣음
//                        rs.getString("JWT")
//                ),
//                getUserTokenParams
//        );
//    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 보류.... DB에서 jwt 토큰 가져오는 함수 - getUserToken() */
    public int checkByUser(String jwt) {

        //쿼리문 생성
        String checkByUserQuery = "select exists(select JWT from USER_LOGOUTS where JWT = ? and STATUS = 0)";   //로그아웃된 상태이면... 토큰을 만료해줘야 하는데 (jwt 만료시간을 변경할수 없기 때문에 이렇게 조치... )
        String checkByUserParams = jwt;
//        System.out.println(getUserTokenParams);


        //쿼리문 실행
        return this.jdbcTemplate.queryForObject(checkByUserQuery,     //토큰은 로그인시마다 바뀌기 때문에 테이블에 토큰이 같을리가 없기 때문에 queryForObject를 사용하였다.
                int.class,
                checkByUserParams); //int형으로 쿼리 결과를 넘겨줌 (0,1)
    }






//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 특정 유저 정보 얻어오기 - getUser() */
    public GetUserRes getUser(int userIdx){     //UserProvider.java에서 userIdx값을 받아옴.
        //쿼리문 생성
        String getUserQuery = "select * from USERS where ID = ?";   //쿼리문 실행 ('userIdx' 값으로 조회)
        int getUserParams = userIdx;      //파라미터(id) 값 저장

        //쿼리문 실행
        return this.jdbcTemplate.queryForObject(getUserQuery,          //하나의 행을 불러오기 때문에 jdbcTemplate.queryForObject 실행
                (rs, rowNum) -> new GetUserRes(    //rs.getString(" ")값이 DB와 일치해야 한다!
                        rs.getInt("ID"),             //각 칼럼은 DB와 매칭이 되어야 한다.
                        rs.getString("NICK_NAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("IMAGE"),
                        rs.getString("SYSTEM_ID"),
                        rs.getString("ACCOUNT_TYPE"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT")),
                getUserParams);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저 닉네임 정보 변경 - modifyNickName()  */
    public int modifyNickName(PatchUserReq patchUserReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String modifyNickNameQuery = "update USERS set NICK_NAME = ? where ID = ? AND STATUS = 1 ";

        //닉네임과, idx를 새로운 객체에 저장
        Object[] modifyNickNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};  //patchUserReq 객체의 nickName 값과 id값을 modifyUserNameParams객체에 저장

        //닉네임 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyNickNameQuery,modifyNickNameParams);
    }


    /* 유저 패스워드 정보 변경 - modifyPassword()  */
    public int modifyPassword(PatchUserReq patchUserReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String modifyPasswordQuery = "update USERS set PASSWORD = ? where ID = ? AND STATUS = 1 ";

        //패스워드와 idx를 새로운 객체에 저장
        Object[] modifyPasswordParams = new Object[]{patchUserReq.getPassword(), patchUserReq.getUserIdx()};  //patchUserReq 객체의 nickName 값과 id값을 modifyUserNameParams객체에 저장

        //패스워드 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyPasswordQuery,modifyPasswordParams);
    }


    /* 유저 이미지 정보 변경 - modifyPassword()  */
    public int modifyImage(PatchUserReq patchUserReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String modifyImageQuery = "update USERS set IMAGE = ? where ID = ? AND STATUS = 1 ";

        //패스워드와 idx를 새로운 객체에 저장
        Object[] modifyImageParams = new Object[]{patchUserReq.getImage(), patchUserReq.getUserIdx()};  //patchUserReq 객체의 nickName 값과 id값을 modifyUserNameParams객체에 저장

        //패스워드 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyImageQuery,modifyImageParams);
    }


//    /* 짭... 유저정보 변경 - modifyInfo()  */
//    public int modifyInfo(PatchUserReq patchUserReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
//
//        //쿼리문 생성 및 객체 생성
//        String modifyInfoQuery;
//        Object[] modifyInfoParams;
//
//        //이 작업도 기존의 칼럼 값들을 보내주면 if문을 쓸 필요는 없다....
//        if(patchUserReq.getNickName() == null && patchUserReq.getPassword() == null ) {   //닉네임과 패스워드 둘다 안들어왔을때, 패스워드와 이미지(null가능) 들어옴
//            modifyInfoQuery = "update USERS set IMAGE = ? where ID = ? ";
//            modifyInfoParams = new Object[]{patchUserReq.getImage(),patchUserReq.getUserIdx()};
//        }
//        else if(patchUserReq.getNickName() == null && patchUserReq.getPassword() != null ){ //닉네임이 안들어왔을때, 패스워드와 이미지(null가능) 들어옴
//            modifyInfoQuery = "update USERS set PASSWORD = ?, IMAGE = ? where ID = ? ";
//            modifyInfoParams = new Object[]{patchUserReq.getPassword(),patchUserReq.getImage(),patchUserReq.getUserIdx()};
//        }
//        else if(patchUserReq.getNickName() != null && patchUserReq.getPassword() == null ){  //패스워드가 안들어왔을떄..
//            modifyInfoQuery = "update USERS set NICK_NAME = ?, IMAGE = ? where ID = ? ";
//            modifyInfoParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getImage(),patchUserReq.getUserIdx()};
//        }
//        else{  //password와 nickname중 null값이 없음 (image는 null 가능!... 문제는 null로 대체되는데???)
//            modifyInfoQuery = "update USERS set NICK_NAME = ?, PASSWORD = ?, IMAGE = ? where ID = ? ";
//            modifyInfoParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getPassword(),patchUserReq.getImage(),patchUserReq.getUserIdx()};
//        }
//
//        //닉네임 변경 쿼리문 수행 (0,1로 반환됨)
//        return this.jdbcTemplate.update(modifyInfoQuery,modifyInfoParams);
//
//    }


















//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저정보 삭제(비활성화) - modifyNickName()  */
    public int deleteUser(PatchUserReq patchUserReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String deleteUserQuery = "update USERS set STATUS = 0 where ID = ? ";

        //idx를 변수에 저장
        int deleteUserParams = patchUserReq.getUserIdx();

        //유저 삭제(비활성화) 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저 로그아웃 - logout()  */
    public int logout(PatchUserReq patchUserReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String logoutQuery = "update USER_LOGOUTS set STATUS = 0 where USER_ID = ? and STATUS = 1";

        //idx를 변수에 저장
        int logoutParams = patchUserReq.getUserIdx();

        //유저 로그아웃 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(logoutQuery,logoutParams);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /* 유저 자동 로그아웃 - autoLogout()  */
//    public int autoLogout(PatchUserReq patchUserReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
//
//
//
//
//
//
//        //쿼리문 생성
//        String logoutQuery = "update USER_LOGOUTS set STATUS = 0 where USER_ID = ? and STATUS = 1";
//
//        //idx를 변수에 저장
//        int logoutParams = patchUserReq.getUserIdx();
//
//        //유저 자동 로그아웃 쿼리문 수행 (0,1로 반환됨)
//        return this.jdbcTemplate.update(logoutQuery,logoutParams);
//    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 유저가 소유한 채널 정보 얻기 - getUserChannel() */
    public List<GetUserChannelRes> getUserChannel(int userIdx){     //UserProvider.java에서 userIdx값을 받아옴.


        //쿼리문 생성
        String getUserChannelQuery = "select * from CHANNELS where USER_ID = ?";   //쿼리문 실행 ('userIdx' 값으로 조회)
        int getUserChannelParams = userIdx;      //파라미터(id) 값 저장

        //쿼리문 실행
        return this.jdbcTemplate.query(getUserChannelQuery,          //하나의 행을 불러오기 때문에 jdbcTemplate.queryForObject 실행
                (rs, rowNum) -> new GetUserChannelRes(    //rs.getString(" ")값이 DB와 일치해야 한다!
                        rs.getInt("ID"),                      //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getString("NAME"),
                        rs.getString("URL"),
                        rs.getString("IMAGE"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("SYSTEM_ID"),
                        rs.getInt("VIEWS"),
                        rs.getInt("SUBSCRIBER_COUNT"),
                        rs.getString("COUNTRY"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT")),
                getUserChannelParams);
    }

















































///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
















/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
















    
    
    



    
    






















}
