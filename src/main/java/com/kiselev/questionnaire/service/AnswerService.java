package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.model.Answer;
import com.kiselev.questionnaire.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    @Autowired
    private AnswerRepository answerRepository;

    public void save(Answer answer) {
        answerRepository.save(answer);
    }
}
