package com.walletsystem.auxilliary;

import com.walletsystem.common.StandardResponse;
import com.walletsystem.users.dto.UserRequest;
import com.walletsystem.users.repositories.UserRepository;
import com.walletsystem.utilities.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRequestValidation {
    private final UserRepository userRepository;
    private final Utility utility;

    public ResponseEntity<?> validateUser(UserRequest request) {
        if (request == null) {
            return ResponseEntity.ofNullable(StandardResponse.error("request body cannot be empty"));
        } else {

            if (utility.isNullOrEmpty(request.getUserName())) {
                return ResponseEntity.badRequest().body(StandardResponse.error("userName cannot empty"));
            } else if (utility.isNullOrEmpty(request.getEmail())) {
                return ResponseEntity.badRequest().body(StandardResponse.error("email cannot be empty"));
            } else if (!utility.isValidEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(StandardResponse.error("email is invalid"));
            } else if (utility.isNullOrEmpty(request.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(StandardResponse.error("phoneNumber cannot be empty"));
            } else if (utility.isNigerianPhoneNumber(request.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(StandardResponse.error("phoneNumber is invalid"));
            } else if (utility.isNullOrEmpty(request.getFirstName())) {
                return ResponseEntity.badRequest().body(StandardResponse.error("firstName cannot be empty"));
            } else if (utility.isNullOrEmpty(request.getLastName())) {
                return ResponseEntity.badRequest().body(StandardResponse.error("lastName cannot be empty"));
            }

            //Check the password if match
            boolean check_password = request.getPassword().equals(request.getConfirmPassword());

            if (!check_password) {
                return ResponseEntity.badRequest().body(StandardResponse.error("Password not match"));
            }

            var checkPhoneNo = userRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
            if (checkPhoneNo != null) {
                return ResponseEntity.badRequest().body(StandardResponse.error("user with this phoneNumber already existed"));
            }

            var checkEmail = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (checkEmail != null) {
                return ResponseEntity.badRequest().body(StandardResponse.error("User with this email already existed"));
            }

            var checkUsername = userRepository.findByUserName(request.getUserName()).orElse(null);
            if (checkUsername != null) {
                return ResponseEntity.badRequest().body(StandardResponse.error("User with this username already existed"));
            }
        }

        return ResponseEntity.ok("success");
    }
}
