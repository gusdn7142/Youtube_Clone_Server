package com.example.demo.src.youtube_premium;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.youtube_premium.model.*;
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
@RequestMapping("/youtube-premiums")

public class PremiumController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    //타입에 따라서 알앗거 Bean을 주입해준다. (자주 사용하는 객체를 Singleton 객체로 생성해놓고 어디서든 불러서 쓸 수 있는 것을 Spring 에서 Bean 이라는 이름을 붙인 것)
    private final PremiumProvider premiumProvider;
    @Autowired
    private final PremiumService premiumService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserProvider userProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)



    public PremiumController(PremiumProvider premiumProvider, PremiumService premiumService, JwtService jwtService, UserProvider userProvider) {
        this.premiumProvider = premiumProvider;
        this.premiumService = premiumService;
        this.jwtService = jwtService;
        this.userProvider = userProvider;  //DB에서 토큰을 꺼내와 확인하기 위해 사용 (접근 제한에 사용)
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 유튜브 프리미엄 가입 API
     * [POST] /channels/:id
     * @return BaseResponse<PostChannelRes>  => idx 값 리턴
     */

    /* 유튜브 프리미엄 가입 -  joinPremium() */
    @ResponseBody
    @PostMapping("/{userId}")
    public BaseResponse<PostPremiumRes> joinPremium (@PathVariable("userId") int userIdx, @RequestBody PostPremiumReq postPremiumReq) {


        try {

            //카드명 입력 필드가 NULL인지 검사 (형식적 Validation)
            if (postPremiumReq.getCardCompany() == null) {
                return new BaseResponse<>(POST_PRIMIUMS_EMPTY_CARDCOMPANY);
            }
            //카드번호 입력 필드가 NULL인지 검사 (형식적 Validation)
            else if (postPremiumReq.getCardNumber() == null) {    //Intger형이라서 null값 체크가 된다!! ㄴㄴㄴ
                return new BaseResponse<>(POST_PRIMIUMS_EMPTY_CARDNUMBER);
            }


            //유튜브 프리미엄 가입
            PostPremiumRes postPremiumRes = premiumService.joinPremium(postPremiumReq, userIdx);
            return new BaseResponse<>(postPremiumRes);


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));  //그냥 에러
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 프리미엄 전체 가입자 조회 API
     * [GET] /youtube-premiums
     * 시스템 ID로 검색 조회 가능 API
     * @return BaseResponse<List<GetUserRes>>
     */

    @ResponseBody   //JSON 혹은 xml 로 요청에 응답할수 있게 해주는 Annotation
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<List<GetPremiumRes>> getPremiumUsers() {
        try{
            List<GetPremiumRes> getPremiumRes = premiumProvider.getPremiumUsers();   //전체 유저 조회


            return new BaseResponse<>(getPremiumRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 프리미엄 가입 여부 확인 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */

    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<GetPremiumRes> getPremiumUser(@PathVariable("userId") int userIdx) {              //BaseResponse<GetUserRes>

        //접근 제한 부분
        try {


            //프리미엄 가입여부 확인
            GetPremiumRes getPremiumRes = premiumProvider.getPremiumUser(userIdx);
            return new BaseResponse<>(getPremiumRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 프리미엄 결제 정보 변경 API
     * [PATCH] /youtube-premium/payment/:usersId
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/payment/{userId}")
    public BaseResponse<String> modifyPayment(@PathVariable("userId") int userIdx, @RequestBody PatchPremiumReq patchPremiumReq){


        try {

            //유저 정보를 객체에 넣음
            patchPremiumReq.setUserId(userIdx);

            //유저 정보 변경
            premiumService.modifyPayment(patchPremiumReq);  //userService.java로 patchUserReq객체 값 전송



            String result = "결제 정보가 정상적으로 변경되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 프리미엄 가입 취소 (비활성화) API
     * [PATCH] /youtube-premium/status/:userId
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/status/{userId}")
    public BaseResponse<String> deletePremium(@PathVariable("userId") int userIdx){   //BaseResponse<String>

        try {

            //userIdx를 실어 보내기 위해 저장
            PatchPremiumReq patchPremiumReq = new PatchPremiumReq(0,null,null,userIdx);

            //유저 정보 삭제 (비활성화)
            premiumService.deletePremium(patchPremiumReq);  //userService.java로 patchUserReq객체 값 전송


            String result = "유튜브 프리미엄 가입 해지(비활성화) 되었습니다.";   //정보 변경 성공시 메시지 지정
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));  //어떤상황에서?.. 오류 뱉어줌
        }

    }







































}
