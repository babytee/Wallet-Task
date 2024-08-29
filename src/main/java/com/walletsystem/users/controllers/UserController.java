package com.walletsystem.users.controllers;

import com.walletsystem.users.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("UserController")
@RequestMapping({"/api/v1/user/profile"})
public class UserController {

    private final UserProfileService userProfileService;
    @GetMapping("get_profile")
    public ResponseEntity<?> getAuthProfile() {
        return userProfileService.getPersonalDetails();
    }


}
