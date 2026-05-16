from fastapi import APIRouter, UploadFile, File, HTTPException
from services.ocr_service import process_receipt_image
from models.schemas import OCRResponse

router = APIRouter()

@router.post("/receipt", response_model=OCRResponse)
async def analyze_receipt(file: UploadFile = File(...)):
    """
    영수증 이미지를 업로드받아 텍스트를 추출합니다.
    """
    # 이미지 파일 여부 검증
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="이미지 파일만 업로드 가능합니다.")

    try:
        # 비동기로 파일 읽기
        contents = await file.read()
        
        # OCR 서비스 호출
        extracted_data = process_receipt_image(contents)
        
        return OCRResponse(success=True, data=extracted_data)
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"OCR 처리 중 오류가 발생했습니다: {str(e)}")