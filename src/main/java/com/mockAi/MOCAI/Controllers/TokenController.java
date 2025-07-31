package com.mockAi.MOCAI.Controllers;

import com.mockAi.MOCAI.Dtos.Response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/accessToken")
public class TokenController {

    @PostMapping
    public ResponseEntity<?> validateAccessToken(@RequestBody String token) {
        return ResponseEntity.ok().body(new SuccessResponse(
            "Token is Valid", LocalDateTime.now().toString()
        ));
    }

}
