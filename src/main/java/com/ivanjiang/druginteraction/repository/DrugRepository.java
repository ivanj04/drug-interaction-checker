package com.ivanjiang.druginteraction.repository;

import com.ivanjiang.druginteraction.model.Drug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

    Optional<Drug> findByGenericNameIgnoreCase(String genericName);

    Page<Drug> findByGenericNameContainingIgnoreCaseOrBrandNamesContainingIgnoreCase(
            String genericName, String brandNames, Pageable pageable);
}
