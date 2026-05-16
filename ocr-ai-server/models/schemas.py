from pydantic import BaseModel
from typing import List

class ExtractedText(BaseModel):
    text: str
    confidence: float

class OCRResponse(BaseModel):
    success: bool
    data: List[ExtractedText]