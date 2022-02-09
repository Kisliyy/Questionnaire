package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.model.AnswerVariant;
import com.kiselev.questionnaire.repository.AnswerVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerVariantService {
    @Autowired
    private AnswerVariantRepository answerVariantRepository;

    public void save(AnswerVariant answerVariant) {
        answerVariantRepository.save(answerVariant);
    }
}
