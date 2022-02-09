package com.kiselev.questionnaire.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Entity
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"responses", "questionnaire"})
public class User {
    @Id
    private String id;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_questionnaire",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "questionnaire_id")})
    private List<Questionnaire> questionnaire = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "respondent")
    private List<Answer> responses = new ArrayList<>();

}
