package com.azit.backend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("✅ 구글 로그인 성공! 프론트엔드로 이동합니다.");

        // 로그인 성공 후 리액트(5173) 홈으로 강제 이동
        response.sendRedirect("https://az-news-frontend-dnauu5jrm-backsoo-kims-projects.vercel.app/");
    }
}