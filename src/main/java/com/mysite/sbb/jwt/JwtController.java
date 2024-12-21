package com.mysite.sbb.jwt;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class JwtController {

    // 로그인 후 jwt 토큰 발급 처리
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;

    // 로그인 성공 시, jwt Token 발급
    @PostMapping("/user/login")
    public void generateJwtToken(@RequestParam("username") String username,
                                                   @RequestParam("password") String password,
                                                   HttpServletResponse response) throws IOException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            SiteUser user = userService.getUser(username);
            String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().getValue());
            String refreshToken = jwtTokenProvider.generateRefreshToken();

            redisTemplate.opsForValue()
                    .set("RT:"+user.getEmail(), refreshToken,
                            JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

            // SSR 에서는 쿠키 활용
            // 쿠키에 담기 (HttpOnly 설정)
            Cookie accessCookie = JwtUtil.convertAccessTokenToCookie(accessToken);
            Cookie refreshCookie = JwtUtil.convertRefreshTokenToCookie(refreshToken);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
            response.sendRedirect("/");

        } catch (AuthenticationException e) {
            // 클라이언트에게 401 상태 코드와 오류 메시지 반환
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 실패");
        }
    }

    // Refresh 토큰 검증하여 Access 토큰, Refresh 토큰 재발급
    @PostMapping("/api/auth/refresh")
    public ResponseEntity<String> refresh(
                        HttpServletRequest request,
                        HttpServletResponse response) {

        String accessToken = JwtUtil.extractTokenFromCookie(request, "ACCESS_TOKEN");
        String refreshToken = JwtUtil.extractTokenFromCookie(request, "REFRESH_TOKEN");

        // 시그니처 검증
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // String email = jwtTokenProvider.getEmailFromToken(accessToken.substring(7));
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);

        // 해당 유저의 RefreshToken 인지 검증
        if(!refreshToken.equals(savedRefreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        SiteUser user= userService.getUserByEmail(email);

        // 새 jwt 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(email, user.getRole().getValue());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        // 새로운 RefreshToken Redis 에 저장
        redisTemplate.opsForValue()
                .set("RT:"+email, refreshToken,
                        JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        // 응답으로 새 토큰 전송
        // 쿠키에 담기 (HttpOnly 설정)
        Cookie accessCookie = JwtUtil.convertAccessTokenToCookie(accessToken);
        Cookie refreshCookie = JwtUtil.convertRefreshTokenToCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("토큰 재발급 성공");
    }

    @GetMapping("/user/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
        accessCookie.setMaxAge(0);
        accessCookie.setPath("/");

        Cookie refreshCookie = new Cookie("ACCESS_TOKEN", null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/")
                .body("로그아웃 성공");
    }
}
