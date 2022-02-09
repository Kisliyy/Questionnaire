package com.kiselev.questionnaire.model;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "questionnaire")
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"answeredUsers", "questions"})
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;

    private ZonedDateTime dateStart;

    private ZonedDateTime dateEnd;

    @ManyToMany(mappedBy = "questionnaire")
    private List<User> answeredUsers = new ArrayList<>();

    @OneToMany(mappedBy = "questionnaire", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

}
