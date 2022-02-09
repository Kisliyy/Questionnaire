package com.kiselev.questionnaire.repository;

import com.kiselev.questionnaire.model.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {

    default Question findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found by id: " + id));
    }
}
