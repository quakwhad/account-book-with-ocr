# 가계부 with OCR (Account Book with OCR)

영수증 OCR 기술을 활용한 스마트 가계부 서비스입니다.  
영수증 이미지를 업로드하면 자동으로 가계부 내역이 등록되며, 통계청(KOSIS) 데이터를 바탕으로 국가 평균 소비 패턴과 비교 분석을 제공합니다.

## 🏗 아키텍처

```
사용자
  │
  │  영수증 업로드 / 가계부 관리
  ▼
┌─────────────────────┐      OCR 분석 요청        ┌─────────────────────┐
│   api-server        │ ───────────────────────► │   ocr-ai-server     │
│   (Spring Boot)     │ ◄─────────────────────── │   (FastAPI)         │
│   :8080             │      콜백(분석 결과)       │   :8000             │
└─────────────────────┘                          └─────────────────────┘
         │
         │  월평균 지출 데이터 조회
         ▼
   KOSIS Open API
   (통계청)
```

## 📦 서비스 구성

| 서비스 | 기술 스택 | 설명 |
|---|---|---|
| [api-server](./api-server/README.md) | Java 17, Spring Boot | 사용자 인증, 가계부 CRUD, KOSIS 연동 |
| [ocr-ai-server](./ocr-ai-server/README.md) | Python 3.8+, FastAPI, PaddleOCR | 영수증 이미지 분석 및 텍스트 추출 |

## 🚀 주요 기능

- **Google OAuth2 로그인**: 구글 계정을 통한 간편 소셜 로그인
- **영수증 OCR 자동 등록**: 영수증 사진 한 장으로 가계부 내역 자동 생성
- **가계부 CRUD**: 지출/수입 내역 관리 및 카테고리 분류
- **소비 패턴 비교**: 이번 달 지출을 1인 가구 국가 평균과 비교 분석
- **관리자 기능**: 전체 사용자 및 내역 모니터링

## ⚙️ 실행 방법

두 서버를 모두 실행해야 OCR 기능을 온전히 사용할 수 있습니다.

### 1. OCR AI 서버 실행 (포트 8000)

```bash
cd ocr-ai-server
python -m venv venv
source venv/bin/activate  # Windows: .\venv\Scripts\activate
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

### 2. API 서버 실행 (포트 8080)

```bash
cd api-server
./gradlew bootRun
```

> 환경 변수(DB, OAuth2, JWT, KOSIS API Key 등) 설정은 각 서비스의 README를 참고하세요.

## 📖 API 문서

서버 실행 후 Swagger UI에서 API 명세를 확인할 수 있습니다.

- **api-server**: `http://localhost:8080/api/v1/docs`
- **ocr-ai-server**: `http://localhost:8000/docs`
