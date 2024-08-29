package com.walletsystem.auxilliary;

import com.walletsystem.admin.models.Admin;
import com.walletsystem.admin.repositories.AdminRepository;
import com.walletsystem.users.models.User;
import com.walletsystem.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByEmail(username);
        Optional<User> user = userRepository.findByEmail(username);
        if (admin.isPresent()) {
            return admin.get();
        }else if(user.isPresent()){
            return user.get();
        } else {
            throw new UsernameNotFoundException("User not found");

        }
    }
}

