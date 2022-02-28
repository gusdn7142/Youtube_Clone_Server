package com.example.demo.src.video;

import com.example.demo.src.channel.model.PatchChannelReq;
import com.example.demo.utils.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.src.video.model.*;

import org.springframework.web.bind.annotation.*;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import static com.example.demo.config.BaseResponseStatus.*;

import com.example.demo.src.user.UserProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)
import org.springframework.web.context.request.RequestContextHolder;    //header에서 토큰 가져올떄 사용
import org.springframework.web.context.request.ServletRequestAttributes; //header에서 토큰 가져올떄 사용
import javax.servlet.http.HttpServletRequest; //header에서 토큰 가져올떄 사용

import java.util.List;


@RestController
@RequestMapping("/videos")

public class VideoController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    //타입에 따라서 알앗거 Bean을 주입해준다. (자주 사용하는 객체를 Singleton 객체로 생성해놓고 어디서든 불러서 쓸 수 있는 것을 Spring 에서 Bean 이라는 이름을 붙인 것)
    private final VideoProvider videoProvider;
    @Autowired
    private final VideoService videoService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserProvider userProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)




    public VideoController(VideoProvider videoProvider, VideoService videoService, JwtService jwtService, UserProvider userProvider) {
        this.videoProvider = videoProvider;
        this.videoService = videoService;
        this.jwtService = jwtService;
        this.userProvider = userProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 동영상 생성 API
     * [POST] /videos/:channelId
     * @return BaseResponse<PostChannelRes>  => idx 값 리턴
     */

    /* 동영상 생성 -  createChannel() */
    @ResponseBody
    @PostMapping("/{channelId}")
    public BaseResponse<PostVideoRes> createVideo(@PathVariable("channelId") int channelIdx, @RequestBody PostVideoReq postVideoReq) {


        try {
//            //썸네일 입력 필드가 NULL인지 검사 (형식적 Validation)    //썸네일 null 허용으로 바꾸었다!
//            if (postVideoReq.getThumbNail() == null) {
//                return new BaseResponse<>(POST_VIDEOS_EMPTY_THUMBNAIL);
//            }
            //동영상 입력 필드가 NULL인지 검사 (형식적 Validation)
            if (postVideoReq.getVideo() == null) {
                return new BaseResponse<>(POST_VIDEOS_EMPTY_VIDEO);
            }
            //영상 제목 입력 필드가 NULL인지 검사 (형식적 Validation)
            else if (postVideoReq.getTitle() == null) {
                return new BaseResponse<>(POST_VIDEOS_EMPTY_TITLE);
            }



            //동영상 생성
            PostVideoRes PostVideoRes = videoService.createVideo(postVideoReq, channelIdx);
            return new BaseResponse<>(PostVideoRes);


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));  //그냥 에러
        }
    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 모든 동영상 조회 API
     * [GET] /videos
     * @return BaseResponse<List<GetUserRes>>
     */

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetVideoRes>> getVideos() {
        try{
            List<GetVideoRes> getVideoRes = videoProvider.getVideos();   //전체 유저 조회


            return new BaseResponse<>(getVideoRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 특정 동영상 정보 조회 API
     * [GET] /videos/:id
     * @return BaseResponse<GetUserRes>
     */

    /* GET 방식 - Path-variable (패스 베리어블) */
    @ResponseBody
    @GetMapping("/{id}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetVideoRes> getVideo(@PathVariable("id") int videoIdx) {              //BaseResponse<GetUserRes>

        try {

            //특정 동영상 정보 얻어오기 - getVideo()
            GetVideoRes getVideoRes = videoProvider.getVideo(videoIdx);
            return new BaseResponse<>(getVideoRes);


        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 동영상 정보 변경 API
     * [PATCH] /videos/:id
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}")
    public BaseResponse<String> modifyVideo(@PathVariable("id") int videolIdx, @RequestBody PatchVideoReq patchVideoReq){  //@PatchUserReq patchUserReq으로 변경 가능


        try {


            //채널 정보를 patchChannelReq 객체에 넣고
            patchVideoReq.setVideoId(videolIdx);


            //채널 정보 변경!
            videoService.modifyVideo(patchVideoReq);



            String result = "채널 정보 변경이 완료되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 동영상 정보 삭제 (비활성화)  API
     * [PATCH] /videos/:id/status
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}/status")
    public BaseResponse<String> deleteVideo(@PathVariable("id") int videolIdx){   //BaseResponse<String>

        try {


            //동영상의 idx를 받아와 객체에 저장
            PatchVideoReq patchVideoReq = new PatchVideoReq(videolIdx,null,null,null,null, 0);



            //채널 정보 삭제 (비활성화)
            videoService.deleteVideo(patchVideoReq);

            String result = "동영상이 삭제(비활성화) 되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }

    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 동영상 페이징 조회 API
     * [GET] /videos/paging/:lastPagingId
     * @return BaseResponse<List<GetUserRes>>
     */

    @ResponseBody
    @GetMapping("/paging")
    public BaseResponse<GetVideoPagingRes> getVideoPagings(@RequestParam(required = false) Integer lastPagingId) {  //int 형은 null이 불가능 하기 때문에 Integer로 변경,  BaseResponse<List<GetVideoRes>>

        try{
            List<GetVideoRes> getVideoRes;

            /* 클라이언트에서 받아온 lastPagingId로 paging 하여 동영상 조회 */
            if(lastPagingId != null){
                getVideoRes = videoProvider.getVideoPagingsById(lastPagingId);   
            }
            /* lastPagingId 안했을 경우 마지막 index 번호로 paging 하여 동영상 조회 */
            else {
                getVideoRes = videoProvider.getVideoPagings();
            }

            //마지막 영상 페이징 인덱스 번호를 불러옴
            int lastPagingIndex = 0;
            for(int i=0; i<getVideoRes.size(); i++){               //Araay list의 길이인 getVideoRes.size()
                lastPagingIndex = getVideoRes.get(i).getId();     //불러온 영상중 마지막 영상의 idx 값을 가져옴
            }

            //lastPagingIndex (마지막 페이징 인덱스)와 getVideoRes (영상 정보 객체)를 새로운 객체에 저장
            GetVideoPagingRes getVideoPagingRes = new GetVideoPagingRes(lastPagingIndex, getVideoRes);

            return new BaseResponse<>(getVideoPagingRes);


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }








































}
