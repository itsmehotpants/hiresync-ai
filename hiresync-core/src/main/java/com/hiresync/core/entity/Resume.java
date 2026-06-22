package com.hiresync.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resumes")
@Getter
@Setter
public class Resume extends BaseEntity {

    @Column(nullable = false, length = 26)
    private String userId;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String filePath;

    private Long fileSize;
    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String rawText;

    private boolean isPrimary = false;
    private int versionNumber = 1;
}
