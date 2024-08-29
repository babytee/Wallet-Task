package com.walletsystem.users.controllers;

import com.walletsystem.auxilliary.LogoutService;
import com.walletsystem.users.dto.UserRequest;
import com.walletsystem.users.services.UserAuthServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController("AuthenticationController")
@RequestMapping({"/api/v1/user/auth"})
public class AuthenticationController {
    private final UserAuthServices authService;
    private final LogoutService logoutService;

    @PostMapping({"register"})
    public ResponseEntity<?> register(@RequestBody UserRequest request){
        return authService.register(request);
    }

    @PostMapping({"login"})
    public ResponseEntity<?> login(@RequestBody UserRequest request) {
        //return ResponseEntity.ok(request);
        return authService.login(request);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
    }



}
