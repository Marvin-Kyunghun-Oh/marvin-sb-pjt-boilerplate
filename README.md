# 🚀 marvin-sb-pjt-boilerplate

> **Spring Boot 프로젝트 생성 시 환경 설정 및 공통 모듈 등,
> 반복되는 리소스를 최소화하기 위한 표준 보일러플레이트입니다.**



## 🛠 Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 4.0.5
* **Build Tool:** Gradle (Wrapper 포함)
* **Database:** H2 (Runtime), MyBatis
* **Logging:** Log4j2 (Log4jdbc 포함)
* **Lombok:** Enabled
* **Validation:** spring-boot-starter-validation

---

## ✨ Key Features

* **Standard Configuration:** 전역 예외 처리(Global Exception Handling) 및 공통 응답 규격 정의
* **Database Layer:** MyBatis 설정 및 SQL 로그 출력을 위한 Log4jdbc 연동
* **Logging Strategy:** 기본 Logback을 Log4j2로 교체하여 고성능 로깅 환경 구축
* **Environment Setup:** `application-local.yml`을 통한 로컬 개발 환경 분리 (보안 가이드 포함)
* **Build Optimization:** Gradle Wrapper를 통한 버전 일관성 유지 및 JAR 패키징 표준화

---

## 📂 Project Structure

```text
src
 ├── main
 │    ├── java/com/franc
 │    │    ├── common          # 공통 유틸, 예외 처리, 상수
 │    │    ├── config          # DB, Security, Web 설정
 │    │    ├── domain          # 비즈니스 로직 (Controller, Service, Mapper)
 │    │    └── MarvinSbApplication.java
 │    └── resources
 │         ├── mapper          # MyBatis XML 파일
 │         ├── application.yml # 공통 설정
 │         └── log4j2.xml      # 로깅 설정
 └── test                      # 단위 테스트 및 통합 테스트