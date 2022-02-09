package com.kiselev.questionnaire.repository;

import com.kiselev.questionnaire.model.Answer;
import org.springframework.data.repository.CrudRepository;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
}
