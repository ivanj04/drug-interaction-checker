package com.ivanjiang.druginteraction.service;

import com.ivanjiang.druginteraction.dto.InteractionCheckResult;
import com.ivanjiang.druginteraction.model.Drug;
import com.ivanjiang.druginteraction.model.DrugInteraction;
import com.ivanjiang.druginteraction.model.enums.EvidenceLevel;
import com.ivanjiang.druginteraction.model.enums.InteractionType;
import com.ivanjiang.druginteraction.model.enums.SeverityLevel;
import com.ivanjiang.druginteraction.repository.DrugInteractionRepository;
import com.ivanjiang.druginteraction.repository.DrugRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {

    @Mock
    private DrugInteractionRepository interactionRepository;

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private InteractionService interactionService;

    private Drug warfarin;
    private Drug aspirin;
    private DrugInteraction warfarinAspirin;

    @BeforeEach
    void setUp() {
        warfarin = Drug.builder()
                .id(1L)
                .genericName("warfarin")
                .drugClass("Anticoagulant")
                .build();

        aspirin = Drug.builder()
                .id(2L)
                .genericName("aspirin")
                .drugClass("NSAID / Antiplatelet")
                .build();

        warfarinAspirin = DrugInteraction.builder()
                .id(1L)
                .drug1(warfarin)
                .drug2(aspirin)
                .severityLevel(SeverityLevel.MAJOR)
                .interactionType(InteractionType.PHARMACODYNAMIC)
                .mechanism("Aspirin inhibits platelet aggregation; combined with warfarin significantly increases hemorrhagic risk.")
                .clinicalEffect("Increased risk of serious bleeding including GI hemorrhage.")
                .recommendation("Avoid concomitant use unless specifically indicated.")
                .evidenceLevel(EvidenceLevel.WELL_DOCUMENTED)
                .build();
    }

    @Test
    void checkInteraction_foundInForwardDirection() {
        when(drugRepository.findByGenericNameIgnoreCase("warfarin")).thenReturn(Optional.of(warfarin));
        when(drugRepository.findByGenericNameIgnoreCase("aspirin")).thenReturn(Optional.of(aspirin));
        when(interactionRepository.findByDrugPair(warfarin, aspirin)).thenReturn(Optional.of(warfarinAspirin));

        InteractionCheckResult result = interactionService.checkInteraction("warfarin", "aspirin");

        assertThat(result.isInteractionFound()).isTrue();
        assertThat(result.getSeverityLevel()).isEqualTo(SeverityLevel.MAJOR);
        assertThat(result.getDrug1()).isEqualTo("warfarin");
        assertThat(result.getDrug2()).isEqualTo("aspirin");
        assertThat(result.getEvidenceLevel()).isEqualTo(EvidenceLevel.WELL_DOCUMENTED);
    }

    @Test
    void checkInteraction_foundInReverseDirection() {
        when(drugRepository.findByGenericNameIgnoreCase("aspirin")).thenReturn(Optional.of(aspirin));
        when(drugRepository.findByGenericNameIgnoreCase("warfarin")).thenReturn(Optional.of(warfarin));
        when(interactionRepository.findByDrugPair(aspirin, warfarin)).thenReturn(Optional.of(warfarinAspirin));

        InteractionCheckResult result = interactionService.checkInteraction("aspirin", "warfarin");

        assertThat(result.isInteractionFound()).isTrue();
        assertThat(result.getSeverityLevel()).isEqualTo(SeverityLevel.MAJOR);
    }

    @Test
    void checkInteraction_noInteractionFound() {
        Drug metformin = Drug.builder().id(3L).genericName("metformin").build();

        when(drugRepository.findByGenericNameIgnoreCase("warfarin")).thenReturn(Optional.of(warfarin));
        when(drugRepository.findByGenericNameIgnoreCase("metformin")).thenReturn(Optional.of(metformin));
        when(interactionRepository.findByDrugPair(warfarin, metformin)).thenReturn(Optional.empty());

        InteractionCheckResult result = interactionService.checkInteraction("warfarin", "metformin");

        assertThat(result.isInteractionFound()).isFalse();
        assertThat(result.getMessage()).contains("No known interaction");
    }

    @Test
    void checkInteraction_drugNotFound() {
        when(drugRepository.findByGenericNameIgnoreCase("unknowndrug")).thenReturn(Optional.empty());

        InteractionCheckResult result = interactionService.checkInteraction("unknowndrug", "aspirin");

        assertThat(result.isInteractionFound()).isFalse();
        assertThat(result.getMessage()).contains("Drug not found in database");
    }

    @Test
    void checkMultiple_returnsPairwiseCombinations() {
        Drug metformin = Drug.builder().id(3L).genericName("metformin").build();

        when(drugRepository.findByGenericNameIgnoreCase("warfarin")).thenReturn(Optional.of(warfarin));
        when(drugRepository.findByGenericNameIgnoreCase("aspirin")).thenReturn(Optional.of(aspirin));
        when(drugRepository.findByGenericNameIgnoreCase("metformin")).thenReturn(Optional.of(metformin));

        when(interactionRepository.findByDrugPair(warfarin, aspirin)).thenReturn(Optional.of(warfarinAspirin));
        when(interactionRepository.findByDrugPair(warfarin, metformin)).thenReturn(Optional.empty());
        when(interactionRepository.findByDrugPair(aspirin, metformin)).thenReturn(Optional.empty());

        List<InteractionCheckResult> results = interactionService.checkMultiple(
                List.of("warfarin", "aspirin", "metformin"));

        // 3 drugs = 3 pairwise combinations (C(3,2) = 3)
        assertThat(results).hasSize(3);

        // The warfarin-aspirin interaction should be found
        InteractionCheckResult warfarinAspirin = results.stream()
                .filter(r -> r.getDrug1().equals("warfarin") && r.getDrug2().equals("aspirin"))
                .findFirst()
                .orElseThrow();
        assertThat(warfarinAspirin.isInteractionFound()).isTrue();
        assertThat(warfarinAspirin.getSeverityLevel()).isEqualTo(SeverityLevel.MAJOR);
    }

    @Test
    void checkMultiple_fourDrugsReturnsSixCombinations() {
        Drug metformin = Drug.builder().id(3L).genericName("metformin").build();
        Drug lisinopril = Drug.builder().id(4L).genericName("lisinopril").build();

        when(drugRepository.findByGenericNameIgnoreCase(any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            return switch (name) {
                case "warfarin" -> Optional.of(warfarin);
                case "aspirin" -> Optional.of(aspirin);
                case "metformin" -> Optional.of(metformin);
                case "lisinopril" -> Optional.of(lisinopril);
                default -> Optional.empty();
            };
        });
        when(interactionRepository.findByDrugPair(any(), any())).thenReturn(Optional.empty());

        List<InteractionCheckResult> results = interactionService.checkMultiple(
                List.of("warfarin", "aspirin", "metformin", "lisinopril"));

        // C(4,2) = 6 combinations
        assertThat(results).hasSize(6);
    }
}
