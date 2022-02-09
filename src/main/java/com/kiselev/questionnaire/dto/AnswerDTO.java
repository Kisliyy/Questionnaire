package com.kiselev.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO implements Serializable {

    @JsonProperty("questionId")
    private Long questionId;

    @JsonProperty("answerData")
    private String answerData;
}
