package com.example.demo.src.youtube_premium;


import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import com.example.demo.src.youtube_premium.model.*;
import com.example.demo.utils.AES128;


import com.example.demo.config.secret.Secret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.example.demo.config.BaseResponseStatus.*;



@Service
public class PremiumService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PremiumDao premiumDao;
    private final PremiumProvider premiumProvider;
    private final JwtService jwtService;


    @Autowired
    public PremiumService(PremiumDao premiumDao, PremiumProvider premiumProvider, JwtService jwtService) {
        this.premiumDao = premiumDao;
        this.premiumProvider = premiumProvider;
        this.jwtService = jwtService;

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 프리미엄 가입(생성) -  joinPremium() */
    public PostPremiumRes joinPremium(PostPremiumReq postPremiumReq, int userIdx) throws BaseException {

//        //프리미엄명 중복 검사 (의미적 Varidation 처리 - 조회는 Provider)
//        if(channelProvider.checkName(postChannelReq.getName()) ==1){              //닉네임이 중복이 되면 결과값인 1과 매핑이 되어 중복 여부를 판단 가능
//            throw new BaseException(POST_CHANNELS_EXISTS_NAME);                   //'이미 존재하는 채널명입니다.'
//        }

            //프리미엄 가입
            try{
                int premiumIdx = premiumDao.joinPremium(postPremiumReq,userIdx);
            return new PostPremiumRes(premiumIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR_JOIN_PREMIUM); 
        }

    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 프리미엄 결제 정보 변경  - modifyPayment()  */
    public void modifyPayment(PatchPremiumReq patchPremiumReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...

        //프리미엄 무료 여부 정보 변경
        if(patchPremiumReq.getFreeTrial() != null) {
            int result = premiumDao.modifyFreeTrial(patchPremiumReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_PREMIUM_FREETRAIL);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //프리미엄 카드회사 정보 변경
        if(patchPremiumReq.getCardCompany() != null) {
            int result = premiumDao.modifyCardCompany(patchPremiumReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_PREMIUM_CARDCOMPANY);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //프리미엄 카드번호 정보 변경
        if(patchPremiumReq.getCardNumber() != null) {
            int result = premiumDao.modifyCardNumber(patchPremiumReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_PREMIUM_CARDNUMBER);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 프리미엄 가입 취소(비활성화) - deletePremium()  */
    public void deletePremium(PatchPremiumReq patchPremiumReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...
        try{
            //프리미엄 가입 취소
            int result = premiumDao.deletePremium(patchPremiumReq);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_PREMIUM);   //'DB에서 유튜브 프리미엄 가입 취소가 정상적으로 되지 않았습니다.'
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
        }
    }









































}
