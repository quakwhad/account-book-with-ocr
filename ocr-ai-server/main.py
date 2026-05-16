from fastapi import FastAPI
from routers import ocr_router

app = FastAPI(
    title="가계부 OCR AI 서버",
    description="PaddleOCR을 이용한 영수증 텍스트 추출 API",
    version="1.0.0"
)

# 라우터 등록
app.include_router(ocr_router.router, prefix="/api/v1/ocr", tags=["OCR"])

@app.get("/")
def health_check():
    return {"status": "ok", "message": "OCR Server is running on CPU"}