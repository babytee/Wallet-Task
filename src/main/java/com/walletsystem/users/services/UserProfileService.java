package com.walletsystem.users.services;

import com.walletsystem.common.StandardResponse;
import com.walletsystem.utilities.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("UserProfileService")
public class UserProfileService {
    private final UserInfoService userDetailsService;
    public ResponseEntity<?> getPersonalDetails() {
        var user = userDetailsService.getAuthenticatedUser();
        return ResponseEntity.ok(StandardResponse.success(userDetailsService.mapToUserResponse(user)));
    }

}
