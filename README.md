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
- 실시간 채팅 
	- 텍스트 및 파일 전송 지원
- 채팅방 생성 및 관리
	- 다중 사용자 동시 참여 가능
	- 채팅방 입장 사용자 안내
	- 채팅방 입장한 사용자 목록 조회
		- Redis Set 활용
- 채팅 데이터 관리
	- Redis Stream + H2 Database를 활용한 메시지 저장 및 아카이빙
- 사용자 인증
	- Spring Security, JWT 및 Redis 캐시 활용


## Review
### 1. HandlerMethodArgumentResolver + Redis 캐싱을 활용한 인증 성능 개선
#### 문제 상황
- Controller 레이어에서 인증 정보를 가져오는 코드 반복

#### 해결방안 1. @UserInfo 애노테이션과 HandlerMethodArgumentResolver 구현
- 장점
	- Controller 레이어 중복 코드 제거
   		1. HandlerMethodArgumentResolver를 활용하여 전역에서 인증 객체를 자동으로 주입
		2. @UserInfo 애노테이션을 도입 : @UserInfo 있는 경우 인증 객체를 Controller에서 수동으로 가져오지 않도록 개선
- 단점
	1. 매 요청마다 SecurityContextHolder를 조회해야 함
	2. 불필요한 인증 객체 변환 로직 실행
		- CustomUserDetails → MemberDto 변환 과정이 매번 실행됨
		- 불필요한 객체 생성으로 GC(가비지 컬렉션) 부담 증가

#### 해결방안 2. Redis 캐싱을 통한 성능 최적화
- Redis에서 인증객체를 조회 후 캐시에 없으면 DB에서 가져오고, Redis에 저장하여 다음 요청부터는 Redis 캐싱
  	- 장점
  		1. 불필요한 객체 생성을 줄여 메모리 사용 최적화
  	 	2. 다중 서버 환경에서도 일관된 사용자 인증 정보 유지 가능

#### 적용 전 (SecurityContextHolder.getContext())
```java
public static MemberDto getCurrentMember() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
        throw new IllegalStateException("로그인된 사용자가 없습니다.");
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    if ( userDetails != null ) {
        return MemberDto.from(userDetails);
    } else {
        throw new IllegalStateException("Security Context에 Member 객체가 없습니다.");
    }
}
```

#### 리팩토링 1. @UserInfo 애노테이션과 HandlerMethodArgumentResolver 구현
```java
@Component
public class CustomUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return MemberDto.from((CustomUserDetails) authentication.getPrincipal());
        }

            throw new UnauthenticatedUserException();
    }
}
```
```java
public ResponseEntity<DataResponse<MemberResDto>> getMember(@UserInfo MemberDto memberDto) {
}
```

#### 리팩토링 2. Redis 캐싱
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    @CachePut(value = "memberCache", key = "#result.email")
    public Member updateMemberCache(Member member) {
        return member;
    }

    @Cacheable(value = "memberCache")
    public Member getCachedMember(String email) {
        log.info("memberCache missed: {}", email);
        return null;
    }

    @CacheEvict(value = "memberCache")
    public void deleteMemberCache(String email) {
        log.info("memberCache 삭제: {}", email);
    }
}
```

### 2. Redis Stream + H2 Database를 활용한 채팅 데이터 저장 구조 최적화
### 전략
1. Redis Stream 활용 최근 메세지 빠른 읽기/쓰기
   - Stream 내에서 특정 범위의 데이터를 쉽게 조회할 수 있어, 최근 메시지 목록을 빠르게 가져옴.
2. 특정 기간 이상 지난 메세지 Redis Stream → H2 Database로 마이그레이션하여 메모리 사용 최적화

#### RedisMessageRepository.java
```java
@Repository
public class RedisMessageRepository implements ChatMessageRepository {

    private static final long MESSAGE_EXPIRATION_DAYS = 7; // 보관기간(일)
    private static final int MESSAGE_PAGE_DEFAULT_SIZE = 20;
    private final StreamOperations<String, String, String> streamOps; // 직렬화 과정 생략을 위해 String 선택

    @Override
    public List<ChatMessage> getRecentMessages(Long roomId, String firstId, int size) {
        String streamKey = RedisKeyGenerator.getChatMessageStreamKey(roomId);

        Range<String> range = (firstId == null) ?
                Range.unbounded() :
                Range.open("-", firstId);

        List<MapRecord<String, String, String>> records = streamOps.reverseRange(streamKey, range, Limit.limit().count((size == 0) ? MESSAGE_PAGE_DEFAULT_SIZE : size));

        return records.stream()
                .sorted((a,b) -> a.getId().getValue().compareTo(b.getId().getValue())) // desc 정렬 (과거-최신)
                .map(recode -> toChatMessage(roomId, recode))
                .toList();
    }

