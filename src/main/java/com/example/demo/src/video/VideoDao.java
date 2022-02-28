package com.example.demo.src.video;

import com.example.demo.src.channel.model.PatchChannelReq;
import com.example.demo.src.channel.model.PostChannelReq;
import com.example.demo.src.video.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import javax.sql.DataSource;
import java.util.List;


@Repository
public class VideoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 생성 -  createVideo() */
    public int createVideo(PostVideoReq postVideoReq, int channelIdx){  //UserServcie.java에서 postUserReq 객체를 받아옴

        //쿼리문 생성
        String createVideoQuery = "INSERT INTO VIDEOS (THUMB_NAIL, VIDEO, TITLE, DESCRIPTION, VIDEO_OPEN, CHANNEL_ID) VALUES (?, ?, ?, ?, ?, ?)";
        Object[] createVideoParams = new Object[]{postVideoReq.getThumbNail(), postVideoReq.getVideo(), postVideoReq.getTitle(), postVideoReq.getDescription(), postVideoReq.getVideoOpen(), channelIdx  };  //postUserReq의 변수명과 유사해야함

        //쿼리문 수행 (유저 생성)
        this.jdbcTemplate.update(createVideoQuery, createVideoParams);

        //Id 값을 반환
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);

    }




//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*모든 동영상 정보 가져오기 - getVideos() */
    public List<GetVideoRes> getVideos(){                  //lIST 형식으로 받아옴
        String getVideosQuery = "select * from VIDEOS";
        return this.jdbcTemplate.query(getVideosQuery,
                (rs,rowNum) -> new GetVideoRes(                  //객체가 만들어지면서 다음 값을 받아옴.
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
                        rs.getInt("CHANNEL_ID"))
        );
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 특정 동영상 정보 얻어오기 - getVideo() */
    public GetVideoRes getVideo(int videoIdx){     //UserProvider.java에서 userIdx값을 받아옴.
        //쿼리문 생성
        String getVideoQuery = "select * from VIDEOS where ID = ?";   //쿼리문 실행 ('userIdx' 값으로 조회)
        int getVideoParams = videoIdx;      //파라미터(id) 값 저장

        //쿼리문 실행
        return this.jdbcTemplate.queryForObject(getVideoQuery,
                (rs, rowNum) -> new GetVideoRes(    //rs.getString(" ")값이 DB와 일치해야 한다!
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
                getVideoParams);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 썸네일 변경 - modifyThumbNail()  */
    public int modifyThumbNail(PatchVideoReq patchVideoReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String  modifyThumbNailQuery = "update VIDEOS set THUMB_NAIL = ? where ID = ? AND STATUS =1 ";
        Object[] modifyThumbNailParams = new Object[]{patchVideoReq.getThumbNail(), patchVideoReq.getVideoId()};

        //리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyThumbNailQuery,modifyThumbNailParams);
    }

    /* 동영상 파일 변경 - modifyVideo()  */
    public int modifyVideo(PatchVideoReq patchVideoReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String  modifyVideoQuery = "update VIDEOS set VIDEO = ? where ID = ? AND STATUS =1 ";
        Object[] modifyVideoParams = new Object[]{patchVideoReq.getVideo(), patchVideoReq.getVideoId()};

        //리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyVideoQuery,modifyVideoParams);
    }


    /* 동영상 제목 변경 - modifyTitle()  */
    public int modifyTitle(PatchVideoReq patchVideoReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String  modifyTitleQuery = "update VIDEOS set TITLE = ? where ID = ? AND STATUS =1 ";
        Object[] modifyTitleParams = new Object[]{patchVideoReq.getTitle(), patchVideoReq.getVideoId()};

        //리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyTitleQuery,modifyTitleParams);
    }


    /* 동영상 설명 변경 - modifyDescription()  */
    public int modifyDescription(PatchVideoReq patchVideoReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String  modifyDescriptionQuery = "update VIDEOS set DESCRIPTION = ? where ID = ? AND STATUS =1 ";
        Object[] modifyDescriptionParams = new Object[]{patchVideoReq.getDescription(), patchVideoReq.getVideoId()};

        //리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyDescriptionQuery,modifyDescriptionParams);
    }


    /* 동영상 공개여부 변경 - modifyVideoOpen()  */
    public int modifyVideoOpen(PatchVideoReq patchVideoReq){   //UserService.java에서 객체 값(nickName)을 받아와서...

        //쿼리문 생성 및 객체 생성
        String  modifyVideoOpenQuery = "update VIDEOS set VIDEO_OPEN = ? where ID = ? AND STATUS =1 ";
        Object[] modifyVideoOpenParams = new Object[]{patchVideoReq.getVideoOpen(), patchVideoReq.getVideoId()};

        //리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(modifyVideoOpenQuery,modifyVideoOpenParams);
    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 동영상 정보 삭제(비활성화) - deleteVideo()  */
    public int deleteVideo(PatchVideoReq patchVideoReq){   //UserService.java에서 객체 값(nickName)을 받아와서...
        //쿼리문 생성
        String deleteVideoQuery = "update VIDEOS set STATUS = 0 where ID = ? ";

        //idx를 변수에 저장
        int deleteVideoParams = patchVideoReq.getVideoId();

        //유저 삭제(비활성화) 쿼리문 수행 (0,1로 반환됨)
        return this.jdbcTemplate.update(deleteVideoQuery,deleteVideoParams);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //동영상 페이징 조회 - getVideoPagings()
    public List<GetVideoRes> getVideoPagings(){                  //lIST 형식으로 받아옴


        String getVideoPagingsQuery = "select * from VIDEOS order by id desc limit 5";


        return this.jdbcTemplate.query(getVideoPagingsQuery,
                (rs,rowNum) -> new GetVideoRes(                  //객체가 만들어지면서 다음 값을 받아옴.
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
                        rs.getInt("CHANNEL_ID"))
        );

    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //동영상 페이징 조회 - getVideoPagings()
    public List<GetVideoRes> getVideoPagingsById(int lastPagingId){                  //lIST 형식으로 받아옴

//        String getVideoPagingsByIdQuery = "select * from VIDEOS where id < ? AND ((select count(id) from VIDEOS where id < ?) >= 5) order by id desc limit 5";
        String getVideoPagingsByIdQuery = "select * from VIDEOS where id < ? order by id desc limit 5";

        return this.jdbcTemplate.query(getVideoPagingsByIdQuery,
                (rs,rowNum) -> new GetVideoRes(                  //객체가 만들어지면서 다음 값을 받아옴.
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
                        lastPagingId
        );

    }



































}
