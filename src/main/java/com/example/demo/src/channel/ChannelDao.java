package com.example.demo.src.channel;


import com.example.demo.src.channel.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import javax.sql.DataSource;
import java.util.List;




@Repository
public class ChannelDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 채널 가입(생성) -  createChannel() */
    public int createChannel(PostChannelReq postChannelReq, int userIdx){  //UserServcie.java에서 postUserReq 객체를 받아옴

        //쿼리문 생성
        String createChannelQuery = "INSERT INTO CHANNELS (NAME, URL, IMAGE, DESCRIPTION, SYSTEM_ID, COUNTRY, USER_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        //칼럼 셋팅 (Url과 SystemId는 임의생성)
        postChannelReq.setUrl("https://youtube.com/channel/" + postChannelReq.getName()); //시스템_ID 생성
        postChannelReq.setSystemId("CHANNEL-" + postChannelReq.getName() ); //시스템_ID 생성

        Object[] createChannelParams = new Object[]{postChannelReq.getName(), postChannelReq.getUrl(), postChannelReq.getImage(), postChannelReq.getDescription(), postChannelReq.getSystemId(), postChannelReq.getCountry(), userIdx  };  //postUserReq의 변수명과 유사해야함

        //쿼리문 수행 (유저 생성)
        this.jdbcTemplate.update(createChannelQuery, createChannelParams);

        //Id 값을 반환
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*채널명 중복 체크 (채널 가입시) - checkName() */
    public int checkName(String name){
        //쿼리문 생성
        String checkNameQuery = "select exists(select NAME from CHANNELS where NAME = ?)";
        String checkNameParams = name;

        //쿼리문 실행(0,1 반환)
        return this.jdbcTemplate.queryForObject(checkNameQuery,
                int.class,
                checkNameParams); //int형으로 쿼리 결과를 넘겨줌 (0,1)
    }
/////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*모든 채널 정보 가져오기 - getChannels() */
    public List<GetChannelRes> getChannels(){                  //lIST 형식으로 받아옴
        String getChannelQuery = "select * from CHANNELS";
        return this.jdbcTemplate.query(getChannelQuery,
                (rs,rowNum) -> new GetChannelRes(                  //객체가 만들어지면서 다음 값을 받아옴.
                        rs.getInt("ID"),                      //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getString("NAME"),
                        rs.getString("URL"),
                        rs.getString("IMAGE"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("SYSTEM_ID"),
                        rs.getInt("VIEWS"),
                        rs.getInt("SUBSCRIBER_COUNT"),
                        rs.getString("COUNTRY"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT"),
                        rs.getInt("USER_ID"))
        );
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 특정 채널 정보 얻어오기 - getChannel() */
    public GetChannelRes getChannel(int channelIdx){     //UserProvider.java에서 userIdx값을 받아옴.
        //쿼리문 생성
        String getChannelQuery = "select * from CHANNELS where ID = ?";   //쿼리문 실행 ('userIdx' 값으로 조회)
        int getChannelParams = channelIdx;      //파라미터(id) 값 저장

        //쿼리문 실행
        return this.jdbcTemplate.queryForObject(getChannelQuery,          //하나의 행을 불러오기 때문에 jdbcTemplate.queryForObject 실행
                (rs, rowNum) -> new GetChannelRes(    //rs.getString(" ")값이 DB와 일치해야 한다!
                        rs.getInt("ID"),                      //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getString("NAME"),
                        rs.getString("URL"),
                        rs.getString("IMAGE"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("SYSTEM_ID"),
                        rs.getInt("VIEWS"),
                        rs.getInt("SUBSCRIBER_COUNT"),
                        rs.getString("COUNTRY"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT"),
                        rs.getInt("USER_ID")),
                getChannelParams);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 채널 명 변경 - modifyName()  */
    public int modifyName(PatchChannelReq patchChannelReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String modifyNameQuery = "update CHANNELS set NAME = ? where ID = ? AND STATUS = 1 ";

        //객체에 저장
        Object[] modifyNameParams = new Object[]{patchChannelReq.getName(), patchChannelReq.getChannelIdx()};

        //쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyNameQuery,modifyNameParams);
    }

    /* 채널 설명 변경 - modifyDescription()  */
    public int modifyDescription(PatchChannelReq patchChannelReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String modifyDescriptionQuery = "update CHANNELS set DESCRIPTION = ? where ID = ? AND STATUS = 1 ";

        //객체에 저장
        Object[] modifyDescriptionParams = new Object[]{patchChannelReq.getDescription(), patchChannelReq.getChannelIdx()};

        //쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyDescriptionQuery,modifyDescriptionParams);
    }


    /* 채널 이미지 변경 - modifyImage()  */
    public int modifyImage(PatchChannelReq patchChannelReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String modifyImageQuery = "update CHANNELS set IMAGE = ? where ID = ? AND STATUS = 1 ";

        //객체에 저장
        Object[] modifyImageParams = new Object[]{patchChannelReq.getImage(), patchChannelReq.getChannelIdx()};

        //쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyImageQuery,modifyImageParams);
    }













//    /* 짭... 채널 정보 변경 - modifyInfo()  */
//    public int modifyInfo(PatchChannelReq patchChannelReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
//
//        //쿼리문 생성 및 객체 생성
//        String modifyInfoQuery;
//        Object[] modifyInfoParams;
//
//        if(patchChannelReq.getName() == null) {  //채널명(필수)이 안들어 왔을떄.. 설명과 이미지는 NULL 가능
//            modifyInfoQuery = "update CHANNELS set DESCRIPTION = ?, IMAGE = ? where ID = ?";  //설명과 이미지는 NULL이여도 된다.
//            modifyInfoParams = new Object[]{patchChannelReq.getDescription(), patchChannelReq.getImage(), patchChannelReq.getChannelIdx()};
//        }
//        else { //채널(필수)명이 들어왔을때, 설명과 이미지는 NULL 가능
//            modifyInfoQuery = "update CHANNELS set NAME = ?, DESCRIPTION = ?, IMAGE = ? where ID = ?";  //설명과 이미지는 NULL이여도 된다.
//            modifyInfoParams = new Object[]{patchChannelReq.getName(), patchChannelReq.getDescription(), patchChannelReq.getImage(), patchChannelReq.getChannelIdx()};
//        }
//
//        //채널 정보 변경 쿼리문 수행 (0,1로 반환됨)
//        return this.jdbcTemplate.update(modifyInfoQuery,modifyInfoParams);
//    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 채널 정보 삭제(비활성화) - deleteChannel()  */
    public int deleteChannel(PatchChannelReq patchChannelReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String deleteChannelQuery = "update CHANNELS set STATUS = 0 where ID = ? ";

        //idx를 변수에 저장
        int deleteChannelParams = patchChannelReq.getChannelIdx();

        //유저 삭제(비활성화) 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(deleteChannelQuery,deleteChannelParams);
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 구독한 채널 정보 얻기 - getSubscriptionInfo() */
    public List<GetChannelRes> getSubscriptionInfo(int channelIdx){     //UserProvider.java에서 userIdx값을 받아옴.
        //쿼리문 생성
        String getSubscriptionInfoQuery = "SELECT CHANNELS.* FROM SUBSCRIBES JOIN CHANNELS\n" +
                                          "ON SUBSCRIBES.CHANNEL_SYSTEM_ID = CHANNELS.SYSTEM_ID\n" +
                                          "WHERE SUBSCRIBES.CHANNEL_ID = ?";
        int getSubscriptionInfoParams = channelIdx;      //파라미터(id) 값 저장

        //쿼리문 실행
        return this.jdbcTemplate.query(getSubscriptionInfoQuery,
                (rs, rowNum) -> new GetChannelRes(    //rs.getString(" ")값이 DB와 일치해야 한다!
                        rs.getInt("ID"),                      //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getString("NAME"),
                        rs.getString("URL"),
                        rs.getString("IMAGE"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("SYSTEM_ID"),
                        rs.getInt("VIEWS"),
                        rs.getInt("SUBSCRIBER_COUNT"),
                        rs.getString("COUNTRY"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT"),
                        rs.getInt("USER_ID")),
                getSubscriptionInfoParams);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 채널이 소유한 동영상 정보 얻기 - getMyVideos() */
    public List<GetMyVideo> getMyVideos(int channelIdx){     //UserProvider.java에서 userIdx값을 받아옴.

        //쿼리문 생성
        String getMyVideosQuery = "select * from VIDEOS where CHANNEL_ID = ?";   //쿼리문 실행 ('userIdx' 값으로 조회)
        int getMyVideosParams = channelIdx;      //파라미터(id) 값 저장

        //쿼리문 실행
        return this.jdbcTemplate.query(getMyVideosQuery,
                (rs, rowNum) -> new GetMyVideo(    //rs.getString(" ")값이 DB와 일치해야 한다!
                        rs.getInt("ID"),                      //각 칼럼은 DB와 매칭이 되어야 한다
                        rs.getString("THUMB_NAIL"),
                        rs.getString("VIDEO"),
                        rs.getString("TITLE"),
                        rs.getString("DESCRIPTION"),
                        rs.getTimestamp("UPLOAD_DATE"),
                        rs.getInt("VIDEO_OPEN"),
                        rs.getInt("LIKE_COUNT"),
                        rs.getInt("HATE_COUNT"),
                        rs.getInt("VIEWS"),
                        rs.getInt("COMMENT_COUNT"),
                        rs.getInt("STATUS"),
                        rs.getTimestamp("CREATE_AT"),
                        rs.getTimestamp("UPDATE_AT"),
                        rs.getInt("CHANNEL_ID")),
                getMyVideosParams);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 채널 구독 - subscribeChannel()  */
    public int subscribeChannel(int id, int channelId){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String subscribeChannelQuery = "insert into SUBSCRIBES (channel_system_id, channel_id) values ((select SYSTEM_ID from CHANNELS where id = ?),?)";
        Object[] subscribeChannelParams = new Object[]{channelId, id};  //구독 당한자, 구독자


        //채널 정보 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(subscribeChannelQuery,subscribeChannelParams);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 좋아요 표시 -  likeVideo() */
    public int likeVideo(int channelId, int videoId){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String likeVideoQuery = "insert into VIDEO_LIKES (VIDEO_ID, CHANNEL_ID) values (?,?)";
        Object[] likeVideoParams = new Object[]{videoId, channelId};  //좋아요 당한 영상, 좋아요 누른 채널


        //채널 정보 변경 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(likeVideoQuery,likeVideoParams);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*좋아요 중복 체크 (활성화 상태 체크) - checkName() */
    public int checklike(int channelId, int videoId){

        //쿼리문 생성
        String checkNameQuery = "select exists(select * from VIDEO_LIKES where VIDEO_ID =? and CHANNEL_ID =? and STATUS = 1)";
//        String checkNameParams = name;


        //쿼리문 실행(0,1 반환)
        return this.jdbcTemplate.queryForObject(checkNameQuery,
                int.class,
                videoId,
                channelId); //int형으로 쿼리 결과를 넘겨줌 (0,1)
    }






















}
