# 가계부 OCR AI 서버 (ocr-ai-server)
FastAPI와 PaddleOCR을 결합하여 영수증 이미지를 분석하고 텍스트를 추출하며, 결과를 Spring Boot 서버로 전송하는 마이크로서비스입니다.

## 🛠 기술 스택
- **Framework:** FastAPI
- **OCR Engine:** PaddleOCR
- **Image Processing:** OpenCV (headless), NumPy
- **HTTP Client:** HTTPX (for callbacks)
- **Language:** Python 3.8+

## 📂 프로젝트 구조
```text
ocr-ai-server/
├── main.py                 # FastAPI 애플리케이션 진입점 (라우터 등록 및 설정)
├── requirements.txt        # 의존성 패키지 목록
├── README.md               # 프로젝트 가이드 문서
├── routers/                # API 엔드포인트 라우팅
│   └── ocr_router.py       # OCR 요청 처리 및 콜백 로직
├── services/               # 비즈니스 로직
│   └── ocr_service.py      # PaddleOCR 연동 및 데이터 가공
└── models/                 # 데이터 검증 및 전송 모델
    └── schemas.py          # Pydantic 스키마 정의
```

## 🚀 설치 및 실행 방법

### 1. 가상환경 구성 및 활성화
프로젝트 루트 디렉토리에서 독립된 파이썬 가상환경을 생성하고 활성화합니다.

#### windows
```bash
python -m venv venv
.\venv\Scripts\activate
```

#### macOS / Linux:
```bash
python3 -m venv venv
source venv/bin/activate
```
### 2. 의존성 패키지 설치
필요한 패키지(FastAPI, PaddleOCR, HTTPX 등)를 설치합니다.
```bash
pip install --upgrade pip
pip install -r requirements.txt
```

### 3. 서버 실행
Uvicorn을 사용하여 FastAPI 서버를 로컬에서 구동합니다.
```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```
- `--host 0.0.0.0`: 외부 호스트(Spring Boot 서버 등)에서 접근 가능하도록 설정합니다.
- `--port 8000`: 8000번 포트로 서버를 실행합니다.
- `--reload`: 코드 수정 시 서버를 자동으로 재시작합니다. (개발 환경용)

## 📝 API 문서 및 테스트 (Swagger UI)
서버를 실행한 후 브라우저에서 아래 주소로 접속하면 Swagger UI를 통해 API 엔드포인트를 확인하고 테스트할 수 있습니다.

- **Swagger UI:** http://localhost:8000/docs
- **Alternative UI (ReDoc):** http://localhost:8000/redoc

## 🔌 주요 API 엔드포인트

### 1. 영수증 OCR 분석 및 콜백
영수증 이미지를 분석하여 텍스트를 추출하고, 추출된 정보(금액, 날짜 등)를 메인 API 서버(Spring Boot)로 전송합니다.

- **URL:** `/api/v2/analyze`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data`
- **Request Body:**
  - `file`: 영수증 이미지 파일 (JPEG, PNG 등)
  - `userId`: 해당 영수증을 소유한 사용자의 ID (Integer)
- **Workflow:**
  1. 이미지 수신 및 디코딩
  2. PaddleOCR을 이용한 텍스트 및 신뢰도 추출
  3. 추출된 텍스트에서 금액(Amount), 날짜(Date) 등 정보 가공
  4. Spring Boot 서버(`http://localhost:8080/api/v1/ledgers/receipt/callback`)로 결과 전송
- **Response Example:**
  ```json
  {
    "success": true,
    "data": [
      {
        "text": "스타벅스 강남점",
        "confidence": 0.9876
      },
      {
        "text": "4,500",
        "confidence": 0.9543
      }
    ]
  }
  ```
  
### 2. 헬스 체크
- **URL:** `/`
- **Method:** `GET`
- **Response:** `{"status": "ok", "message": "OCR Server is running on CPU"}`

## 💡 주의 사항
- **최초 실행 시 지연:** `PaddleOCR`은 최초 실행 시 한국어 및 영어 대응 모델 파일을 자동으로 다운로드합니다. 이로 인해 첫 번째 요청의 응답 시간이 다소 소요될 수 있습니다.
- **Spring Boot 서버 연동:** 분석 결과는 Spring Boot 서버의 특정 엔드포인트로 자동 콜백됩니다. 원활한 연동을 위해 Spring Boot 서버가 `localhost:8080`에서 구동 중이어야 합니다.
- **하드웨어 환경:** 본 서버는 CPU 환경에서 작동하도록 최적화되어 있습니다. GPU를 사용하려면 `ocr_service.py`의 `PaddleOCR` 설정 및 `paddlepaddle-gpu` 설치가 필요합니다.