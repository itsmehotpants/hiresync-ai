package com.hiresync.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "contacts")
@Getter
@Setter
public class Contact extends BaseEntity {

    @Column(nullable = false, length = 26)
    private String userId;

    @Column(nullable = false)
    private String name;

    private String email;
    private String phone;
    private String company;
    private String role;

    @Column(columnDefinition = "TEXT")
    private String linkedinUrl;

    private String contactType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate lastContactedAt;
}
