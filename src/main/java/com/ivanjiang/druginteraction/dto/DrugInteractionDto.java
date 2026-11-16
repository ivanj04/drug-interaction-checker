package com.ivanjiang.druginteraction.dto;

import com.ivanjiang.druginteraction.model.enums.EvidenceLevel;
import com.ivanjiang.druginteraction.model.enums.InteractionType;
import com.ivanjiang.druginteraction.model.enums.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugInteractionDto {
    private Long id;

    @NotBlank(message = "Drug 1 name is required")
    private String drug1Name;

    @NotBlank(message = "Drug 2 name is required")
    private String drug2Name;

    @NotNull(message = "Severity level is required")
    private SeverityLevel severityLevel;

    @NotNull(message = "Interaction type is required")
    private InteractionType interactionType;

    private String mechanism;
    private String clinicalEffect;
    private String recommendation;

    @NotNull(message = "Evidence level is required")
    private EvidenceLevel evidenceLevel;
}
