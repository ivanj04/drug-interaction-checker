package com.ivanjiang.druginteraction.service;

import com.ivanjiang.druginteraction.dto.DrugDto;
import com.ivanjiang.druginteraction.model.Drug;
import com.ivanjiang.druginteraction.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrugService {

    private final DrugRepository drugRepository;

    public Page<DrugDto> searchDrugs(String query, Pageable pageable) {
        Page<Drug> drugs;
        if (query == null || query.isBlank()) {
            drugs = drugRepository.findAll(pageable);
        } else {
            drugs = drugRepository
                    .findByGenericNameContainingIgnoreCaseOrBrandNamesContainingIgnoreCase(query, query, pageable);
        }
        return drugs.map(this::toDto);
    }

    public DrugDto getDrugById(Long id) {
        Drug drug = drugRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Drug not found with id: " + id));
        return toDto(drug);
    }

    public DrugDto createDrug(DrugDto dto) {
        if (drugRepository.findByGenericNameIgnoreCase(dto.getGenericName()).isPresent()) {
            throw new IllegalArgumentException("Drug already exists: " + dto.getGenericName());
        }
        Drug drug = Drug.builder()
                .genericName(dto.getGenericName())
                .brandNames(dto.getBrandNames())
                .drugClass(dto.getDrugClass())
                .description(dto.getDescription())
                .build();
        Drug saved = drugRepository.save(drug);
        return toDto(saved);
    }

    public Drug findByGenericName(String name) {
        return drugRepository.findByGenericNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("Drug not found: " + name));
    }

    private DrugDto toDto(Drug drug) {
        return DrugDto.builder()
                .id(drug.getId())
                .genericName(drug.getGenericName())
                .brandNames(drug.getBrandNames())
                .drugClass(drug.getDrugClass())
                .description(drug.getDescription())
                .build();
    }
}
