package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {


//////////////////////////////////////////////////////////////////////////////////////////
    /*
    * 클라이언트 요청 성공_2XX => 2000
    */
    SUCCESS(true, 2000, "요청에 성공하였습니다."),



//////////////////////////////////////////////////////////////////////////////////////////
    /*
     * URL 리다이렉션_3XX => 3000
     */




//////////////////////////////////////////////////////////////////////////////////////////
    /*
     * 클라이언트측 요청 오류 (ex, 형식적 Validation)_4XX => 4000
     */
    /////////////////////user 도메안//////////////////////////

    REQUEST_ERROR(false, 4000, "입력값을 확인해주세요."),   //안쓰이는것 같은데?

    /*입력 필드 NULL 일때_4000~4100 (형식적 Validation) - 회원가입*/
    POST_USERS_EMPTY_EMAIL(false, 4001, "이메일을 입력해주세요."),
    POST_USERS_EMPTY_NICKNAME(false, 4002, "닉네임을 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 4003, "패스워드를 입력해주세요."),
//    POST_USERS_EMPTY_SYSTEMID(false, 4004, "시스템ID를 입력해주세요."),
    //USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    /*jwt 토큰 관련_4100~4120 (형식적 Validation)*/
    INVALID_JWT(false, 4100, "유효하지 않은 JWT입니다."),

    /*X-ACCESS-TOKEN(jwt) 입력 안함. (형식적 Validation) - 정보 조회, 수정    */
    EMPTY_JWT(false, 4101, "JWT를 입력해주세요."),

    /*jwt 토큰 비교 (형식적 Validation) - 정보 조회, 수정    */
    INVALID_USER_JWT(false,4102,"권한이 없는 유저의 접근입니다."),

    //로그아웃 관련//
    LOGOUT_USER_JWT(false, 4103, "로그아웃된 유저입니다."),


    //channel 도메인//
    POST_CHANNELS_EMPTY_NAME(false, 4010, "채널명을 입력해주세요."),
    POST_CHANNELS_EMPTY_COUNTRY(false, 4011, "소속 국가를 입력해주세요."),



   //youtube-premium 도메인
    POST_PRIMIUMS_EMPTY_CARDCOMPANY(false, 4020, "카드명을 입력해주세요."),
    POST_PRIMIUMS_EMPTY_CARDNUMBER(false, 4021, "카드 번호를 입력해주세요."),


    //videos 도메인
    POST_VIDEOS_EMPTY_VIDEO(false, 5116, "영상을 업로드 해주세요."),
    POST_VIDEOS_EMPTY_TITLE(false, 5117, "영상 제목을 입력 해주세요."),




/////////////////////////////////////////////////////////////////////////////////////////
    /*
     * 서버, DB측 응답 오류_ (ex, 의미적 Validation)_5XX => 5000
     */
    /////////////////////user 도메안//////////////////////////
    /*입력 필드 중복값_5000~5100(의미적 Varidation) - 회원가입*/
    POST_USERS_INVALID_EMAIL(false, 5000, "이메일 형식을 확인해주세요."),
    POST_USERS_INVALID_Password(false, 5000, "패스워드 형식을 확인해주세요."),


    POST_USERS_EXISTS_NICKNAME(false,5001,"이미 사용중인 닉네임입니다."),
    POST_USERS_EXISTS_EMAIL(false,5002,"이미 가입된 이메일입니다."),


    POST_USERS_JOIN_KAKAO(false,5003,"카카오 계정으로 신규 가입해 주세요."),

//    POST_USERS_EXISTS_EMAIL2(false,5004,"이미 가입된 이메일입니다."),


    POST_USERS_EXISTS_SYSTEMID(false,5005,"중복된 시스템ID입니다."),


    /*로그인 관련 5100~5150 */
    /* 로그인 실패 (의미적 Varidation) - 로그인 */
    INCORRECT_TO_PASSWORD(false,5100,"이메일 혹은 비밀번호가 틀렸습니다."),
    NOT_EXIST_EMAIL(false,5101,"존재하지 않는 이메일입니다."),


    /*유저 정보 변경 (의미적 Varidation)   - 회원 정보 변경 */
    INACTIVE_USER_STATUS(false,5102,"계정이 비활성화(삭제)된 상태입니다."),  //중복된다...




    /*유저 정보 변경 (의미적 Varidation)   - 회원 정보 변경 */
    MODIFY_FAIL_USERNAME(false,5102,"유저 닉네임 변경에 실패했습니다."),
    MODIFY_FAIL_PASSWORD(false,5103,"유저 패스워드 변경에 실패했습니다."),
    MODIFY_FAIL_IMAGE(false,5104,"유저 이미지 변경에 실패했습니다."),

    DELETE_FAIL_USER(false,5110,"유저 정보 삭제(비활성화)를 실패했습니다."),
    logout_FAIL_USER(false,5111,"이미 로그아웃 되었습니다."),
    auto_logout_FAIL_USER(false,5112,"아직 유저의 jwt 토큰이 만료되지 않아 자동 로그아웃에 실패했습니다."),



    /*토큰 저장 실패 */
    SAVE_FAIL_jwt(false,5999,"JWT 토큰 저장에 실패하였습니다."),


    /*패스워드 암복화 실패 (의미적 Varidation) - 회원가입 */
    PASSWORD_ENCRYPTION_ERROR(false, 5120, "비밀번호 암호화에 실패하였습니다."),

    /*패스워드 복호화 실패 (의미적 Varidation) - 로그인*/
    PASSWORD_DECRYPTION_ERROR(false, 5121, "비밀번호 복호화에 실패하였습니다."),



    /*DB와 서버관련_5200~5250
    /* 데이터 베이스 쿼리문 오류 (의미적 Varidation) */
    DATABASE_ERROR(false, 5201, "데이터베이스 연결에 실패하였습니다."),



    DATABASE_ERROR_CREATE_USER(false, 5202, "신규 유저 정보를 DB에 등록하지 못하였습니다."),
    DATABASE_ERROR_CHECK_USER_STATUS(false, 5203, "DB에서 활성화된 유저인지 파악을 하지 못하였습니다."),
    DATABASE_ERROR_CREATE_KAKAO_USER(false, 5204, "카카오 유저 정보를 DB에 등록하지 못하였습니다."),






//    DATABASE_ERROR2(false, 5201, "데이터베이스2 연결에 실패하였습니다."), //지우자
    EMAIL_NONE_USER_INFO(false,5205,"해당 이메일에 해당되는 유저가 없습니다."),  //중복된다...
    DATABASE_ERROR_USER_INFO(false, 5206, "DB에서 유저 정보를 불러오지 못하였습니다."),


    GET_FAIL_USER_JWT(false, 5207, "DB에서 해당 유저의 JWT를 가져오지 못하였습니다."),



    /* 서버 에러 */
    SERVER_ERROR(false, 5220, "서버와의 연결에 실패하였습니다."),  //안쓰이는데?







    //channel 도메인//
    POST_CHANNELS_EXISTS_NAME(false,5301,"중복된 채널명입니다."),
    DATABASE_ERROR_CREATE_CHANNEL(false, 5302, "신규 채널 정보를 DB에 등록하지 못하였습니다."),
    DATABASE_ERROR_CHANNELS_INFO(false, 5303, "DB에서 채널 정보를 불러오지 못하였습니다."),
//    MODIFY_FAIL_DESCRIPTION(false,5102,"채널 설명 변경에 실패했습니다."),  =>이제 안씀

    MODIFY_FAIL_CHANNEL_NAME(false,5304,"채널 명 변경에 실패했습니다."),
    MODIFY_FAIL_CHANNEL_DESCRIPTION(false,5305,"채널 설명 변경에 실패했습니다."),
    MODIFY_FAIL_CHANNEL_IMAGE(false,5306,"채널 이미지 변경에 실패했습니다."),

    DELETE_FAIL_CHANNEL(false,5310,"채널 정보 삭제(비활성화)를 실패했습니다."),
    SUBSCRIBE_FAIL_CHANNEL(false,5311,"채널 구독에 실패했습니다."),
    lIKE_FAIL_VIDEO(false,5312,"동영상 좋아요 기록 저장에 실패했습니다."),
    LIKE_EXISTS_VIDEO(false,5313,"좋아요를 이미 누르셨습니다."),


    //youtube-premium 도메인
    DATABASE_ERROR_JOIN_PREMIUM(false, 5401, "DB에 유튜브 프리미엄 가입 정보를 생성하지 못하였습니다."),
    DATABASE_ERROR_PREMIUM_INFO(false, 5402, "DB에서 유튜브 프리미엄 정보를 불러오지 못하였습니다."),
    DELETE_FAIL_PREMIUM(false,5403,"유튜브 프리미엄 가입 취소가 정상적으로 되지 않았습니다."),
//    POST_VIDEOS_EMPTY_THUMBNAIL(false, 5100, "썸네일을 업로드 해주세요."),  //null 허용으로 바꾸어서...

    MODIFY_FAIL_PREMIUM_FREETRAIL(false,5404,"프리미엄 무료 여부 변경에 실패했습니다."),
    MODIFY_FAIL_PREMIUM_CARDCOMPANY(false,5405,"프리미엄 카드 회사 변경에 실패했습니다."),
    MODIFY_FAIL_PREMIUM_CARDNUMBER(false,5406,"프리미엄 카드 번호 변경에 실패했습니다."),




    //videos 도메인
    DATABASE_ERROR_CREATE_VIDEO(false, 5501, "신규 동영상 정보를 DB에 등록하지 못하였습니다."),
    DATABASE_ERROR_VIDEO_INFO(false, 5502, "DB에서 동영상 정보를 불러오지 못하였습니다."),

    MODIFY_FAIL_VIDEO_THUMBNAIL(false,5503,"동영상 썸네일 변경에 실패했습니다."),
    MODIFY_FAIL_VIDEO_FILE(false,5504,"동영상 파일 변경에 실패했습니다."),
    MODIFY_FAIL_VIDEO_TITLE(false,5505,"동영상 제목 변경에 실패했습니다."),
    MODIFY_FAIL_VIDEO_DESCRIPTION(false,5506,"동영상 설명 변경에 실패했습니다."),
    MODIFY_FAIL_VIDEO_VIDEOOPEN(false,5507,"동영상 공개여부 변경에 실패했습니다."),













    DELETE_FAIL_VIDEO(false,5520,"동영상 정보 삭제(비활성화)를 실패했습니다."),









    ///////////////////////////////////////////////////////////////////////////////////////

    /* (의미적 Validation) */
    DUPLICATED_EMAIL(false, 5203, "중복된 이메일입니다."),   //이거 진짜 안쓰이는데????????
    // 안쓰이는데???
    RESPONSE_ERROR(false, 5204, "값을 불러오는데 실패하였습니다.");//지금 안쓰이는것 같은데??















////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 기타 오류 : 요청 성공
     */

////////////////////////////////////////////////////////////////////////////////////////







    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
