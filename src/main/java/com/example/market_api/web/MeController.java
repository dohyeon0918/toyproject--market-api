package com.example.market_api.web;

import com.example.market_api.common.dto.ApiResponse;
import com.example.market_api.domain.Member;
import com.example.market_api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MeController {
    private final MemberService memberService;

    @GetMapping("/me")
    public ApiResponse<?> me(@AuthenticationPrincipal Long userId){
        Member m = memberService.getbyId(userId);
        HashMap<String, Object> dto = new HashMap<>();
        dto.put("id", m.getId());
        dto.put("email", m.getEmail());
        dto.put("nickname", m.getNickname());
        dto.put("role", m.getRole().name());
        return ApiResponse.ok(dto);
     }
}
