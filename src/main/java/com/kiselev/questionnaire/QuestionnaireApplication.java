package com.kiselev.questionnaire;

import com.kiselev.questionnaire.model.AnswerVariant;
import com.kiselev.questionnaire.model.Question;
import com.kiselev.questionnaire.model.Questionnaire;
import com.kiselev.questionnaire.model.TypeQuestion;
import com.kiselev.questionnaire.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.ZonedDateTime;
import java.util.Collections;

@SpringBootApplication
public class QuestionnaireApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuestionnaireApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(QuestionRepository questionRepository,
                                  UserRepository userRepository,
                                  QuestionnaireRepository questionnaireRepository,
                                  AnswerRepository answerRepository,
                                  AnswerVariantRepository answerVariantRepository) {
        return args -> {
            Questionnaire questionnaire = questionnaireRepository.save(new Questionnaire(
                    1L,
                    "Lolll",
                    "Description",
                    ZonedDateTime.now(),
                    ZonedDateTime.now(),
                    Collections.emptyList(),
                    Collections.emptyList()));
            Question question = questionRepository.save(Question.builder()
                    .typeQuestion(TypeQuestion.TEXT)
                    .questionnaire(questionnaire)
                    .build());
            answerVariantRepository.save(new AnswerVariant(1L,
                    "No name",
                    question));
        };

    }

}
