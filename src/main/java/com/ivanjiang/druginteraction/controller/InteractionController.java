package com.ivanjiang.druginteraction.controller;

import com.ivanjiang.druginteraction.dto.DrugInteractionDto;
import com.ivanjiang.druginteraction.dto.InteractionCheckResult;
import com.ivanjiang.druginteraction.dto.MultiDrugCheckRequest;
import com.ivanjiang.druginteraction.model.enums.SeverityLevel;
import com.ivanjiang.druginteraction.service.InteractionService;
import com.ivanjiang.druginteraction.service.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Tag(name = "Drug Interactions", description = "Drug interaction checking and management")
public class InteractionController {

    private final InteractionService interactionService;
    private final RateLimitService rateLimitService;

    @GetMapping("/check")
    @Operation(summary = "Check interaction between two drugs (cached)")
    public ResponseEntity<?> checkInteraction(
            @RequestParam String drug1,
            @RequestParam String drug2,
            HttpServletRequest request,
            Authentication auth) {

        String ip = getClientIp(request);
        String username = auth != null ? auth.getName() : null;

        if (!rateLimitService.tryConsume(ip, username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Please try again later.");
        }

        return ResponseEntity.ok(interactionService.checkInteraction(drug1, drug2));
    }

    @PostMapping("/check-multiple")
    @Operation(summary = "Check all pairwise interactions for a list of drugs")
    public ResponseEntity<?> checkMultiple(
            @Valid @RequestBody MultiDrugCheckRequest request,
            HttpServletRequest httpRequest,
            Authentication auth) {

        String ip = getClientIp(httpRequest);
        String username = auth != null ? auth.getName() : null;

        if (!rateLimitService.tryConsume(ip, username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Please try again later.");
        }

        List<InteractionCheckResult> results = interactionService.checkMultiple(request.getDrugs());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get interaction by ID")
    public ResponseEntity<DrugInteractionDto> getInteraction(@PathVariable Long id,
                                                              HttpServletRequest request,
                                                              Authentication auth) {
        String ip = getClientIp(request);
        String username = auth != null ? auth.getName() : null;

        if (!rateLimitService.tryConsume(ip, username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        return ResponseEntity.ok(interactionService.getInteractionById(id));
    }

    @PostMapping
    @Operation(summary = "Add a new drug interaction (ADMIN only)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<DrugInteractionDto> createInteraction(@Valid @RequestBody DrugInteractionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interactionService.createInteraction(dto));
    }

    @GetMapping("/by-severity")
    @Operation(summary = "List all interactions by severity level")
    public ResponseEntity<?> getBySeverity(
            @RequestParam SeverityLevel level,
            HttpServletRequest request,
            Authentication auth) {

        String ip = getClientIp(request);
        String username = auth != null ? auth.getName() : null;

        if (!rateLimitService.tryConsume(ip, username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Please try again later.");
        }

        return ResponseEntity.ok(interactionService.getBySeverity(level));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
