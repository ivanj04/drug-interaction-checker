package com.ivanjiang.druginteraction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugDto {
    private Long id;

    @NotBlank(message = "Generic name is required")
    private String genericName;

    private String brandNames;
    private String drugClass;
    private String description;
}
