package com.ivanjiang.druginteraction.controller;

import com.ivanjiang.druginteraction.dto.DrugDto;
import com.ivanjiang.druginteraction.service.DrugService;
import com.ivanjiang.druginteraction.service.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
@Tag(name = "Drugs", description = "Drug search and management")
public class DrugController {

    private final DrugService drugService;
    private final RateLimitService rateLimitService;

    @GetMapping
    @Operation(summary = "Search drugs by name (paginated)")
    public ResponseEntity<?> searchDrugs(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest request,
            Authentication auth) {

        String ip = getClientIp(request);
        String username = auth != null ? auth.getName() : null;

        if (!rateLimitService.tryConsume(ip, username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Please try again later.");
        }

        Page<DrugDto> drugs = drugService.searchDrugs(query, pageable);
        return ResponseEntity.ok(drugs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get drug by ID")
    public ResponseEntity<DrugDto> getDrug(@PathVariable Long id,
                                            HttpServletRequest request,
                                            Authentication auth) {
        String ip = getClientIp(request);
        String username = auth != null ? auth.getName() : null;

        if (!rateLimitService.tryConsume(ip, username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        return ResponseEntity.ok(drugService.getDrugById(id));
    }

    @PostMapping
    @Operation(summary = "Add a new drug (ADMIN only)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<DrugDto> createDrug(@Valid @RequestBody DrugDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drugService.createDrug(dto));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
