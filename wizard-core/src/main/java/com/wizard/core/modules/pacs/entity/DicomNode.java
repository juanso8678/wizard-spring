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
@Table(name = "pacs_dicom_nodes")
public class DicomNode {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String aeTitle;

    @Column(nullable = false)
    private String hostname;

    @Column(nullable = false)
    private Integer port;

    private String description;
    private String nodeType; // SCU, SCP, BOTH

    // Organization (for multitenancy)
    @Column(name = "organization_id")
    private UUID organizationId;

    // Configuration
    private Boolean queryRetrieve;
    private Boolean store;
    private Boolean echo;
    private Boolean find;
    private Boolean move;
    private Boolean get;

    private Boolean active;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
