package com.example.demo.src.youtube_premium;

import com.example.demo.src.youtube_premium.model.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;


@Repository
public class PremiumDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 프리미엄 가입(생성) -  joinPremium() */
    public int joinPremium(PostPremiumReq postPremiumReq, int userIdx){  //UserServcie.java에서 postUserReq 객체를 받아옴

        //쿼리문 생성
        String joinPremiumQuery = "INSERT INTO YOUTUBE_PREMIUMS (FREE_TRIAL, CARD_COMPANY, CARD_NUMBER, USER_ID) VALUES (?, ?, ?, ?)";

//        //칼럼 셋팅 (Url과 SystemId는 임의생성)
//        postChannelReq.setUrl("https://youtube.com/channel/" + postChannelReq.getName()); //시스템_ID 생성
//        postChannelReq.setSystemId("CHANNEL-" + postChannelReq.getName() ); //시스템_ID 생성

//        System.out.println(postPremiumReq.getFreeTrial());
//        System.out.println(postPremiumReq.getCardCompany());
//        System.out.println(postPremiumReq.getCardNumber());
//        System.out.println(userIdx);
        Object[] joinPremiumParams = new Object[]{postPremiumReq.getFreeTrial(), postPremiumReq.getCardCompany(), postPremiumReq.getCardNumber(), userIdx};  //postUserReq의 변수명과 유사해야함

        //쿼리문 수행 (유저 생성)
        this.jdbcTemplate.update(joinPremiumQuery, joinPremiumParams);


        //Id 값을 반환
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);

    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 모든 프리미엄 가입자 정보 조회 -  */
    public List<GetPremiumRes> getPremiumUsers(){                  //lIST 형식으로 받아옴
        String getPremiumUsersQuery = "select FREE_TRIAL, PAYMENT_DATE, CARD_COMPANY, CARD_NUMBER, STATUS from YOUTUBE_PREMIUMS";


        return this.jdbcTemplate.query(getPremiumUsersQuery,
                (rs,rowNum) -> new GetPremiumRes(                  //객체가 만들어지면서 다음 값을 받아옴.
                        rs.getInt("FREE_TRIAL"),             //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getTimestamp("PAYMENT_DATE"),
                        rs.getString("CARD_COMPANY"),
                        rs.getString("CARD_NUMBER"),
                        rs.getInt("STATUS"))
        );
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 프리미엄 가입 여부 확인 - getPremiumUser() */
    public GetPremiumRes getPremiumUser(int userIdx){     //UserProvider.java에서 userIdx값을 받아옴.
        //쿼리문 생성
        String getPremiumUserQuery = "select FREE_TRIAL, PAYMENT_DATE, CARD_COMPANY, CARD_NUMBER, STATUS from YOUTUBE_PREMIUMS where USER_ID = ? AND STATUS = 1";   //쿼리문 실행 ('userIdx' 값으로 조회)
        int getPremiumUserParams = userIdx;      //파라미터(id) 값 저장

        //쿼리문 실행
        return this.jdbcTemplate.queryForObject(getPremiumUserQuery,          //하나의 행을 불러오기 때문에 jdbcTemplate.queryForObject 실행
                (rs, rowNum) -> new GetPremiumRes(    //rs.getString(" ")값이 DB와 일치해야 한다!
                        rs.getInt("FREE_TRIAL"),             //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getTimestamp("PAYMENT_DATE"),
                        rs.getString("CARD_COMPANY"),
                        rs.getString("CARD_NUMBER"),
                        rs.getInt("STATUS")),
                getPremiumUserParams);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 프리미엄 무료 여부 정보 변경  - modifyFreeTrial()  */
    public int modifyFreeTrial(PatchPremiumReq patchPremiumReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String modifyFreeTrialQuery = "update YOUTUBE_PREMIUMS set FREE_TRIAL = ? where USER_ID = ? AND STATUS = 1";
        Object[] modifyFreeTrialParams = new Object[]{patchPremiumReq.getFreeTrial(), patchPremiumReq.getUserId()};

        //프리미엄 결제 정보 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyFreeTrialQuery,modifyFreeTrialParams);
    }


    /* 프리미엄 카드회사 정보 변경  - modifyFreeTrial()  */
    public int modifyCardCompany(PatchPremiumReq patchPremiumReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String modifyCardCompanyQuery = "update YOUTUBE_PREMIUMS set CARD_COMPANY = ? where USER_ID = ? AND STATUS = 1";
        Object[] modifyCardCompanyParams = new Object[]{patchPremiumReq.getCardCompany(), patchPremiumReq.getUserId()};

        //프리미엄 결제 정보 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyCardCompanyQuery,modifyCardCompanyParams);
    }


    /* 프리미엄 카드번호 정보 변경  - modifyFreeTrial()  */
    public int modifyCardNumber(PatchPremiumReq patchPremiumReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String modifyCardNumberQuery = "update YOUTUBE_PREMIUMS set CARD_NUMBER = ? where USER_ID = ? AND STATUS = 1";
        Object[] modifyCardNumberParams = new Object[]{patchPremiumReq.getCardNumber(), patchPremiumReq.getUserId()};

        //프리미엄 결제 정보 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyCardNumberQuery,modifyCardNumberParams);
    }










//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 프리미엄 가입 취소(비활성화) - deletePremium()  */
    public int deletePremium(PatchPremiumReq patchPremiumReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String deletePremiumQuery = "update YOUTUBE_PREMIUMS set STATUS = 0 where USER_ID = ? ";

        //idx를 변수에 저장
        int deletePremiumParams = patchPremiumReq.getUserId();

        //프리미엄 정보 삭제(비활성화) 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(deletePremiumQuery,deletePremiumParams);
    }






























}
