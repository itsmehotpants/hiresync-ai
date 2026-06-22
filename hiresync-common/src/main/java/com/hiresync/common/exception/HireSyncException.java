package com.hiresync.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HireSyncException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public HireSyncException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static HireSyncException notFound(String resource) {
        return new HireSyncException(resource + " not found", HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static HireSyncException forbidden() {
        return new HireSyncException("Access denied", HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public static HireSyncException badRequest(String message) {
        return new HireSyncException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static HireSyncException conflict(String message) {
        return new HireSyncException(message, HttpStatus.CONFLICT, "CONFLICT");
    }
}
