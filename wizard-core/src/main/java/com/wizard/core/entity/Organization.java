package com.wizard.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString 
public class Organization {
   
    @Id
    @GeneratedValue
    private UUID id;
    

    @Column(nullable = false, unique = true)
    private String name;
    private String logo;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String description;
}

