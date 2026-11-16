package com.ivanjiang.druginteraction.service;

import com.ivanjiang.druginteraction.dto.DrugInteractionDto;
import com.ivanjiang.druginteraction.dto.InteractionCheckResult;
import com.ivanjiang.druginteraction.model.Drug;
import com.ivanjiang.druginteraction.model.DrugInteraction;
import com.ivanjiang.druginteraction.model.enums.SeverityLevel;
import com.ivanjiang.druginteraction.repository.DrugInteractionRepository;
import com.ivanjiang.druginteraction.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ivanjiang.druginteraction.config.CacheConfig.INTERACTIONS_CACHE;

@Service
@RequiredArgsConstructor
public class InteractionService {

    private final DrugInteractionRepository interactionRepository;
    private final DrugRepository drugRepository;

    @Cacheable(value = INTERACTIONS_CACHE, key = "#drug1Name.toLowerCase() + ':' + #drug2Name.toLowerCase()")
    public InteractionCheckResult checkInteraction(String drug1Name, String drug2Name) {
        Optional<Drug> d1Opt = drugRepository.findByGenericNameIgnoreCase(drug1Name);
        Optional<Drug> d2Opt = drugRepository.findByGenericNameIgnoreCase(drug2Name);

        if (d1Opt.isEmpty()) {
            return InteractionCheckResult.builder()
                    .drug1(drug1Name)
                    .drug2(drug2Name)
                    .interactionFound(false)
                    .message("Drug not found in database: " + drug1Name)
                    .build();
        }

        if (d2Opt.isEmpty()) {
            return InteractionCheckResult.builder()
                    .drug1(drug1Name)
                    .drug2(drug2Name)
                    .interactionFound(false)
                    .message("Drug not found in database: " + drug2Name)
                    .build();
        }

        Drug drug1 = d1Opt.get();
        Drug drug2 = d2Opt.get();

        Optional<DrugInteraction> interaction = interactionRepository.findByDrugPair(drug1, drug2);

        if (interaction.isPresent()) {
            DrugInteraction di = interaction.get();
            return InteractionCheckResult.builder()
                    .drug1(drug1.getGenericName())
                    .drug2(drug2.getGenericName())
                    .interactionFound(true)
                    .interactionId(di.getId())
                    .severityLevel(di.getSeverityLevel())
                    .interactionType(di.getInteractionType())
                    .mechanism(di.getMechanism())
                    .clinicalEffect(di.getClinicalEffect())
                    .recommendation(di.getRecommendation())
                    .evidenceLevel(di.getEvidenceLevel())
                    .message("Interaction found between " + drug1.getGenericName() + " and " + drug2.getGenericName())
                    .build();
        }

        return InteractionCheckResult.builder()
                .drug1(drug1.getGenericName())
                .drug2(drug2.getGenericName())
                .interactionFound(false)
                .message("No known interaction between " + drug1.getGenericName() + " and " + drug2.getGenericName())
                .build();
    }

    public List<InteractionCheckResult> checkMultiple(List<String> drugNames) {
        List<InteractionCheckResult> results = new ArrayList<>();
        for (int i = 0; i < drugNames.size(); i++) {
            for (int j = i + 1; j < drugNames.size(); j++) {
                results.add(checkInteraction(drugNames.get(i), drugNames.get(j)));
            }
        }
        return results;
    }

    public DrugInteractionDto getInteractionById(Long id) {
        DrugInteraction di = interactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Interaction not found with id: " + id));
        return toDto(di);
    }

    public DrugInteractionDto createInteraction(DrugInteractionDto dto) {
        Drug drug1 = drugRepository.findByGenericNameIgnoreCase(dto.getDrug1Name())
                .orElseThrow(() -> new IllegalArgumentException("Drug not found: " + dto.getDrug1Name()));
        Drug drug2 = drugRepository.findByGenericNameIgnoreCase(dto.getDrug2Name())
                .orElseThrow(() -> new IllegalArgumentException("Drug not found: " + dto.getDrug2Name()));

        if (interactionRepository.findByDrugPair(drug1, drug2).isPresent()) {
            throw new IllegalArgumentException("Interaction already exists for this drug pair");
        }

        DrugInteraction interaction = DrugInteraction.builder()
                .drug1(drug1)
                .drug2(drug2)
                .severityLevel(dto.getSeverityLevel())
                .interactionType(dto.getInteractionType())
                .mechanism(dto.getMechanism())
                .clinicalEffect(dto.getClinicalEffect())
                .recommendation(dto.getRecommendation())
                .evidenceLevel(dto.getEvidenceLevel())
                .build();

        DrugInteraction saved = interactionRepository.save(interaction);
        return toDto(saved);
    }

    public List<DrugInteractionDto> getBySeverity(SeverityLevel level) {
        return interactionRepository.findBySeverityLevel(level)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private DrugInteractionDto toDto(DrugInteraction di) {
        return DrugInteractionDto.builder()
                .id(di.getId())
                .drug1Name(di.getDrug1().getGenericName())
                .drug2Name(di.getDrug2().getGenericName())
                .severityLevel(di.getSeverityLevel())
                .interactionType(di.getInteractionType())
                .mechanism(di.getMechanism())
                .clinicalEffect(di.getClinicalEffect())
                .recommendation(di.getRecommendation())
                .evidenceLevel(di.getEvidenceLevel())
                .build();
    }
}
