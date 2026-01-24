package com.azit.backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

@Component
public class NewsCrawlerScheduler {

    // 1시간(3600000ms)마다 실행 (테스트할 땐 10000(10초)으로 바꿔서 확인해봐)
    // initialDelay = 5000 : 서버 켜지고 5초 뒤에 처음 실행
    @Scheduled(fixedRate = 3600000, initialDelay = 5000)
    public void runPythonCrawler() {
        System.out.println("🐍 [스케줄러] 파이썬 크롤러 실행 시작...");

        try {
            // 1. 파이썬 파일의 경로 찾기
            // 현재 프로젝트 폴더 + /crawler/crawler.py
            String projectPath = System.getProperty("user.dir");
            String pythonScriptPath = Paths.get(projectPath, "crawler", "crawler.py").toString();

            // 2. 프로세스 빌더로 파이썬 실행 명령 만들기
            // 명령어: python3 경로/crawler.py
            String pythonExePath = Paths.get(projectPath, "crawler", "venv", "bin", "python").toString();
            ProcessBuilder processBuilder = new ProcessBuilder(pythonExePath, pythonScriptPath);

            // 3. 실행!
            Process process = processBuilder.start();

            // 4. 파이썬이 출력하는 로그(print문)를 자바 콘솔에서 보기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("🐍 [Python] " + line);
            }

            // 5. 끝날 때까지 기다리기
            int exitCode = process.waitFor();
            System.out.println("✅ [스케줄러] 크롤링 종료. (종료 코드: " + exitCode + ")");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ [스케줄러] 크롤링 중 에러 발생!");
        }
    }
}