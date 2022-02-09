package com.kiselev.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVariantSelectableDTO implements Serializable, QuestionMarkerDTO {

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("selected")
    private Boolean isSelected;
}
