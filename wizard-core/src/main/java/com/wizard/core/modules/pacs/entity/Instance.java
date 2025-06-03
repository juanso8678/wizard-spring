package com.wizard.core.modules.pacs.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pacs_instances")
public class Instance {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String sopInstanceUID;

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    // Instance Information
    private String instanceNumber;
    private String sopClassUID;
    private String transferSyntaxUID;

    // Image Information
    private Integer rows;
    private Integer columns;
    private Integer bitsAllocated;
    private Integer bitsStored;
    private String photometricInterpretation;

    // File Information
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;

    // Status
    private String status; // RECEIVED, STORED, AVAILABLE

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}