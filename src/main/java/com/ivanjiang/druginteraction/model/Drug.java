package com.ivanjiang.druginteraction.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drugs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "generic_name", nullable = false, unique = true)
    private String genericName;

    @Column(name = "brand_names")
    private String brandNames;

    @Column(name = "drug_class")
    private String drugClass;

    @Column(columnDefinition = "TEXT")
    private String description;
}
