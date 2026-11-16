package com.ivanjiang.druginteraction.dto;

import com.ivanjiang.druginteraction.model.enums.EvidenceLevel;
import com.ivanjiang.druginteraction.model.enums.InteractionType;
import com.ivanjiang.druginteraction.model.enums.SeverityLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionCheckResult {
    private String drug1;
    private String drug2;
    private boolean interactionFound;
    private Long interactionId;
    private SeverityLevel severityLevel;
    private InteractionType interactionType;
    private String mechanism;
    private String clinicalEffect;
    private String recommendation;
    private EvidenceLevel evidenceLevel;
    private String message;
}
