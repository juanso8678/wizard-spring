package com.wizard.core.modules.pacs.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pacs_studies")
public class Study {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String studyInstanceUID;

    // Patient Information
    private String patientId;
    private String patientName;
    private String patientBirthDate;
    private String patientSex;

    // Study Information
    private LocalDate studyDate;
    private LocalTime studyTime;
    private String studyDescription;
    private String accessionNumber;
    private String referringPhysician;
    private String studyId;

    // Technical Information
    private String modality;
    private String institutionName;
    private String stationName;

    // Organization (for multitenancy)
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    // Metadata
    private Integer numberOfSeries;
    private Integer numberOfInstances;
    private String storageLocation;
    private String status; // RECEIVED, PROCESSING, COMPLETED, ERROR

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Series> series;

    // Timestamps
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}