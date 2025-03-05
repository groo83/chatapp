# 채팅 서비스

## Skill 
![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-007396?style=flat-square&logo=websocket&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2%20Database-003545?style=flat-square&logo=h2&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=junit5&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)

## Features 
- 다대다 실시간 채팅 
	- 텍스트 및 파일 형식 발신 및 수신
- 채팅방 생성
- 채팅방 입장한 사용자 목록 조회
- 채팅방 입장 사용자 안내
- 사용자 인증 및 등록

## Review
### 정적인 뷰 매핑 간소화
- 동적인 데이터 처리가 필요하지 않은 경우 컨트롤러를 만들지 않고도 특정 URL에 대해 뷰를 직접 반환
- DispatcherServlet이 뷰를 직접 매핑하면 불필요한 컨트롤러 호출이 생략되어 약 10~20% 성능 향상
  - 대량의 트래픽을 처리해야 하는 경우 요청당 응답 시간이 줄어들어 처리량 증가
- 불필요한 컨트롤러 클래스를 줄이고, 코드의 간결성을 유지

```java
  @Configuration
  @RequiredArgsConstructor
  public class WebMvcConfig implements WebMvcConfigurer {
  
      @Override
      public void addViewControllers(ViewControllerRegistry registry) {
          registry.addViewController("/home").setViewName("home");
          registry.addViewController("/").setViewName("home");
          registry.addViewController("/main").setViewName("main");
          registry.addViewController("/login").setViewName("login");
          registry.addViewController("/register").setViewName("register");
      }
  }
```
### 
