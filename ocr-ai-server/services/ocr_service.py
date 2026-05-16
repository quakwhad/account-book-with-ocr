from paddleocr import PaddleOCR
import numpy as np
import cv2

ocr_model = PaddleOCR(use_angle_cls=True, lang='korean', enable_mkldnn=False)

def process_receipt_image(image_bytes: bytes) -> list:
    """
    업로드된 이미지 바이트를 읽어 OCR 결과를 반환합니다.
    """
    # 바이트 데이터를 numpy 배열로 변환
    nparr = np.frombuffer(image_bytes, np.uint8)
    
    # OpenCV를 사용하여 이미지 디코딩
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    if img is None:
        raise ValueError("이미지를 디코딩할 수 없습니다. 손상된 파일일 수 있습니다.")

    # PaddleOCR 추론 실행
    result = ocr_model.ocr(img)

    extracted_texts = []
    
    # 결과가 비어있으면 빈 리스트 반환
    if not result or not isinstance(result, list):
        return extracted_texts

    # 딕셔너리 구조에 맞춘 파싱 로직
    res_dict = result[0]
    
    # 결과가 딕셔너리 형태이고, 텍스트와 신뢰도 키가 모두 있는지 확인
    if isinstance(res_dict, dict) and 'rec_texts' in res_dict and 'rec_scores' in res_dict:
        texts = res_dict['rec_texts']
        scores = res_dict['rec_scores']
        
        # 텍스트와 신뢰도를 짝지어서 리스트에 담기
        for text, score in zip(texts, scores):
            extracted_texts.append({
                "text": str(text),
                "confidence": float(score)
            })

    return extracted_texts