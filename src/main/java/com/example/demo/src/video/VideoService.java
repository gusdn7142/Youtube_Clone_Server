package com.example.demo.src.video;

import com.example.demo.config.BaseException;
import static com.example.demo.config.BaseResponseStatus.*;

import com.example.demo.src.channel.model.PatchChannelReq;
import com.example.demo.src.channel.model.PostChannelRes;
import com.example.demo.utils.JwtService;
import com.example.demo.src.video.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VideoService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final VideoDao videoDao;
    private final VideoProvider videoProvider;
    private final JwtService jwtService;


    @Autowired
    public VideoService(VideoDao videoDao, VideoProvider videoProvider, JwtService jwtService) {
        this.videoDao = videoDao;
        this.videoProvider = videoProvider;
        this.jwtService = jwtService;

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 생성 -  createVideo() */
    public PostVideoRes createVideo(PostVideoReq postVideoReq, int channelIdx) throws BaseException {

        //동영상 생성
        try{
            int vidoeIdx = videoDao.createVideo(postVideoReq,channelIdx);
            return new PostVideoRes(vidoeIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR_CREATE_VIDEO);
        }

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 정보 변경 - modifyvideo()  */
    public void modifyVideo(PatchVideoReq patchVideoReq) throws BaseException {


        //썸네일 변경
        if(patchVideoReq.getThumbNail() != null) {
            int result = videoDao.modifyThumbNail(patchVideoReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_VIDEO_THUMBNAIL);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //영상 변경
        if(patchVideoReq.getVideo() != null) {
            int result = videoDao.modifyVideo(patchVideoReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_VIDEO_FILE);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //제목 변경
        if(patchVideoReq.getTitle() != null) {
            int result = videoDao.modifyTitle(patchVideoReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_VIDEO_TITLE);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //설명 변경
        if(patchVideoReq.getDescription() != null) {
            int result = videoDao.modifyDescription(patchVideoReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_VIDEO_DESCRIPTION);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

        //공개 여부 변경
        if(patchVideoReq.getVideoOpen() != null) {
            int result = videoDao.modifyVideoOpen(patchVideoReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_VIDEO_VIDEOOPEN);   //result 값이 0 (DB에서 userName 값의 수정 실패시) 이면 에러코드 반환
            }
        }

    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 정보 삭제(비활성화) - deleteVideo()  */
    public void deleteVideo(PatchVideoReq patchVideoReq) throws BaseException {    //UserController.java에서 객체 값( id, nickName)을 받아와서...
        try{
            //채널 정보 삭제
            int result = videoDao.deleteVideo(patchVideoReq);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_VIDEO);   //채널 정보 삭제(비활성화)를 실패했습니다.
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);   //쿼리문 자체의 오류.
        }
    }











































}
