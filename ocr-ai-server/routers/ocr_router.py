import os
from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from services.ocr_service import process_receipt_image, extract_receipt_info
from models.schemas import OCRResponse, CallbackRequest
import httpx
import logging
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

router = APIRouter()
logger = logging.getLogger(__name__)

SPRING_BOOT_CALLBACK_URL = "http://localhost:8080/api/v1/ledgers/receipt/callback"
# 환경 변수에서 시크릿 값 가져오기
CALLBACK_SECRET = os.getenv("CALLBACK_SECRET", "")

@router.post("/analyze", response_model=OCRResponse)
async def analyze_receipt(
    file: UploadFile = File(...),
    userId: int = Form(...)
):
    """
    영수증 이미지를 업로드받아 분석하고 결과를 Spring Boot 서버로 콜백합니다.
    """
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="이미지 파일만 업로드 가능합니다.")

    try:
        # 비동기로 파일 읽기
        contents = await file.read()
        
        # OCR 서비스 호출
        extracted_data = process_receipt_image(contents)
        
        # 상세 정보 추출
        receipt_info = extract_receipt_info(extracted_data)
        
        # 콜백 데이터 구성
        callback_data = CallbackRequest(
            userId=userId,
            amount=receipt_info["amount"],
            category=receipt_info["category"],
            description=receipt_info["description"],
            type=receipt_info["type"],
            date=receipt_info["date"]
        )
        
        # 콜백 전송용 헤더 설정
        headers = {
            "X-Callback-Secret": CALLBACK_SECRET
        }
        
        # Spring Boot 서버로 콜백 (비동기)
        async with httpx.AsyncClient() as client:
            try:
                response = await client.post(
                    SPRING_BOOT_CALLBACK_URL,
                    json=callback_data.model_dump(),
                    headers=headers,
                    timeout=10.0
                )
                response.raise_for_status()
                logger.info(f"Callback successful: {response.status_code}")
            except Exception as e:
                logger.error(f"Callback failed: {str(e)}")
        
        return OCRResponse(success=True, data=extracted_data)
        
    except Exception as e:
        logger.error(f"OCR processing failed: {str(e)}")
        raise HTTPException(status_code=500, detail=f"OCR 처리 중 오류가 발생했습니다: {str(e)}")