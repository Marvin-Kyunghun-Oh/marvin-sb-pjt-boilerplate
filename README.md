# Marvin Spring Boot Boilerplate (V1)

최대한 표준에 맞는 아키텍처와, 필수 공통 기능을 적용하여,<br>
프로젝트 구축할때마다 '환경 설정'과 같은 시행착오를 최소화하기 위한 보일러플레이트 프로젝트.


## 🚀 Key Features

### 1. 전 레이어 국제화 (i18n)
- **Accept-Language** 헤더 기반의 동적 언어 처리.
- 메시지 번들 분리 관리 (`messages`, `errors`, `validation`).
- 에러 메시지, 유효성 검사, 공통 코드(Enum), 성공 안내 메시지 전 영역 다국어 지원.

### 2. 분산 추적 및 로깅 (MDC & Log4j2)
- **MDC(Mapped Diagnostic Context)** 기반의 Trace ID 추적.
- 모든 요청에 대해 8자리 고유 Trace ID 부여 및 응답 헤더(`X-Trace-Id`) 연동.
- **Log4j2** 및 **log4jdbc**를 통한 컬러 로그 및 SQL 쿼리 가시화.

### 3. API 응답 및 예외 표준화
- **BaseResponse**: 모든 API의 응답 규격을 통일된 JSON 포맷으로 강제.
- **ResponseBodyAdvice**: 컨트롤러 응답을 가로채 자동으로 공통 규격 래핑 및 성공 메시지 번역 수행.
- **GlobalExceptionHandler**: 비즈니스 예외(`BizException`) 및 시스템 예외를 통합 처리하여 표준 에러 응답 반환.

### 4. 도메인 중심 설계 및 DTO 전략
- **도메인 중심 패키지 구조**: `domain.{domain}.{layer}` 구조로 응집도 향상.
- **Record DTO**: Java Record를 활용한 불변 DTO 구현.
- **이너 클래스 전략**: API 전용 DTO 내부에 요청/응답 record를 응집하여 파일 관리 효율화.
- **MapStruct**: 엔티티와 DTO 간의 타입 안전한 변환 자동화.

### 5. 보안 및 문서화
- **Jasypt**: 설정 파일(`application.yml`)의 민감 정보를 암호화 관리.
- **SpringDoc (Swagger)**: OpenAPI 3.0 기반의 자동화된 API 문서화 및 테스트 환경 제공.

## 🛠 Tech Stack
- **Framework**: Java 17 / Spring Boot 3.5.13
- **Database**: H2 (Runtime) / Spring Data JPA
- **Library**: Lombok, MapStruct, Jasypt, Log4j2, log4jdbc
- **Documentation**: SpringDoc OpenAPI (Swagger)

## 📂 Project Structure
```text
src/main/java/com/marvin/boiler/
├── config/             # 전역 설정 (Locale, Swagger, Jasypt 등)
├── domain/             # 도메인별 레이어 (Account 등)
│   └── {domain}/
│       ├── controller/ # API 컨트롤러
│       ├── service/    # 비즈니스 로직
│       ├── repository/ # 데이터 접근
│       ├── entity/     # JPA 엔티티
│       ├── dto/        # API 요청/응답 Record
│       ├── code/       # 도메인 전용 Enum
│       └── mapper/     # MapStruct 매퍼
└── global/             # 공통 모듈
    ├── code/           # 공통 Enum 및 메시지 유틸
    ├── dto/            # 공통 응답/페이징 객체
    ├── entity/         # 공통 엔티티 (BaseTimeEntity)
    ├── exception/      # 예외 처리 모듈
    └── filter/         # 서블릿 필터 (MDC 등)
```

## ⚙️ Quick Start

### 1. Profile 설정
프로파일 = `local/dev/stg/prod`
```bash
-Dspring.profiles.active=local
```

### 1. Jasypt 암호 키 설정
설정 파일 암호화 해독을 위해 JVM 옵션으로 복호화 키를 주입해야 함.
```bash
-DENCRYPTION_PASSWORD=your_password
```

### 2. API 문서 확인
애플리케이션 실행 후 아래 주소에서 확인 가능.
- **Swagger UI**: `http://localhost:8080/boiler/api/swagger-ui.html`

### 3. 주요 API 예시
- **회원 관리**: `/boiler/api/accounts`
- **공통 코드 조회**: `/boiler/api/common/codes`

## 📝 가이드라인
1. **DTO 작성**: `AccountApiDto`와 같이 도메인별 대표 DTO 클래스 내부에 `record`로 요청/응답 객체를 정의한다.
2. **비즈니스 예외**: 반드시 `BizException`을 사용하며, `ErrorCode`에 정의된 다국어 키를 활용한다.
3. **상태값 관리**: 모든 Enum은 `BaseEnum`을 상속받아 설명값의 다국어 처리가 가능하도록 구현한다.
