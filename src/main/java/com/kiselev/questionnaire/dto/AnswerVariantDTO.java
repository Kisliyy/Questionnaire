package com.kiselev.questionnaire.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVariantDTO implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("answer")
    private String answer;

}
