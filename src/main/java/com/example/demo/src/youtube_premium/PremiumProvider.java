package com.example.demo.src.youtube_premium;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.youtube_premium.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class PremiumProvider {


    private final PremiumDao premiumDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PremiumProvider(PremiumDao premiumDao, JwtService jwtService) {
        this.premiumDao = premiumDao;
        this.jwtService = jwtService;
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //프리미엄에 가입한 모든 유저 조회 - getPremiumUsers()
    public List<GetPremiumRes> getPremiumUsers() throws BaseException{
        try{  //에러가 없으면 Dao에게 넘겨줌
            List<GetPremiumRes> getPremiumRes = premiumDao.getPremiumUsers();
            return getPremiumRes;
        }
        catch (Exception exception) { //에러가 발생한 경우
            throw new BaseException(DATABASE_ERROR_PREMIUM_INFO); //데이터 베이스 오류를 던져줌
        }
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 프리미엄 가입 여부 확인 - getPremiumUser() */
    public GetPremiumRes getPremiumUser(int userIdx) throws BaseException {   //UserComtroller.java에서 userIdx값을 받아옴.
        try {   //에러가 없다면
            GetPremiumRes GetPremiumRes = premiumDao.getPremiumUser(userIdx);  //userDao.getUser()에게 userIdx값을 그대로 넘겨줌
            return GetPremiumRes;
        } catch (Exception exception) {    //에러가 있다면 (의미적 validation 처리)
            throw new BaseException(DATABASE_ERROR_PREMIUM_INFO);
        }
    }



















}
