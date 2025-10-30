package com.theusual.controllers;

import com.theusual.services.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/otp")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.client-origin:*}")
public class AuthController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || phone.isBlank()) {
            return ResponseEntity.badRequest().body("Phone is required");
        }
        otpService.sendOtp(phone);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String code = body.get("code");
        if (phone == null || code == null) {
            return ResponseEntity.badRequest().body("Phone and code are required");
        }
        boolean ok = otpService.verifyOtp(phone, code);
        if (!ok) {
          return ResponseEntity.badRequest().body("Invalid or expired code");
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
