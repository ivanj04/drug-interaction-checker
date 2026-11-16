package com.ivanjiang.druginteraction.model;

import com.ivanjiang.druginteraction.model.enums.EvidenceLevel;
import com.ivanjiang.druginteraction.model.enums.InteractionType;
import com.ivanjiang.druginteraction.model.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drug_interactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drug1_id", nullable = false)
    private Drug drug1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drug2_id", nullable = false)
    private Drug drug2;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false)
    private SeverityLevel severityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    @Column(columnDefinition = "TEXT")
    private String mechanism;

    @Column(name = "clinical_effect", columnDefinition = "TEXT")
    private String clinicalEffect;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_level", nullable = false)
    private EvidenceLevel evidenceLevel;
}
