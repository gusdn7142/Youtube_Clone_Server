package com.example.demo.src.channel;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.channel.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class ChannelProvider {


    private final ChannelDao channelDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ChannelProvider(ChannelDao channelDao, JwtService jwtService) {
        this.channelDao = channelDao;
        this.jwtService = jwtService;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*채널명 중복 체크 (채널 가입시) - checkName() */
    public int checkName(String name) throws BaseException{
        try{
            return channelDao.checkName(name);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //모든 채널 조회 - getUsers()
    public List<GetChannelRes> getChannels() throws BaseException{
        try{  //에러가 없으면 Dao에게 넘겨줌
            List<GetChannelRes> getChannelRes = channelDao.getChannels();
            return getChannelRes;
        }
        catch (Exception exception) { //에러가 발생한 경우
            throw new BaseException(DATABASE_ERROR_CHANNELS_INFO); //데이터 베이스 오류를 던져줌
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 특정 채널 정보 얻어오기 - getChannel() */
    public GetChannelRes getChannel(int channelIdx) throws BaseException {   //UserComtroller.java에서 userIdx값을 받아옴.
        try {   //에러가 없다면
            GetChannelRes getChannelRes = channelDao.getChannel(channelIdx);  //userDao.getUser()에게 userIdx값을 그대로 넘겨줌
            return getChannelRes ;
        } catch (Exception exception) {    //에러가 있다면 (의미적 validation 처리)
            throw new BaseException(DATABASE_ERROR_CHANNELS_INFO);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 구독한 채널 정보 조회 - getSubscriptionInfo() */
    public List<GetChannelRes> getSubscriptionInfo(int channelIdx) throws BaseException {   //UserComtroller.java에서 userIdx값을 받아옴.
        try {   //에러가 없다면
            List<GetChannelRes> getChannelRes = channelDao.getSubscriptionInfo(channelIdx);  //userDao.getUser()에게 userIdx값을 그대로 넘겨줌
            return getChannelRes ;
        } catch (Exception exception) {    //에러가 있다면 (의미적 validation 처리)
            throw new BaseException(DATABASE_ERROR_CHANNELS_INFO);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 채널이 소유한 동영상 정보 얻어오기 - getMyVideos() */
    public List<GetMyVideo> getMyVideos(int channelIdx) throws BaseException {
        try {   //에러가 없다면
            List<GetMyVideo> getMyVideo = channelDao.getMyVideos(channelIdx);
            return getMyVideo;
        }
        catch (Exception exception) {    //에러가 있다면 (의미적 validation 처리)
            throw new BaseException(DATABASE_ERROR_VIDEO_INFO);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*좋아요 중복 체크 (활성화 상태 체크) - checkName() */
    public int checklike(int channelId, int videoId) throws BaseException{
        try{
            return channelDao.checklike(channelId, videoId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }












}
