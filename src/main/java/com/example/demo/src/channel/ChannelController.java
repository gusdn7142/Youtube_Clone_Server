package com.example.demo.src.channel;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import com.example.demo.src.channel.model.*;

import com.example.demo.utils.JwtService;
import com.example.demo.src.user.UserProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import org.springframework.web.context.request.RequestContextHolder;    //header에서 토큰 가져올떄 사용
import org.springframework.web.context.request.ServletRequestAttributes; //header에서 토큰 가져올떄 사용
import javax.servlet.http.HttpServletRequest; //header에서 토큰 가져올떄 사용





@RestController
@RequestMapping("/channels")

public class ChannelController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    //타입에 따라서 알앗거 Bean을 주입해준다. (자주 사용하는 객체를 Singleton 객체로 생성해놓고 어디서든 불러서 쓸 수 있는 것을 Spring 에서 Bean 이라는 이름을 붙인 것)
    private final ChannelProvider channelProvider;
    @Autowired
    private final ChannelService channelService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserProvider userProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)





    public ChannelController(ChannelProvider channelProvider, ChannelService channelService, JwtService jwtService, UserProvider userProvider) {
        this.channelProvider = channelProvider;
        this.channelService = channelService;
        this.jwtService = jwtService;
        this.userProvider = userProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 채널 생성 (채널 가입) API
     * [POST] /channels/:userId
     * @return BaseResponse<PostChannelRes>  => idx 값 리턴
     */

    /* 채널 가입(생성) -  createChannel() */
    @ResponseBody
    @PostMapping("/{userId}")
    public BaseResponse<PostChannelRes> createChannel(@PathVariable("userId") int userIdx, @RequestBody PostChannelReq postChannelReq) {


        try {


            //채널 입력 필드가 NULL인지 검사 (형식적 Validation)
            if (postChannelReq.getName() == null) {
                return new BaseResponse<>(POST_CHANNELS_EMPTY_NAME);
            }
            //소속국가 입력 필드가 NULL인지 검사 (형식적 Validation)
            else if (postChannelReq.getCountry() == null) {
                return new BaseResponse<>(POST_CHANNELS_EMPTY_COUNTRY);
            }

            //Http body에 필수 칼럼들이 모두 입력이 되었다면.
            //userService 클래스의  createChannel() 함수로 입력한 값이 저장되어 있는 postChannelReq를 전송
            PostChannelRes postChannelRes = channelService.createChannel(postChannelReq, userIdx);
            return new BaseResponse<>(postChannelRes);


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));  //그냥 에러
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 모든 채널 조회 API
     * [GET] /channels
     * 시스템 ID로 검색 조회 가능 API
     * [GET] /users? systemId=
     * @return BaseResponse<List<GetUserRes>>
     */

    /* GET 방식 - Query String (쿼리 스트링)*/
    @ResponseBody   //JSON 혹은 xml 로 요청에 응답할수 있게 해주는 Annotation
    @GetMapping("")
    public BaseResponse<List<GetChannelRes>> getChannels() {
        try{
            List<GetChannelRes> getChannelRes = channelProvider.getChannels();   //전체 유저 조회


            return new BaseResponse<>(getChannelRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 특정 채널 정보 조회 API
     * [GET] /channels/:id
     * @return BaseResponse<GetUserRes>
     */

    /* GET 방식 - Path-variable (패스 베리어블) */
    @ResponseBody
    @GetMapping("/{id}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetChannelRes> getChannel(@PathVariable("id") int channelIdx) {              //BaseResponse<GetUserRes>

        try {

            //특정 채널 정보 얻어오기 - getChannel()
            GetChannelRes getChannelRes = channelProvider.getChannel(channelIdx);
            return new BaseResponse<>(getChannelRes);


        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    /**
//     * 짭.... 채널 특정 정보 변경 API
//     * [PATCH] /channels/:Id
//     * @return BaseResponse<String>
//     */
//
//    @ResponseBody
//    @PatchMapping("/{id}")
//    public BaseResponse<String> modifyDescription(@PathVariable("id") int channelIdx, @RequestBody Channel channel){  //@PatchUserReq patchUserReq으로 변경 가능
//
//
//        try {
//            //접근 제한 부분 성공 (이제 로그인된 사용자[토큰을 가진 사용자]만 채널을 가입할 수 있다)       --추후에 적용!!!!!!!
////            //DB에서 사용자의 JWT 추출
////            String jwt = userProvider.getUserToken(userIdx);
////
////            //가져온 토큰에서 Idx 추출
////            int userIdxByJwt = jwtService.getUserIdx2(jwt);
////
////            //userIdx와 접근한 유저가 같은지 확인
////            if (userIdx != userIdxByJwt) {
////                return new BaseResponse<>(INVALID_USER_JWT);
////            }
//
//
//
//            //채널 정보를 patchChannelReq 객체에 넣고
//            PatchChannelReq patchChannelReq = new PatchChannelReq(channelIdx, null, null, channel.getDescription());
//
//            //채널 정보 변경!
//            channelService.modifyDescription(patchChannelReq);
//
//            String result = "채널 정보 변경이 완료되었습니다.";   //정보 변경 성공시 메시지 지정
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
//        }
//    }


    /**
     * 채널 정보 변경 API
     * [PATCH] /channels/:Id
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}")
    public BaseResponse<String> modifyInfo(@PathVariable("id") int channelIdx, @RequestBody PatchChannelReq patchChannelReq){  //@PatchUserReq patchUserReq으로 변경 가능


        try {


            //채널 정보를 patchChannelReq 객체에 넣고
            patchChannelReq.setChannelIdx(channelIdx);


            //채널 정보 변경!
            channelService.modifyInfo(patchChannelReq);



            String result = "채널 정보 변경이 완료되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 채널 정보 삭제 (비활성화 - 현재 로그인한 상태에서만 가능) API
     * [PATCH] /channels/:Id/status
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{id}/status")
    public BaseResponse<String> deleteChannel(@PathVariable("id") int channelIdx){   //BaseResponse<String>

        try {



            //채널의 idx를 받아와 객체에 저장
            PatchChannelReq patchChannelReq = new PatchChannelReq(channelIdx,null,null,null);
//            PatchChannelReq patchChannelReq = null;
//            patchChannelReq.setChannelIdx(channelIdx);  => 실행시 값이 안들어감!!

            System.out.println(patchChannelReq.getChannelIdx());


            //채널 정보 삭제 (비활성화)
            channelService.deleteChannel(patchChannelReq);

            String result = "채널 상태가 삭제(비활성화) 되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }

    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 구독한 채널 조회 API
     * [GET] /channels/:id/subscription-info
     * @return BaseResponse<GetUserRes>
     */

    /* GET 방식  */
    @ResponseBody
    @GetMapping("/{id}/subscription-info") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse <List<GetChannelRes>> getSubscriptionInfo(@PathVariable("id") int channelIdx) {              //BaseResponse<GetUserRes>

        try {
            //구독한 채널 정보 얻어오기 - getChannel()
            List<GetChannelRes> getChannelRes = channelProvider.getSubscriptionInfo(channelIdx);
            return new BaseResponse<>(getChannelRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 채널이 소유한 동영상 정보 조회 API
     * [GET] /channels/:id/videos
     * @return BaseResponse<GetUserRes>
     */

    /* GET 방식 - Path-variable (패스 베리어블) */
    @ResponseBody
    @GetMapping("{id}/videos")
    public BaseResponse<List<GetMyVideo>> getMyVideos (@PathVariable("id") int channelIdx  ) {   //BaseResponse<GetUserRes>


        try {

            //유저가 소유한 채널 정보 얻기 - getMyVideos()
            List<GetMyVideo> getMyVideo = channelProvider.getMyVideos(channelIdx);
            return new BaseResponse<>(getMyVideo);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 채널 구독 (채널이 채널을 구독) API
     * [POST] /channels/:id/subscribes/:channelId
     * @return BaseResponse<PostChannelRes>  => idx 값 리턴
     */

    /* 채널 구독 -  subscribeChannel() */
    @ResponseBody
    @PostMapping("/{id}/subscribes/{channelId}")
    public BaseResponse<String> subscribeChannel(@PathVariable("id") int id, @PathVariable("channelId") int channelId) { //구독자, 구독당한자.


        try {

            //Http body에 필수 칼럼들이 모두 입력이 되었다면.
            //userService 클래스의  createChannel() 함수로 입력한 값이 저장되어 있는 postChannelReq를 전송
            channelService.subscribeChannel(id, channelId);


            String result = "채널 구독에 성공하였습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 채널이 동영상 좋아요 표시 (채널이 채널을 구독) API
     * [POST] /channels/:id/video-likes/videoId
     * @return BaseResponse<PostChannelRes>  => idx 값 리턴
     */

    /* 동영상 좋아요 표시 -  likeVideo() */
    @ResponseBody
    @PostMapping("/{id}/video-likes/{videoId}")
    public BaseResponse<String> likeVideo (@PathVariable("id") int channelId, @PathVariable("videoId") int videoId) { //구독자, 구독당한자.


        try {

            //Http body에 필수 칼럼들이 모두 입력이 되었다면.
            //userService 클래스의  createChannel() 함수로 입력한 값이 저장되어 있는 postChannelReq를 전송
            channelService.likeVideo(channelId, videoId);


            String result = "해당 영상에 좋아요가 표시가 반영되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }
    }


























}
