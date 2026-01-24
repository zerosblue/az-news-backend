package com.azit.backend.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String password;
    private String nickname;
    private String interests; // "주식,부동산"
}