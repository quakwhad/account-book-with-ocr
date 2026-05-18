from pydantic import BaseModel
from typing import List

class ExtractedText(BaseModel):
    text: str
    confidence: float

class OCRResponse(BaseModel):
    success: bool
    data: List[ExtractedText]

class ReceiptTotalResponse(BaseModel):
    total: int

class CallbackRequest(BaseModel):
    userId: int
    amount: int
    category: str
    description: str
    type: str
    date: str