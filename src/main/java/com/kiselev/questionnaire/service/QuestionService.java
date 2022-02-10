package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.dto.QuestionDTO;
import com.kiselev.questionnaire.model.AnswerVariant;
import com.kiselev.questionnaire.model.Question;
import com.kiselev.questionnaire.model.Questionnaire;
import com.kiselev.questionnaire.repository.AnswerVariantRepository;
import com.kiselev.questionnaire.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerVariantService answerVariantService;

    @Autowired
    private AnswerVariantRepository answerVariantRepository;

    @Transactional
    public List<Question> updateQuestions(Questionnaire questionnaire, List<QuestionDTO> questions) {
        List<Question> existingQuestions = questionnaire.getQuestions();
        Map<Long, QuestionDTO> questionsIdList = questions.stream()
                .collect(Collectors.toMap(QuestionDTO::getId, q -> q));

        //Удаление неиспользуемых вопросов
        removeQuestion(existingQuestions, questionsIdList);
        //обновление существующих вопросов
        updateQuestions(existingQuestions, questionsIdList);
        //создание новых вопросов
        List<Question> questionList = create(questions, questionnaire);
        //Добавление новых вопросов в опросник
        existingQuestions.addAll(questionList);
        //Сохранение вопросов в опроснике
        questionnaire.setQuestions(existingQuestions);
        return existingQuestions;
    }

    private void removeQuestion(List<Question> existingQuestions, Map<Long, QuestionDTO> questionsIdList) {
        existingQuestions.removeIf(q -> !questionsIdList.containsKey(q.getId()));
    }

    private void updateQuestions(List<Question> existingQuestions, Map<Long, QuestionDTO> questionsIdList) {
        existingQuestions.stream()
                .filter(q -> questionsIdList.containsKey(q.getId()))
                .peek(q -> {
                    QuestionDTO updateQuestion = questionsIdList.get(q.getId());
                    q.setQuestionText(updateQuestion.getQuestionText());
                    q.setTypeQuestion(updateQuestion.getTypeQuestion());
                    q.setVariantList(answerVariantService.updateAnswerVariant(q, updateQuestion.getVariantList()));
                });
    }

    private List<Question> create(List<QuestionDTO> questions, Questionnaire questionnaire) {
        return questions.stream()
                .filter(q -> q.getId() == null)
                .map(q -> {
                    Question question = questionRepository.save(Question.builder()
                            .questionnaire(questionnaire)
                            .questionText(q.getQuestionText())
                            .typeQuestion(q.getTypeQuestion())
                            .build());

                    List<AnswerVariant> answerVariantList = answerVariantRepository.saveAll(
                            q.getVariantList().stream()
                                    .map(av -> AnswerVariant.builder()
                                            .data(av.getAnswer())
                                            .question(question)
                                            .build())
                                    .collect(Collectors.toList()));
                    question.setVariantList(answerVariantList);
                    return question;
                }).collect(Collectors.toList());
    }
}
