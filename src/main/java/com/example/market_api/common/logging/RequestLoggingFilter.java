package com.example.market_api.common.logging;


import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString().substring(0, 8); // 짧은 추적 ID
        MDC.put("traceId", traceId);
        HttpServletRequest req = (HttpServletRequest) request;

        try {
            String method = req.getMethod();
            String uri = req.getRequestURI();
            String query = req.getQueryString();
            String auth = maskAuth(req.getHeader("Authorization")); // 민감정보 마스킹

            log.info(">> {} {}{} auth={}",
                    method, uri, (query != null ? "?" + query : ""), auth);

            long start = System.currentTimeMillis();
            chain.doFilter(request, response);
            long took = System.currentTimeMillis() - start;

            int status = ((HttpServletResponse) response).getStatus();
            log.info("<< {} {} status={} took={}ms", method, uri, status, took);

        } finally {
            MDC.clear();
        }
    }

    private String maskAuth(String authHeader) {
        if (authHeader == null) return "null";
        // "Bearer abcdef..." → "Bearer ****(len=xxx)"
        return "Bearer ****(len=" + authHeader.length() + ")";
    }
}