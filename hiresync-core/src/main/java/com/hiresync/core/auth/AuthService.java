package com.hiresync.core.auth;

import com.hiresync.core.auth.dto.AuthResponse;
import com.hiresync.core.auth.dto.LoginRequest;
import com.hiresync.core.auth.dto.RegisterRequest;
import com.hiresync.core.entity.User;
import com.hiresync.core.repository.UserRepository;
import com.hiresync.common.exception.HireSyncException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.jwt.expiry-ms}")
    private long jwtExpiryMs;

    @Value("${app.jwt.refresh-expiry-ms}")
    private long refreshExpiryMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw HireSyncException.conflict("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setProvider("LOCAL");
        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> HireSyncException.notFound("User"));

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw HireSyncException.badRequest("Invalid or expired refresh token");
        }

        String userId = jwtService.extractUserId(refreshToken);
        String storedToken = (String) redisTemplate.opsForValue().get("refresh:" + userId);

        if (!refreshToken.equals(storedToken)) {
            throw HireSyncException.badRequest("Refresh token mismatch");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> HireSyncException.notFound("User"));

        return buildAuthResponse(user);
    }

    public void logout(String userId) {
        redisTemplate.delete("refresh:" + userId);
        log.info("User {} logged out, refresh token invalidated", userId);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getName());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        // Store refresh token in Redis — key is "refresh:{userId}" so one delete invalidates all sessions
        redisTemplate.opsForValue().set(
                "refresh:" + user.getId(),
                refreshToken,
                refreshExpiryMs,
                TimeUnit.MILLISECONDS
        );

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtExpiryMs / 1000,
                new AuthResponse.UserInfo(user.getId(), user.getEmail(), user.getName(), user.getPictureUrl())
        );
    }
}
