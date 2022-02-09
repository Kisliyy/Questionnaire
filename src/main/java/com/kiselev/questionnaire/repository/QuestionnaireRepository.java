package com.kiselev.questionnaire.repository;

import com.kiselev.questionnaire.model.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    default Questionnaire findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Questionnaire not found by id: " + id));
    }
}
