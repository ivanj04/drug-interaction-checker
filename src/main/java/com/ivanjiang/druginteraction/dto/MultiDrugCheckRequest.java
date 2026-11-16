package com.ivanjiang.druginteraction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultiDrugCheckRequest {

    @NotNull(message = "Drug list is required")
    @Size(min = 2, message = "At least 2 drugs required for interaction check")
    private List<String> drugs;
}
