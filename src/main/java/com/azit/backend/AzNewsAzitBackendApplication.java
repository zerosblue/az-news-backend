package com.azit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 이거 추가

@EnableScheduling // ★ 여기 스위치 켰음!
@SpringBootApplication
public class AzNewsAzitBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzNewsAzitBackendApplication.class, args);
    }
}