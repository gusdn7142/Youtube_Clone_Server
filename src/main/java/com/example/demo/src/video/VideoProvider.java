package com.example.demo.src.video;

import com.example.demo.src.video.model.*;
import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class VideoProvider {


    private final VideoDao videoDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public VideoProvider(VideoDao videoDao, JwtService jwtService) {
        this.videoDao = videoDao;
        this.jwtService = jwtService;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //모든 동영상 조회 - getVideos()
    public List<GetVideoRes> getVideos() throws BaseException {
        try {  //에러가 없으면 Dao에게 넘겨줌
            List<GetVideoRes> getVideoRes = videoDao.getVideos();
            return getVideoRes;
        } catch (Exception exception) { //에러가 발생한 경우
            throw new BaseException(DATABASE_ERROR_VIDEO_INFO); //데이터 베이스 오류를 던져줌
        }
    }

    ////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 특정 동영상 정보 얻어오기 - getVideo() */
    public GetVideoRes getVideo(int videoIdx) throws BaseException {   //UserComtroller.java에서 userIdx값을 받아옴.
        try {   //에러가 없다면
            GetVideoRes getVideoRes = videoDao.getVideo(videoIdx);  //userDao.getUser()에게 userIdx값을 그대로 넘겨줌
            return getVideoRes;
        } catch (Exception exception) {    //에러가 있다면 (의미적 validation 처리)
            throw new BaseException(DATABASE_ERROR_VIDEO_INFO);
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //동영상 페이징 조회 - getVideoPagings()
    public List<GetVideoRes> getVideoPagings() throws BaseException {

        try {
            List<GetVideoRes> getVideoRes = videoDao.getVideoPagings();
            return getVideoRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR_VIDEO_INFO); //데이터 베이스 오류를 던져줌
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //idx 받아와서 동영상 페이징 조회 - getVideoPagingsById()
    public List<GetVideoRes> getVideoPagingsById(int lastPagingId) throws BaseException {

        try {
        List<GetVideoRes> getVideoRes = videoDao.getVideoPagingsById(lastPagingId);
            return getVideoRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR_VIDEO_INFO); //데이터 베이스 오류를 던져줌
        }
        
    }








}