    public List<ChatMessage> getExpiredMessages(Long roomId) {
        String streamKey = RedisKeyGenerator.getChatMessageStreamKey(roomId);

        // redis 에서 특정 조건 직접 조회
        long fewDaysAgoMillis = Instant.now().minusSeconds(MESSAGE_EXPIRATION_DAYS * 24 * 3600).toEpochMilli();
        String fewDaysAgoId = fewDaysAgoMillis + "-0";

        Range<String> range = Range.leftOpen("0", fewDaysAgoId);
        List<MapRecord<String, String, String>> records = streamOps.range(streamKey, range, Limit.limit().count(5));

        return records.stream()
                .map(recode -> toChatMessage(roomId, recode))
                .collect(Collectors.toList());
    }
}

```

#### ChatMessageService.java
```java
public List<ChatMessageDto> getRecentMessages(Long roomId, String firstId, int size) {
    List<ChatMessage> resultMessages = new ArrayList<>(redisMessageRepository.getRecentMessages(roomId, firstId, size));

    if (resultMessages.size() < size) {
        int remainingSize = size - resultMessages.size();

        // 최신순 (내림차순)
        List<ChatMessage> h2Messages = h2MessageRepository.getRecentMessages(roomId, firstId, remainingSize);
        // 오름차순으로 정렬
        Collections.reverse(h2Messages);

        resultMessages.addAll(0, h2Messages);
    }

    return resultMessages.stream()
            .map(ChatMessageDto::fromEntity)
            .collect(Collectors.toList());
}
```

#### ChatMessageScheduler.java
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageScheduler {

    private final ChatMessageService messageService;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 매일 0시 0분 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void migrateExpiredMessages() {
        log.info("Start message migration process");

        List<Long> chatRoomIds = chatRoomRepository.findAllChatRoomIds();

        for (Long roomId : chatRoomIds) {
            try {
                messageService.migrateExpiredMessages(roomId);
                log.info("Migrated expired messages for chatRoomId={}", roomId);

            } catch (Exception e) {
                log.error("Faild to migrate messages for chatRoomId={}", roomId);
            }
        }

        log.info("Message migration process completed.");
    }
}
```
### 3. 채팅 서비스 파일 업로드 성능 개선
#### 문제상황
- 파일 업로드 시 용량에 따른 속도 저하
- WebSocket(STOMP) 프로토콜을 사용하여 파일을 Base64 인코딩 후 전송하는 방식으로 파일 업로드를 처리
	- 파일 Upload 단계에서 서버에서 파일을 base64인코딩 데이터를 클라이언트에 내려주어 다운로드시 서버통신없이 직접 다운로드
	- 장점
		1. 서버 부하 감소
			- 파일 다운로드 시 HTTP 요청 없이 클라이언트에서 직접 다운로드 가능 
		2. 실시간 전송 가능 (즉시 다운로드 유도)
			- STOMP(WebSocket)는 양방향 실시간 통신을 지원하므로, 업로드 후 즉시 클라이언트에 파일을 전달
	- 단점
		1. Base64 인코딩으로 파일 크기 33% 증가 
	 	2. CPU 연산 부담 증가 (인코딩 & 디코딩 과정)
	  	3. STOMP(WebSocket)의 비효율적인 바이너리 데이터 처리
	  	   - STOMP는 텍스트 기반 메시지 전송에 최적화 : 바이너리 데이터는 부하가 커지고 성능이 저하
  
#### 해결방안
- WebSocket(STOMP) → HTTP 통신 이용
	- 장점 
		1. 네트워크 트래픽 절감 : 원본 크기 그대로 전송
	 	2. CPU 부하 최소화 : 인코딩 처리 없이 MultipartFile을 활용하여 바이너리 파일을 직접 전송하여 속도 향상 효과
	 	3. 대용량 파일 업로드 처리 가능 : Chunked File Upload(조각 업로드) 방식 도입 가능

#### 적용 전 (STOMP 통신)
```java
@MessageMapping("/chat/{roomId}") 
@SendTo("/topic/{roomId}/messages") 
public ChatMessageDto handleMessage(@DestinationVariable("roomId") Long roomId, @Payload ChatMessageDto message) throws Exception {
    if ("file".equals(message.getType())) {
	fileEncodeBase64(message);
        message.setFileUrl("/files/download/" + URLEncoder.encode(message.getFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
    }
}

private void fileEncodeBase64(ChatMessageDto message) throws IOException {
    String fileName = message.getFileName();
    String fileData = message.getFileData();

    byte[] fileBytes = Base64.getDecoder().decode(fileData);

    File directory = new File("uploads");

    if (!directory.exists() && !directory.mkdirs()) {
        throw new IOException("Failed to create directories: " + directory.getAbsolutePath());
    }

    fileName = fileName.replace(" ", "_");

    File file = new File(directory, fileName);
    try (FileOutputStream fos = new FileOutputStream(file)) {
        fos.write(fileBytes);
    }
}
```

#### 적용 후 (HTTP 통신)
```java
@PostMapping("/upload")
public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();

    fileName = processFileName(fileName);

    Path path = Paths.get(uploadDir, fileName);
    Files.write(path, file.getBytes());

    return ResponseEntity.ok(fileName);
}
```

### 4. 불필요한 컨트롤러 호출 최소화
- 동적인 데이터 처리가 필요하지 않은 경우 컨트롤러를 만들지 않고도 특정 URL에 대해 뷰를 직접 반환
- 장점
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

