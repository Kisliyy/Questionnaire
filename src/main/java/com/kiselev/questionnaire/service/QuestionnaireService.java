package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.dto.*;
import com.kiselev.questionnaire.dto.creation.QuestionnaireCreationDTO;
import com.kiselev.questionnaire.model.*;
import com.kiselev.questionnaire.repository.AnswerRepository;
import com.kiselev.questionnaire.repository.AnswerVariantRepository;
import com.kiselev.questionnaire.repository.QuestionRepository;
import com.kiselev.questionnaire.repository.QuestionnaireRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

    public Questionnaire getQuestionnaire(Long id) {
        return questionnaireRepository.findByIdOrThrow(id);
    }

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
                .map(this::toDto)
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
        return toDto(questionnaire);
    }

    private QuestionnaireDTO toDto(Questionnaire questionnaire) {
        return new QuestionnaireDTO(
                questionnaire.getId(),
                questionnaire.getName(),
                questionnaire.getDescription(),
                questionnaire.getDateStart(),
                questionnaire.getDateEnd(),
                questionnaire.getQuestions().stream().map(qs -> new QuestionDTO(
                        qs.getId(),
                        qs.getTypeQuestion(),
                        qs.getQuestionText(),
                        qs.getVariantList().stream().map(v -> new AnswerVariantDTO(
                                v.getId(),
                                v.getData()
                        )).collect(Collectors.toList())
                )).collect(Collectors.toList())
        );
    }

    @Transactional
    public QuestionnaireDTO update(Long questionnaireId, QuestionnaireDTO updateQuestionnaireDTO) {
        if (questionnaireRepository.existsById(questionnaireId)) {
            //TODO: просетить поля
            Questionnaire questionnaire = questionnaireRepository.getById(questionnaireId);
            questionnaire.setDescription(updateQuestionnaireDTO.getDescription());
            questionnaire.setName(updateQuestionnaireDTO.getName());
            questionnaire.setDateStart(updateQuestionnaireDTO.getDateStart());
            questionnaire.setDateEnd(updateQuestionnaireDTO.getDateEnd());
            questionnaire.setQuestions(updateQuestions(questionnaire, updateQuestionnaireDTO.getQuestions()));
            questionnaireRepository.save(questionnaire);
            return toDto(questionnaire);
        }
        return updateQuestionnaireDTO;
    }

    private List<Question> updateQuestions(Questionnaire questionnaire, List<QuestionDTO> questions) {
        List<Question> existingQuestions = questionnaire.getQuestions();
        Map<Long, QuestionDTO> questionsIdList = questions.stream()
                .collect(Collectors.toMap(QuestionDTO::getId, q -> q));

        //Удаление
        existingQuestions.removeIf(q -> !questionsIdList.containsKey(q.getId()));

        //Обновление
        existingQuestions.stream()
                .filter(q -> questionsIdList.containsKey(q.getId()))
                .peek(q -> {
                    QuestionDTO updateQuestion = questionsIdList.get(q.getId());
                    q.setQuestionText(updateQuestion.getQuestionText());
                    q.setTypeQuestion(updateQuestion.getTypeQuestion());
                    q.setVariantList(updateAnswerVariant(q, updateQuestion.getVariantList()));
                }).collect(Collectors.toList());

        //Добавление
        List<Question> questionList = questions.stream()
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

        existingQuestions.addAll(questionList);
        questionnaire.setQuestions(existingQuestions);
        return existingQuestions;
    }


    private List<AnswerVariant> updateAnswerVariant(Question q, List<AnswerVariantDTO> variantList) {
        List<AnswerVariant> existingQuestioningAnswerVariants = q.getVariantList();
        Map<Long, AnswerVariantDTO> answerIdList = variantList
                .stream()
                .collect(Collectors.toMap(AnswerVariantDTO::getId, aw -> aw));

        //удаление ненужных вариантов ответов
        existingQuestioningAnswerVariants.removeIf(av -> !answerIdList.containsKey(av.getId()));

        //обновление вариантов ответа
        existingQuestioningAnswerVariants.stream()
                .filter(av -> answerIdList.containsKey(av.getId()))
                .peek(av -> {
                    AnswerVariantDTO answerVariantDTO = answerIdList.get(av.getId());
                    av.setData(answerVariantDTO.getAnswer());
                    av.setQuestion(q);
                }).collect(Collectors.toList());

        //добавление новых вариантов ответа
        List<AnswerVariant> newAnswerVariant = existingQuestioningAnswerVariants.stream()
                .filter(av -> av.getId() == null)
                .peek(av -> {
                    AnswerVariantDTO answerVariantDTO = answerIdList.get(null);
                    AnswerVariant.builder()
                            .data(answerVariantDTO.getAnswer())
                            .question(q);
                }).collect(Collectors.toList());
        existingQuestioningAnswerVariants.addAll(newAnswerVariant);

        return answerVariantRepository.saveAll(existingQuestioningAnswerVariants);
    }

    public void delete(Long questionnaireId) {
        questionnaireRepository.deleteById(questionnaireId);
    }
}

