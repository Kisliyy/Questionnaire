package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.dto.*;
import com.kiselev.questionnaire.dto.creation.QuestionnaireCreationDTO;
import com.kiselev.questionnaire.model.*;
import com.kiselev.questionnaire.repository.AnswerRepository;
import com.kiselev.questionnaire.repository.AnswerVariantRepository;
import com.kiselev.questionnaire.repository.QuestionRepository;
import com.kiselev.questionnaire.repository.QuestionnaireRepository;
import com.kiselev.questionnaire.service.dto.QuestionnaireToDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuestionnaireService {

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerVariantRepository answerVariantRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionnaireToDTO questionnaireToDTO;


    public List<Questionnaire> getAll() {
        return questionnaireRepository.findAll();
    }

    @Transactional
    public void saveAnswer(String userId, Long questionnaireId, AnswerDTO answerDTO) {
        Questionnaire questionnaire = questionnaireRepository.findByIdOrThrow(questionnaireId);
        User user = userService.getOrCreateUser(userId);
        user.getQuestionnaire().add(questionnaire);
        questionnaire.getAnsweredUsers().add(user);

        Question question = questionRepository.findByIdOrThrow(answerDTO.getQuestionId());
        Answer answer = Answer.builder()
                .question(question)
                .respondent(user)
                .answerData(answerDTO.getAnswerData())
                .build();
        answerRepository.save(answer);
        question.getAnswerList().add(answer);
    }

    @Transactional(readOnly = true)
    public List<QuestionnaireOfUserDTO> getQuestionnaireDTOWithAnswer(String userId) {
        User user = userService.getUser(userId);
        return user.getQuestionnaire()
                .stream()
                .map(q -> new QuestionnaireOfUserDTO(
                        q.getId(),
                        q.getName(),
                        q.getDescription(),
                        q.getDateStart(),
                        q.getDateEnd(),
                        q.getQuestions().stream()
                                .map(qs -> {
                                    List<Answer> answersOfUser = qs.getAnswerList().stream()
                                            .peek(a -> log.info(a.toString()))
                                            .filter(a -> a.getRespondent().getId().equals(userId))
                                            .collect(Collectors.toList());
                                    switch (qs.getTypeQuestion()) {
                                        case TEXT:
                                            return new QuestionWithTextAnswerDTO(
                                                    qs.getId(),
                                                    qs.getTypeQuestion(),
                                                    qs.getQuestionText(),
                                                    answersOfUser.get(0).getAnswerData()
                                            );
                                        case ONE_ANSWER:
                                        case MULTIPLY_ANSWER:
                                            return new QuestionWithVariantsAnswerDTO(
                                                    qs.getId(),
                                                    qs.getTypeQuestion(),
                                                    qs.getQuestionText(),
                                                    qs.getVariantList().stream().map(variant -> new AnswerVariantSelectableDTO(
                                                            variant.getData(),
                                                            answersOfUser.stream()
                                                                    .anyMatch(a -> a.getAnswerData().equals(variant.getData()))
                                                    )).collect(Collectors.toList())
                                            );
                                        default:
                                            return null;
                                    }
                                }).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionnaireDTO> getCollect() {
        return getAll().stream()
                .map(qd -> questionnaireToDTO.toDto(qd))
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionnaireDTO create(QuestionnaireCreationDTO questionnaireDTO) {
        Questionnaire questionnaire = questionnaireRepository.save(
                Questionnaire.builder()
                        .name(questionnaireDTO.getName())
                        .dateStart(questionnaireDTO.getDateStart())
                        .dateEnd(questionnaireDTO.getDateEnd())
                        .description(questionnaireDTO.getDescription())
                        .build()
        );
        List<Question> questions = questionnaireDTO.getQuestions()
                .stream()
                .map(qu -> {
                    Question question = questionRepository.save(
                            Question.builder()
                                    .questionnaire(questionnaire)
                                    .typeQuestion(qu.getTypeQuestion())
                                    .questionText(qu.getQuestionText())
                                    .build()
                    );
                    List<AnswerVariant> answerVariants = answerVariantRepository.saveAll(
                            qu.getVariantList().stream()
                                    .map(vl -> AnswerVariant.builder()
                                            .question(question)
                                            .data(vl)
                                            .build()
                                    ).collect(Collectors.toList()));
                    question.setVariantList(answerVariants);
                    return question;
                })
                .collect(Collectors.toList());
        questionnaire.setQuestions(questions);
        return questionnaireToDTO.toDto(questionnaire);
    }


    @Transactional
    public QuestionnaireDTO update(Long questionnaireId, QuestionnaireDTO updateQuestionnaireDTO) {
        if (questionnaireRepository.existsById(questionnaireId)) {

            Questionnaire questionnaire = questionnaireRepository.getById(questionnaireId);
            questionnaire.setDescription(updateQuestionnaireDTO.getDescription());
            questionnaire.setName(updateQuestionnaireDTO.getName());
            questionnaire.setDateStart(updateQuestionnaireDTO.getDateStart());
            questionnaire.setDateEnd(updateQuestionnaireDTO.getDateEnd());
            if (questionnaire.getDateStart() == null) {
                questionnaire.setQuestions(questionService.updateQuestions(questionnaire, updateQuestionnaireDTO.getQuestions()));
            }
            questionnaireRepository.save(questionnaire);

            return questionnaireToDTO.toDto(questionnaire);
        }
        return updateQuestionnaireDTO;
    }

    public void delete(Long questionnaireId) {
        questionnaireRepository.deleteById(questionnaireId);
    }
}

