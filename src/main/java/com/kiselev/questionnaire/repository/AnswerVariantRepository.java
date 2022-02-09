package com.kiselev.questionnaire.repository;

import com.kiselev.questionnaire.model.AnswerVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerVariantRepository extends JpaRepository<AnswerVariant, Long> {
}
