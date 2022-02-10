package com.kiselev.questionnaire.service.dto;

import com.kiselev.questionnaire.dto.AnswerVariantDTO;
import com.kiselev.questionnaire.dto.QuestionDTO;
import com.kiselev.questionnaire.dto.QuestionnaireDTO;
import com.kiselev.questionnaire.model.Questionnaire;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class QuestionnaireToDTO {

    public QuestionnaireDTO toDto(Questionnaire questionnaire) {
        return new QuestionnaireDTO(
                questionnaire.getId(),
                questionnaire.getName(),
                questionnaire.getDescription(),
                questionnaire.getDateStart(),
                questionnaire.getDateEnd(),
                questionnaire.getQuestions().stream().map(qs -> new QuestionDTO(
                        qs.getId(),
                        qs.getTypeQuestion(),
                        qs.getQuestionText(),
                        qs.getVariantList().stream().map(v -> new AnswerVariantDTO(
                                v.getId(),
                                v.getData()
                        )).collect(Collectors.toList())
                )).collect(Collectors.toList())
        );
    }
}
