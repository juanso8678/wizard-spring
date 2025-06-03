package com.wizard.core.modules.pacs.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pacs_series")
public class Series {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String seriesInstanceUID;

    @ManyToOne
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    // Series Information
    private String seriesNumber;
    private String seriesDescription;
    private String modality;
    private LocalTime seriesTime;
    private String bodyPartExamined;
    private String protocolName;

    // Technical Information
    private String manufacturer;
    private String manufacturerModelName;
    private Integer numberOfInstances;

    // Metadata
    private String status; // RECEIVED, PROCESSING, COMPLETED

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Instance> instances;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}