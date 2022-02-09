package com.kiselev.questionnaire.dto;

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
public class QuestionnaireOfUserDTO implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("dateStart")
    private ZonedDateTime dateStart;

    @JsonProperty("dateEnd")
    private ZonedDateTime dateEnd;

    @JsonProperty("questions")
    private List<QuestionMarkerDTO> questions = new ArrayList<>();
}
