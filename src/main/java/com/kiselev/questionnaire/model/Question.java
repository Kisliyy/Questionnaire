package com.kiselev.questionnaire.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question")
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"variantList", "answerList"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "questionnaire_id", nullable = false)
    private Questionnaire questionnaire;

    @Enumerated(EnumType.STRING)
    private TypeQuestion typeQuestion;

    private String questionText;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerVariant> variantList = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<Answer> answerList = new ArrayList<>();

}
