package com.kiselev.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kiselev.questionnaire.model.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("typeQuestion")
    private TypeQuestion typeQuestion;

    @JsonProperty("questionText")
    private String questionText;

    @JsonProperty("variantList")
    private List<AnswerVariantDTO> variantList;
}
