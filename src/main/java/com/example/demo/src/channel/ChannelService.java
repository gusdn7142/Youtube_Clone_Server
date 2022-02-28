package com.example.demo.src.channel;


import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import com.example.demo.src.channel.model.*;
import com.example.demo.utils.AES128;


import com.example.demo.config.secret.Secret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.example.demo.config.BaseResponseStatus.*;





@Service
public class ChannelService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChannelDao channelDao;
    private final ChannelProvider channelProvider;
    private final JwtService jwtService;


    @Autowired
    public ChannelService(ChannelDao channelDao, ChannelProvider channelProvider, JwtService jwtService) {
        this.channelDao = channelDao;
        this.channelProvider = channelProvider;
        this.jwtService = jwtService;

    }




//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 채널 가입(생성) -  createChannel() */
    public PostChannelRes createChannel(PostChannelReq postChannelReq, int userIdx) throws BaseException {

        //채널명 중복 검사 (의미적 Varidation 처리 - 조회는 Provider)
        if(channelProvider.checkName(postChannelReq.getName()) ==1){              //닉네임이 중복이 되면 결과값인 1과 매핑이 되어 중복 여부를 판단 가능
            throw new BaseException(POST_CHANNELS_EXISTS_NAME);                   //'이미 존재하는 채널명입니다.'
        }

        //채널 가입(생성)
        try{
            int channelIdx = channelDao.createChannel(postChannelReq,userIdx);
            return new PostChannelRes(channelIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR_CREATE_CHANNEL);  //채널 생성 실패 에러 (고쳐야함)
        }

    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /* 짭.. 채널 정보 변경 - modifyDescription()  */
//    public void modifyDescription(PatchChannelReq patchChannelReq) throws BaseException {
//        try{
//
//            //채널 정보 변경
//            int result = channelDao.modifyDescription(patchChannelReq);
//            if(result == 0){
//                throw new BaseException(MODIFY_FAIL_CHANNEL_INFO);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
//            }
//        } catch(Exception exception){
//            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
//        }
//    }

    /* 채널 정보 변경 - modifyInfo()  */
    public void modifyInfo(PatchChannelReq patchChannelReq) throws BaseException {

        //채널 명 변경
        if(patchChannelReq.getName() != null) {
            int result = channelDao.modifyName(patchChannelReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_CHANNEL_NAME);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //채널 설명 변경
        if(patchChannelReq.getDescription() != null) {
            int result = channelDao.modifyDescription(patchChannelReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_CHANNEL_DESCRIPTION);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //채널 이미지 변경
        if(patchChannelReq.getImage() != null) {
            int result = channelDao.modifyImage(patchChannelReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_CHANNEL_IMAGE);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 채널 정보 삭제(비활성화) - deleteChannel()  */
    public void deleteChannel(PatchChannelReq patchChannelReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...
        try{
            //채널 정보 삭제
            int result = channelDao.deleteChannel(patchChannelReq);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_CHANNEL);   //채널 정보 삭제(비활성화)를 실패했습니다.
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
        }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 채널 구독 - subscribeChannel()  */
    public void subscribeChannel(int id, int channelId) throws BaseException {
        try{
            //채널 구독
            int result = channelDao.subscribeChannel(id,channelId );
            if(result == 0){
                throw new BaseException(SUBSCRIBE_FAIL_CHANNEL);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 좋아요 표시 -  likeVideo() */
    public void likeVideo(int channelId, int videoId) throws BaseException {

        //동영상 좋아요 중복 체크 (활성화 상태 체크)
        if(channelProvider.checklike(channelId, videoId) ==1){             
            throw new BaseException(LIKE_EXISTS_VIDEO);                   //'좋아요를 이미 누르셨습니다.'
        }
        
        try{
            //동영상 좋아요 표시
            int result = channelDao.likeVideo(channelId,videoId);

            System.out.println(result);

            if(result == 0){
                throw new BaseException(lIKE_FAIL_VIDEO);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
        }
    }


































}
