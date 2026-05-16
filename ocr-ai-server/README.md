# 가계부 OCR AI 서버 (ocr-ai-server)
FastAPI와 PaddleOCR을 결합하여 영수증 이미지를 분석하고 텍스트를 추출하는 마이크로서비스입니다. Spring Boot 메인 API 서버와 연동하여 작동하도록 설계되었습니다.

## 🛠 기술 스택
- **Framework:** FastAPI
- **OCR Engine:** PaddleOCR
- **Image Processing:** OpenCV (headless)
- **Language:** Python 3.8+

## 📂 프로젝트 구조
```text
ocr-ai-server/
├── main.py                 # FastAPI 애플리케이션 진입점
├── requirements.txt        # 의존성 패키지 목록
├── README.md               # 프로젝트 가이드 문서
├── routers/                # API 엔드포인트 라우터
│   └── ocr_router.py
├── services/               # OCR 비즈니스 로직 및 모델 제어
│   └── ocr_service.py
└── models/                 # Pydantic 데이터 검증 스키마
    └── schemas.py
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
필요한 패키지(FastAPI, PaddleOCR CPU 버전 등)를 설치합니다.
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
FastAPI는 코드를 기반으로 대화형 API 문서를 자동으로 생성합니다. 서버를 실행한 후 브라우저에서 아래 주소로 접속하면 Swagger UI를 통해 API 엔드포인트를 확인하고 직접 영수증 이미지를 업로드하여 테스트해 볼 수 있습니다.

- **Swagger UI:** http://localhost:8000/docs
- **Alternative UI (ReDoc):** http://localhost:8000/redoc

## 🔌 주요 API 엔드포인트

### 1. 영수증 OCR 분석
- **URL:** `/api/v1/ocr/receipt`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data`
- **Request Body:**
  - `file`: 영수증 이미지 파일 (JPEG, PNG 등)
- **Response:** 분석 성공 여부와 함께 텍스트 및 신뢰도 결과 배열을 반환합니다.
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
        "text": "아메리카노 4,500",
        "confidence": 0.9543
      }
    ]
  }
  ```
  
### 2. 헬스 체크
- **URL:** `/`
- **Method:** `GET`
- **Response:** 서버 정상 작동 상태 메시지를 반환합니다.

## 💡 주의 사항
- **최초 실행 시 지연:** `PaddleOCR` 라이브러리는 최초로 OCR 추론을 실행할 때 언어 모델 파일(한국어 및 영어 대응 모델)을 자동으로 가동 자원에 다운로드합니다. 이로 인해 첫 번째 요청의 응답 시간이 다소 소요될 수 있으며, 두 번째 요청부터는 메모리에 로드된 모델을 사용하여 빠르게 처리됩니다.
- **하드웨어 환경:** CPU 전용 환경에서 무리 없이 작동할 수 있도록 코드 내부적으로 GPU 미사용 옵션이 강제 설정되어 있습니다.