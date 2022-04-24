# Youtube_Clone_Server
>유튜브 API 서버 개발 (개인 프로젝트)  
- API 명세서: https://docs.google.com/spreadsheets/d/1JuW5yt8tvZ3sx_hiWTesqtDn_ihmU_4J/edit#gid=514363059 
- ERD 설계도: https://aquerytool.com/aquerymain/index/?rurl=f9df6444-acbe-4991-a7d8-c5f6fd088abd
    - 비밀번호 : 738qku   
</br>

## 1. 제작 기간 & 참여 인원  
- 2021년 12월 11일 ~ 12월 25일  
    - 서버 개발자 : 뎁스(본인)
   
</br>

## 2. 사용 기술
#### `Language`
  - Java 15
  - Spring Boot (소프트스퀘어드 Template 사용)
  - Gradle
  - Spring JDBC 
#### `Environment`  
  - AWS EC2 (Ubuntu 20.04)  
  - Nginx
#### `Database`  
  - RDS (Mysql 8.0)

</br>

## 3. ERD 설계도
![Youtube_modeling(Final)](https://user-images.githubusercontent.com/62496215/157594667-bdfef997-3913-4eb5-bda8-f696f0c790a7.png)


</br>

## 4. 핵심 기능
>실제 유튜브 서비스의 기능과 유사하게 기능을 구현하였습니다.  
>이 서비스의 핵심 기능에는 사용자와 채널,영상 정보 등록/조회/수정 기능 등이 있습니다.  
>서비스의 세부적인 기능은 [API 명세서](https://docs.google.com/spreadsheets/d/1JuW5yt8tvZ3sx_hiWTesqtDn_ihmU_4J/edit#gid=514363059)를 참고해 주시면 감사합니다.  

- 구현한 API
    - 사용자 : 회원가입 API, 로그인/로그아웃 API, 프로필 조회∘수정 API
    - 채널 : 채널 가입∘구독∘조회∘변경∘삭제 API
    - 프리미엄 : 프리미엄 가입∘변경∘취소 API
    - 동영상 : 동영상 생성∘조회∘변경∘삭제 API   

</br>

## 5. 핵심 트러블 슈팅
- DB 연결 정보, JWT와 PASSWORD 키 값이 노출되지 않도록 .gitignore 파일에 Secret.java, application.xml 추가
- 회원가입 이외에 패스워드 변경시에도 암호화하여 DB에 저장하도록 코드 구현

</br>

## 6. 회고 / 느낀점
>프로젝트 개발 회고 글   
- Spring boot를 처음 접하고 2주 안에 유튜브 API 서버를 개발해야 하는 상황에서 Spring boot의 동작과정 (Controller -> Service/Provider -> Dao)에서의 기본 코드를 이해하는데 3일 정도의 시간이 소요되었습니다.
- 그리고 local이 아닌 EC2 환경에서 remote 모드로 Intellij를 통해 개발을 진행하였는데, Intellij에서 remote 모드로 개발을 진행시 기본적인 코드 문법 오류를 체크해 주지 않아 빌드 과정에서의 많은 오류가 발생하여 오류를 찾아 해결하는데에도 많은 시간이 소요되었습니다.
- 처음에 Spring boot를 접했을때, 20개 가량의 API를 2주일 안에 개발해야 하는 과제에 대한 부담감과 압박감이 크게 느껴졌으나, 절대 포기하지 않는다는 마인드 하나만으로 하루 14시간 정도를 투자하여 유튜브의 기본적인 API 들을 구현할 수 있었습니다.

 
