package com.example.market_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiFlowTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    static String tokenUser1;
    static Long productId;

    @Test
    @Order(1)
    void 회원가입_로그인() throws Exception{

        //회원가입
        mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"i@i", "password":"12345678","nickname":"user1"}
                        """))
                .andExpect(status().isOk());

        //로그인
        String loginRes = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"i@i","password":"12345678"}
                        """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        tokenUser1 = om.readTree(loginRes).get("data").get("accessToken").asText();
    }

    @Test
    @Order(2)
    void 상품생성_조회() throws Exception{
        //상품 생성
        String createRes = mvc.perform(post("/products")
                .header("Authorization","Bearer "+ tokenUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"title":"맥북 파우치","description":"미개봉","price":15000}
            """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        productId = om.readTree(createRes).get("data").asLong();

        mvc.perform(get("/products/" + productId)
                .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("맥북 파우치"));

    }

    @Test
    @Order(3)
    void 상품수정_삭제() throws Exception {
        // 수정
        mvc.perform(patch("/products/" + productId)
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"price":13000}
                """))
                .andExpect(status().isOk());

        // 삭제
        mvc.perform(delete("/products/" + productId)
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void 소유권검증() throws Exception {
        // 다른 사용자 생성 & 로그인
        mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"h@h","password":"12345678","nickname":"user2"}
                """)).andExpect(status().isOk());

        String loginRes = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"h@h","password":"12345678"}
                """)).andReturn().getResponse().getContentAsString();

        String tokenUser2 = om.readTree(loginRes).get("data").get("accessToken").asText();

        // user1이 만든 상품 하나 새로 생성
        String createRes = mvc.perform(post("/products")
                .header("Authorization", "Bearer " + tokenUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"아이패드 케이스","description":"새제품","price":20000}
                """)).andReturn().getResponse().getContentAsString();

        Long newProductId = om.readTree(createRes).get("data").asLong();

        // user2가 삭제 시도 → 실패 기대
        mvc.perform(delete("/products/" + newProductId)
                        .header("Authorization", "Bearer " + tokenUser2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("본인 상품만 삭제할 수 있습니다."));
    }

}
