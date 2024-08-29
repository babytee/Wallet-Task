package com.walletsystem.utilities;

import com.walletsystem.users.dto.UserRequest;
import com.walletsystem.users.models.User;
import com.walletsystem.users.repositories.UserRepository;
import com.walletsystem.wallet.models.BankDetail;
import com.walletsystem.wallet.repositories.BankDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("UserInfoService")
public class UserInfoService {

    private final UserRepository userRepository;
    private final BankDetailRepository bankDetailRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null; // or  throw new RuntimeException("User not authenticated");
    }

    public UserRequest getAuthProfile() {
        User user = getAuthenticatedUser();
        return mapToUserResponse(user);
    }

    public UserRequest mapToUserResponse(User user) {
        UserRequest request = new UserRequest();
        request.setId(user.getId());
        request.setFirstName(user.getFirstName());
        request.setLastName(user.getLastName());
        request.setEmail(user.getEmail());
        request.setPhoneNumber(user.getPhoneNumber());
        return request;
    }

    public User getUserById(Long userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user;
        }
        return null;
    }

    public BankDetail getUserPagaBank(String accountNumber) {
        var bankDetail = bankDetailRepository.findByAccountNumber(accountNumber).orElse(null);
        if (bankDetail != null) {
            return bankDetail;
        }
        return null;
    }
}

