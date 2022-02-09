package com.kiselev.questionnaire.dto.creation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kiselev.questionnaire.model.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreationDTO implements Serializable {

    @JsonProperty("typeQuestion")
    private TypeQuestion typeQuestion;

    @JsonProperty("questionText")
    private String questionText;

    @JsonProperty("variantList")
    private List<String> variantList = new ArrayList<>();

}
