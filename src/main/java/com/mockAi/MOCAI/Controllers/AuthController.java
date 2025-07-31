package com.mockAi.MOCAI.Controllers;

import com.mockAi.MOCAI.Dtos.Request.LoginRequestDto;
import com.mockAi.MOCAI.Dtos.Request.RegisterRequestDto;
import com.mockAi.MOCAI.Dtos.Response.SuccessResponse;
import com.mockAi.MOCAI.Services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("hello")
    public ResponseEntity<SuccessResponse> hello() {
        return ResponseEntity.ok(new SuccessResponse("Success: Hello world!", LocalDateTime.now().toString()));
    }

    @PostMapping("register")
    public ResponseEntity<?> registerTheUser(@Validated({Default.class}) @RequestBody RegisterRequestDto user) {
        return authService.registerUser(user);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@Validated({Default.class}) @RequestBody LoginRequestDto data, HttpServletResponse res) {
        Cookie cookie = new Cookie("demo_cookie", "helloFromSpring");
        cookie.setMaxAge(60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);

        res.addCookie(cookie);
        return authService.loginUser(data, res);
    }

}
