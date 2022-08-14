# 📝 프로젝트 소개
>라이징 캠프기간(약 2주) 동안 혼자서 진행한 [유튜브](https://www.youtube.com/) 클론 프로젝트입니다.  
- 제작 기간 : 2022년 12월 11일 ~ 12월 25일  
- 서버 개발자 : 뎁스(본인)

</br>

## 💁‍♂️ Wiki
- 📰 [API 명세서](https://docs.google.com/spreadsheets/d/1JuW5yt8tvZ3sx_hiWTesqtDn_ihmU_4J/edit#gid=514363059 )
- 📦 [ERD 설계도](https://aquerytool.com/aquerymain/index/?rurl=f9df6444-acbe-4991-a7d8-c5f6fd088abd)    
    - 비밀번호 : 738qku    
- 📁 [디렉토리 구조](https://github.com/gusdn7142/Youtube_Clone_Server/wiki/%F0%9F%93%81-Directory-Structure)
- 📽 시연 영상 : API 명세서의 postman 시연화면으로 대체


</br>

## 🛠 사용 기술
#### `Back-end`
  - Java 15
  - Spring Boot 2.4.2 (소프트스퀘어드 Template 사용)
  - Gradle 6.7.1
  - Spring JDBC 
#### `DevOps`  
  - AWS EC2 (Ubuntu 20.04)  
  - AWS RDS (Mysql 8.0)
  - Nginx
  - GitHub
#### `Etc`  
  - JWT
  - postman

</br>

## 📦 ERD 설계도
![Youtube_modeling(Final)](https://user-images.githubusercontent.com/62496215/157594667-bdfef997-3913-4eb5-bda8-f696f0c790a7.png)
</br>

</br>


## 🔎 핵심 기능 및 담당 기능
>당근마켓 서비스의 핵심기능은 채널 구독과 동영상 업로드 및 조회입니다.  
>서비스의 세부적인 기능은 [API 명세서](https://docs.google.com/spreadsheets/d/1JuW5yt8tvZ3sx_hiWTesqtDn_ihmU_4J/edit#gid=514363059)를 참고해 주시면 감사합니다.   
- 구현한 기능  
    - 사용자 : 회원가입 API, 로그인/로그아웃 API, 프로필 조회∘수정 API
    - 채널 : 채널 가입∘구독∘조회∘변경∘삭제 API
    - 프리미엄 : 프리미엄 가입∘변경∘취소 API
    - 동영상 : 동영상 생성∘조회∘변경∘삭제 API   

</br>


## 🌟 트러블 슈팅
- DB 연결 정보와 JWT, PASSWORD 키 값 등이 외부에 노출되지 않도록 Secret.java, application.xml을 .gitignore 파일에 추가
- 회원가입 외에 패스워드 변경시에도 패스워드를 암호화하여 DB에 저장하도록 로직 구현
- 유튜브 도메인을 분석하면서 새로운 사실을 깨달았습니다. 사용자 입장에서는 크게 신경쓰지 않았으나, 직접 DB를 설계하다보니 유튜브 서비스가 사용자 중심이 아닌 채널 중심의 서비스로 운영되고 있다는것을 깨달았고 이에 따라 채널을 구독하고 영상에 좋아요를 누르고 댓글을 작성하는 등의 행위의 주체가 채널이 되도록 DB를 설계하였습니다.  

</br>


## ❕ 회고 / 느낀점
>프로젝트 개발 회고 글   
- Spring boot를 처음 접하고 2주 안에 유튜브 API 서버를 개발해야 하는 상황에서 Spring boot의 동작과정 (Controller -> Service/Provider -> Dao)에서의 기본 코드를 이해하는데 3일 정도의 시간이 소요되었습니다.
- 그리고 local이 아닌 EC2 환경에서 remote 모드로 Intellij를 통해 개발을 진행하였는데, Intellij에서 remote 모드로 개발을 진행시 기본적인 코드 문법 오류를 체크해 주지 않아 빌드 과정에서의 많은 오류가 발생하여 오류를 찾아 해결하는데에도 많은 시간이 소요되었습니다.
- 처음에 Spring boot를 접했을때, 20개 가량의 API를 2주일 안에 개발해야 하는 과제에 대한 부담감과 압박감이 크게 느껴졌으나, 절대 포기하지 않는다는 마인드 하나만으로 하루 14시간 정도를 투자하여 유튜브의 기본적인 API 들을 구현할 수 있었습니다.




 
