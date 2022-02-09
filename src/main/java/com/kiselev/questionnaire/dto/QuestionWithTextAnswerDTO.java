package com.kiselev.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kiselev.questionnaire.model.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionWithTextAnswerDTO implements Serializable, QuestionMarkerDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("typeQuestion")
    private TypeQuestion typeQuestion;

    @JsonProperty("questionText")
    private String questionText;

    @JsonProperty("answer")
    private String answer;

}

