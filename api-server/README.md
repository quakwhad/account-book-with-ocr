# 가계부 OCR API 서버 (Account Book with OCR API Server)

본 프로젝트는 영수증 OCR 기술을 활용한 스마트 가계부 서비스의 백엔드 API 서버입니다. 사용자의 영수증을 분석하여 자동으로 가계부 내역을 생성하고, 통계 데이터를 바탕으로 소비 패턴을 분석해주는 기능을 제공합니다.

## 🚀 주요 기능

### 1. 사용자 인증 및 권한 관리
- **Google OAuth2 소셜 로그인**: 구글 계정을 통한 간편 로그인 지원.
    - **로그인 경로**: `/oauth2/authorization/google`
    - **로그인 흐름**:
        1. 사용자가 위 경로로 접속하여 구글 로그인 진행.
        2. 인증 성공 시 서버에서 **Access Token**과 **Refresh Token** 생성.
        3. **Refresh Token**은 보안을 위해 `HttpOnly` 쿠키에 저장.
        4. **Access Token**은 클라이언트(프론트엔드)로 리다이렉트 시 쿼리 파라미터(`?accessToken=...`)로 전달.
- **JWT (JSON Web Token)**: Access Token 및 Refresh Token을 이용한 보안 인증.
- **역할 기반 접근 제어**: 일반 사용자(USER)와 관리자(ADMIN) 권한 분리.

### 2. 가계부 관리 (Ledger Management)
- **지출/수입 내역 CRUD**: 가계부 내역의 생성, 조회, 수정, 삭제 기능.
- **카테고리별 분류**: 내역별 카테고리 지정 및 관리.
- **페이지네이션**: 대용량 내역 조회 시 효율적인 페이지네이션 지원.

### 3. OCR 영수증 처리
- **영수증 업로드**: 사용자가 업로드한 영수증 이미지를 외부 FastAPI 서버로 전달.
- **비동기 처리 및 콜백**: FastAPI에서 분석 완료 후 콜백 API를 통해 자동으로 가계부 내역에 반영.

### 4. 소비 패턴 비교 분석
- **KOSIS 연동**: 통계청(KOSIS) Open API를 통해 1인 가구 월평균 지출 데이터를 수집.
- **국가 평균 비교**: 사용자의 이번 달 총 지출액과 국가 평균 지출액을 비교하여 피드백 제공.

### 5. 관리자 기능
- **전체 사용자 관리**: 시스템 가입자 목록 조회 및 상태 관리.
- **전체 내역 모니터링**: 모든 사용자의 가계부 내역 모니터링 기능 (관리자 전용).

## 🛠 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.12
- **Database**: MySQL
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security, OAuth2 Client, JJWT
- **Build Tool**: Gradle
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Mapping**: MapStruct, Lombok
- **External Communication**: RestClient (Spring 6.1+)

## ⚙️ 설정 및 실행 방법

### 요구 사항
- Java 17 이상
- MySQL 8.0 이상
- (선택) FastAPI OCR 서버

### 환경 변수 설정
`src/main/resources/application.yml` 파일에서 다음 항목들을 설정해야 합니다:
- **Database**: DB URL, Username, Password
- **OAuth2**: Google Client ID 및 Secret
- **JWT**: JWT Secret Key 및 만료 시간
- **External API**: FastAPI 서버 주소 및 KOSIS API Key

### 실행 방법
```bash
./gradlew bootRun
```

## 📖 API 문서 (Swagger)
서버 실행 후 아래 주소를 통해 API 명세서를 확인할 수 있습니다:
- `http://localhost:8080/api/v1/docs`

## 🏗 프로젝트 구조
- `domain`: 비즈니스 로직 단위(auth, user, ledger)로 구분된 패키지 구조.
- `global`: 공통 예외 처리, 보안 설정, 클라이언트 설정 등 전역 설정 관리.
- `client`: 외부 시스템(FastAPI, KOSIS)과의 통신을 담당하는 클라이언트 모듈.
