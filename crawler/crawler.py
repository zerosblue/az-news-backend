import requests
from bs4 import BeautifulSoup
import pymysql
import time
from dateutil import parser

# 1. DB 연결 설정 (네가 만든 DB 정보랑 일치시켰어)
def get_db_connection():
    return pymysql.connect(
        host='localhost',
        user='root',
        password='1234',
        db='news_azit', 
        charset='utf8mb4',
        cursorclass=pymysql.cursors.DictCursor
    )

# 2. 크롤링 및 저장 함수
def crawl_and_save(category, search_query):
    print(f"🚀 [{category}] 구글 뉴스 수집 시작...")
    # 구글 뉴스 RSS 주소
    url = f"https://news.google.com/rss/search?q={search_query}&hl=ko&gl=KR&ceid=KR:ko"
    
    try:
        res = requests.get(url)
        # XML 형식의 데이터를 분석하기 위해 lxml 파서 사용
        soup = BeautifulSoup(res.text, "xml")
        items = soup.find_all("item")
        
        conn = get_db_connection()
        cursor = conn.cursor()
        
        new_count = 0
        # 최신 뉴스 20개만 가져오기
        for item in items[:20]:
            title = item.title.text
            link = item.link.text
            raw_date = item.pubDate.text 
            # 날짜 형식을 DB에 맞게 변환 (YYYY-MM-DD HH:MM:SS)
            pub_date = parser.parse(raw_date).strftime('%Y-%m-%d %H:%M:%S')
            
            # 이미 저장된 뉴스인지 확인 (제목으로 중복 체크)
            cursor.execute("SELECT id FROM news WHERE title = %s", (title,))
            if cursor.fetchone() is None:
                sql = "INSERT INTO news (title, link, category, provider, pub_date) VALUES (%s, %s, %s, %s, %s)"
                cursor.execute(sql, (title, link, category, "Google News", pub_date))
                new_count += 1
        
        conn.commit()
        conn.close()
        print(f"✅ [{category}] 저장 완료: {new_count}건의 새로운 뉴스.")
    except Exception as e:
        print(f"❌ [{category}] 에러 발생: {e}")

# 3. 실행 부분
if __name__ == "__main__":
    # 검색어 설정
    categories = {
        "주식": "주식 증시", 
        "코인": "비트코인 가상화폐", 
        "부동산": "부동산 아파트"
    }
    
    print("--- 뉴스 수집을 시작합니다 ---")
    for cat, query in categories.items():
        crawl_and_save(cat, query)
        time.sleep(1) # 차단을 막기 위해 1초 쉬기
    print("--- 모든 작업이 끝났습니다 ---")