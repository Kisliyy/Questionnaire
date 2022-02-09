package com.kiselev.questionnaire.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "answer_variant")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AnswerVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String data;

    @JoinColumn(name = "question_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

}
