package com.ivanjiang.druginteraction.repository;

import com.ivanjiang.druginteraction.model.Drug;
import com.ivanjiang.druginteraction.model.DrugInteraction;
import com.ivanjiang.druginteraction.model.enums.SeverityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrugInteractionRepository extends JpaRepository<DrugInteraction, Long> {

    @Query("SELECT di FROM DrugInteraction di WHERE " +
           "(di.drug1 = :drug1 AND di.drug2 = :drug2) OR " +
           "(di.drug1 = :drug2 AND di.drug2 = :drug1)")
    Optional<DrugInteraction> findByDrugPair(@Param("drug1") Drug drug1, @Param("drug2") Drug drug2);

    List<DrugInteraction> findBySeverityLevel(SeverityLevel severityLevel);

    @Query("SELECT di FROM DrugInteraction di WHERE di.drug1 = :drug OR di.drug2 = :drug")
    List<DrugInteraction> findAllByDrug(@Param("drug") Drug drug);
}
