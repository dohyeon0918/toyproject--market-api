package com.example.market_api.web;

import com.example.market_api.common.dto.ApiResponse;
import com.example.market_api.common.dto.AuthDtos;
import com.example.market_api.domain.Member;
import com.example.market_api.security.JwtTokenProvider;
import com.example.market_api.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuhController {
    private final MemberService memberService;
    private final JwtTokenProvider jwt;

    @PostMapping("/signup")
    public ApiResponse<Long> signup(@Valid @RequestBody AuthDtos.SignupRequest req){
        Long id = memberService.signup(req.email(), req.password(), req.nickname());
        return ApiResponse.ok(id);
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.TokenResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req){
        Member m = memberService.authenticate(req.email(), req.password());
        String token = jwt.createToken(m.getId(), m.getRole().name());
        return ApiResponse.ok(new AuthDtos.TokenResponse(token));
    }

}
