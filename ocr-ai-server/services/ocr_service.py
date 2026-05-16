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

def extract_total_amount(extracted_texts: list) -> int:
    """
    OCR 추출 결과에서 규칙에 따라 총액을 파싱합니다.
    """
    # 1. confidence >= 0.9만 남기기
    filtered_texts = [item for item in extracted_texts if item.get("confidence", 0.0) >= 0.9]
    
    # 쉼표가 포함된 텍스트 필터링
    comma_texts = [item["text"] for item in filtered_texts if "," in item["text"]]
    
    # 2. 쉼표가 존재하는 text의 개수 파악하기
    if len(comma_texts) >= 1:
        # 2-1. 1개 이상 존재할 때
        possible_numbers = []
        for text in comma_texts:
            # 쉼표 제거
            text_no_comma = text.replace(",", "")
            # 정수형으로 변경 가능한 것들만 변경
            if text_no_comma.isdigit():
                possible_numbers.append(int(text_no_comma))
        
        # 그 중 가장 큰 수로 결정
        return max(possible_numbers) if possible_numbers else 0

    else:
        # 2-2. 존재하지 않을 때
        # text 중 정수형으로만 이루어진 String만 리스트로 만들기
        digit_texts = [item["text"] for item in filtered_texts if item["text"].isdigit()]
        
        possible_numbers = []
        for text in digit_texts:
            # 리스트 중 길이가 2 이상 4 미만인 것들
            if 2 <= len(text) < 4:
                possible_numbers.append(int(text))
                
        # 그 중 가장 큰 값으로 결정
        return max(possible_numbers) if possible_numbers else 0