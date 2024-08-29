package com.walletsystem.users.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagatech.collect.request.RegisterPersistentPaymentAccountRequest;
import com.pagatech.collect.response.RegisterPersistentPaymentAccountResponse;
import com.walletsystem.auxilliary.Role;
import com.walletsystem.auxilliary.UserRequestValidation;
import com.walletsystem.common.MessagingException;
import com.walletsystem.common.StandardResponse;
import com.walletsystem.config.JwtService;
import com.walletsystem.users.dto.AuthenticationResponse;
import com.walletsystem.users.dto.UserRequest;
import com.walletsystem.users.models.Token;
import com.walletsystem.users.models.TokenType;
import com.walletsystem.users.models.User;
import com.walletsystem.users.repositories.TokenRepository;
import com.walletsystem.users.repositories.UserRepository;
import com.walletsystem.utilities.UserInfoService;
import com.walletsystem.utilities.Utility;
import com.walletsystem.wallet.dto.BankDetailRequest;
import com.walletsystem.wallet.models.BankDetail;
import com.walletsystem.wallet.models.Wallet;
import com.walletsystem.wallet.repositories.BankDetailRepository;
import com.walletsystem.wallet.repositories.WalletRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.pagatech.collect.core.Collect;
import com.pagatech.collect.request.GetPersistentPaymentAccountRequest;
import com.pagatech.collect.request.GetPersistentPaymentAccountRequest;

@RequiredArgsConstructor
@Service("UserAuthServices")
public class UserAuthServices {
    private static final Logger log = LoggerFactory.getLogger(UserAuthServices.class);
    private final UserRequestValidation userRequestValidation;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserInfoService userInfoService;
    private final WalletRepository walletRepository;
    private final Utility utility;
    private final BankDetailRepository bankDetailRepository;

    public ResponseEntity<?> register(UserRequest request) {
        //User Registration
        ResponseEntity<?> userRequestResEntity =
                userRequestValidation.validateUser(request);

        if (userRequestResEntity.getStatusCode() != HttpStatus.OK) {
            return userRequestResEntity;
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);


        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0.0);
        walletRepository.save(wallet);

        createVirtualAccount(request, user);

        return ResponseEntity.ok(StandardResponse.success("Registered Successfully"));
    }

    public ResponseEntity<?> login(UserRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(StandardResponse.error("Email is required"));
        }

        Optional<User> userOpt = userRepository.
                findByEmail(request.getEmail());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(StandardResponse.error("User Not Found"));
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(StandardResponse.error("Password not match"));
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            AuthenticationResponse authResponse = new AuthenticationResponse();
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            authResponse.setAccessToken(jwtToken);
            authResponse.setRefreshToken(refreshToken);

            authResponse.setUserDetails(userInfoService.mapToUserResponse(user));


            return ResponseEntity.ok(StandardResponse.success("LoggedIn Successfully", authResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(StandardResponse.error("Authentication failed: " + e.getMessage()));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void createVirtualAccount(UserRequest registration, User user) {
        try {
            // Initialize the Collect class with the custom OkHttpClient
            Collect collect = new Collect.Builder()
                    .setApiKey("4d3bc4968207451992bb28ab0a90830ad33b5e8843704b6aa8ec820a7c6675a683b79fbd3b224eaaa08af8a03bff320ce2dae362e9b64ef7947a65103ffa4300")
                    .setPrincipal("E3F0003C-C2D0-46E2-9F7F-60B3E7CFF1B4")
                    .setCredential("pY4+BDmu7qUaWxh")
                    .setTest(true)
                    .build();

            // Create a virtual account
            RegisterPersistentPaymentAccountRequest request = RegisterPersistentPaymentAccountRequest.builder()
                    .referenceNumber("test12345100zz3")
                    .phoneNumber(registration.getPhoneNumber())
                    .accountName(registration.getFirstName() + " " + registration.getLastName())
                    .firstName(registration.getFirstName())
                    .lastName(registration.getLastName())
                    .accountReference("0121111111116")
                    .financialIdentificationNumber("22222222222")
                    .creditBankId("3E94C4BC-6F9A-442F-8F1A-8214478D5D86")
                    .creditBankAccountNumber("0000000000")
                    .callbackUrl("")
                    .build();
            //2513073759


            RegisterPersistentPaymentAccountResponse response = collect.registerPersistentPaymentAccount(request);

            //response.getAccountNumber();
            createBankDetails(response.getAccountNumber(), user);

            log.info("Paga Virtual Account Created: {}", response);
            System.out.println(response.toString());
            log.info("Paga Virtual Account Created: {}", response.toString());
        } catch (Exception e) {
            log.error("Failed to create Paga virtual account", e);
            throw new RuntimeException("Failed to create Paga virtual account", e);
        }
    }

    private void createBankDetails(String accountNumber, User user) {
        BankDetail bankDetail = BankDetail.builder()
                .bankName("Paga")
                .accountNumber(accountNumber)
                .user(user)
                .build();
        bankDetailRepository.save(bankDetail);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false).expired(false).build();
        this.tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = this.tokenRepository.findAllValidTokensByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach((token) -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            this.tokenRepository.saveAll(validUserTokens);
        }
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring(7);
            String userEmail = this.jwtService.extractUsername(refreshToken);
            if (userEmail != null) {
                User user = this.userRepository.findByEmail(userEmail).orElseThrow();
                if (this.jwtService.isTokenValid(refreshToken, user)) {
                    this.revokeAllUserTokens(user);
                    String accessToken = this.jwtService.generateToken(user);
                    this.saveUserToken(user, accessToken);
                    AuthenticationResponse authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
                    (new ObjectMapper()).writeValue(response.getOutputStream(), authResponse);
                }
            }
        }
    }

    public void checkTokenExpiration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring(7);
            String userEmail = this.jwtService.extractUsername(refreshToken);
            if (userEmail != null) {
                User user = this.userRepository.findByEmail(userEmail).orElseThrow();
                if (this.jwtService.isTokenValid(refreshToken, user)) {
                    this.revokeAllUserTokens(user);
                    String accessToken = this.jwtService.generateToken(user);
                    this.saveUserToken(user, accessToken);
                    AuthenticationResponse authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
                    (new ObjectMapper()).writeValue(response.getOutputStream(), authResponse);
                }
            }
        }
    }


}
