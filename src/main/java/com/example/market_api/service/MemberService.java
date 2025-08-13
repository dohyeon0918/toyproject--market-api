package com.example.market_api.service;

import com.example.market_api.common.exception.BadRequestException;
import com.example.market_api.common.exception.NotFoundException;
import com.example.market_api.domain.Member;
import com.example.market_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(String email, String rawPassword, String nickname){
        if(memberRepository.existsByEmail(email)){
            throw new BadRequestException("이미 가입된 이메일입니다.");
        }
        Member m = new Member();
        m.setEmail(email);
        m.setPassword(passwordEncoder.encode(rawPassword));
        m.setNickname(nickname);
        m.setRole(Member.Role.USER);
        return memberRepository.save(m).getId();
    }

    public Member authenticate(String email, String rawPassword){
        Member m = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("이메일/비밀번호를 확인하세요"));
        if(!passwordEncoder.matches(rawPassword, m.getPassword())){
            throw new BadRequestException("이메일/비밀번호를 확인하세요.");
        }
        return m;
    }

    public Member getbyId(Long id){
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다"));
    }
}
