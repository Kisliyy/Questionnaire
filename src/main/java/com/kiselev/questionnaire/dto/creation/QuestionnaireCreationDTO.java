package com.kiselev.questionnaire.dto.creation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireCreationDTO implements Serializable {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("dateStart")
    private ZonedDateTime dateStart;

    @JsonProperty("dateEnd")
    private ZonedDateTime dateEnd;

    @JsonProperty("questions")
    private List<QuestionCreationDTO> questions = new ArrayList<>();
}
