import requests
from bs4 import BeautifulSoup
import pymysql
import time
from dateutil import parser

# 1. DB 연결 설정
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
    url = f"https://news.google.com/rss/search?q={search_query}&hl=ko&gl=KR&ceid=KR:ko"

    try:
        res = requests.get(url)
        soup = BeautifulSoup(res.text, "xml")
        items = soup.find_all("item")

        conn = get_db_connection()
        cursor = conn.cursor()

        new_count = 0

        for item in items[:20]:
            title = item.title.text
            link = item.link.text
            raw_date = item.pubDate.text
            pub_date = parser.parse(raw_date).strftime('%Y-%m-%d %H:%M:%S')

            # ★ 요약 내용 가져오기 (없으면 빈칸)
            description = item.description.text if item.description else ""
            # HTML 태그 제거 (간단하게)
            description = BeautifulSoup(description, "lxml").text

            # 중복 확인
            cursor.execute("SELECT id FROM news WHERE title = %s", (title,))
            if cursor.fetchone() is None:
                # DB에 저장 (description 포함)
                sql = "INSERT INTO news (title, link, category, provider, pub_date, description) VALUES (%s, %s, %s, %s, %s, %s)"
                cursor.execute(sql, (title, link, category, "Google News", pub_date, description))
                new_count += 1

        conn.commit()
        conn.close()
        print(f"✅ [{category}] 저장 완료: {new_count}건의 새로운 뉴스.")
    except Exception as e:
        print(f"❌ [{category}] 에러 발생: {e}")

# 3. 실행 부분
if __name__ == "__main__":
    categories = {
        "주식": "주식 증시",
        "코인": "비트코인 가상화폐",
        "부동산": "부동산 아파트"
    }

    print("--- 뉴스 수집을 시작합니다 ---")
    for cat, query in categories.items():
        crawl_and_save(cat, query)
        time.sleep(1)
    print("--- 모든 작업이 끝났습니다 ---")