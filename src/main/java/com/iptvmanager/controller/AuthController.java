package com.iptvmanager.controller;

import com.iptvmanager.domain.Usuario;
import com.iptvmanager.dto.LoginRequestDTO;
import com.iptvmanager.dto.LoginResponseDTO;
import com.iptvmanager.dto.RegisterRequestDTO;
import com.iptvmanager.dto.RegisterResponseDTO;
import com.iptvmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        RegisterResponseDTO response = authService.registerNewUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/2fa/enable/{userId}")
    public ResponseEntity<LoginResponseDTO> enableTwoFactor(@PathVariable String userId) {
        LoginResponseDTO response = authService.enableTwoFactor(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/2fa/disable/{userId}")
    public ResponseEntity<Void> disableTwoFactor(@PathVariable String userId) {
        authService.disableTwoFactor(userId);
        return ResponseEntity.noContent().build();
    }
}