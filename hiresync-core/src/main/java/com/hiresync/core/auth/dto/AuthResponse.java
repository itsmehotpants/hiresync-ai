package com.hiresync.core.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserInfo user
) {
    public record UserInfo(String id, String email, String name, String pictureUrl) {}

    public static AuthResponse of(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
