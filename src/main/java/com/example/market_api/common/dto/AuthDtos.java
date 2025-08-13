package com.example.market_api.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record SignupRequest(
            @Email(message="이메일 형식") String email,
            @Size(min = 8, message = "비밀번호는 8자 이상") String password,
            @NotBlank String nickname
    ){}
    public record LoginRequest(
            @Email String email,
            @NotBlank String password
    ){}
    public record TokenResponse(String accessToken){}
    public record MeResponse(Long id, String email, String nickname, String role){}

}
