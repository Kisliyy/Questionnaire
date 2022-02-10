package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.dto.AnswerVariantDTO;
import com.kiselev.questionnaire.model.AnswerVariant;
import com.kiselev.questionnaire.model.Question;
import com.kiselev.questionnaire.repository.AnswerVariantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnswerVariantService {
    @Autowired
    private AnswerVariantRepository answerVariantRepository;

    @Transactional
    public List<AnswerVariant> updateAnswerVariant(Question question, List<AnswerVariantDTO> variantList) {
        List<AnswerVariant> existingQuestioningAnswerVariants = question.getVariantList();
        Map<Long, AnswerVariantDTO> answerIdList = variantList
                .stream()
                .collect(Collectors.toMap(AnswerVariantDTO::getId, aw -> aw));

        //удаление ненужных вариантов ответов
        existingQuestioningAnswerVariants.removeIf(av -> !answerIdList.containsKey(av.getId()));

        //обновление вариантов ответа
        existingQuestioningAnswerVariants = updateAnswerVariant(existingQuestioningAnswerVariants, answerIdList, question);

        //создание новых вариантов ответа
        List<AnswerVariant> newAnswerVariant = create(question, answerIdList, existingQuestioningAnswerVariants);
        //сохранение новых вариантов ответов
        existingQuestioningAnswerVariants.addAll(newAnswerVariant);

        return answerVariantRepository.saveAll(existingQuestioningAnswerVariants);
    }

    //обновление вариантов ответа
    private List<AnswerVariant> updateAnswerVariant(List<AnswerVariant> existingQuestioningAnswerVariants,
                                                    Map<Long, AnswerVariantDTO> answerVariantDTOMap,
                                                    Question question) {
        return existingQuestioningAnswerVariants.stream()
                .filter(av -> answerVariantDTOMap.containsKey(av.getId()))
                .peek(av -> {
                            av.setData(answerVariantDTOMap.get(av.getId()).getAnswer());
                            av.setQuestion(question);
                        }
                ).collect(Collectors.toList());
    }

    //добавление новых вариантов ответа
    private List<AnswerVariant> create(Question question,
                                       Map<Long, AnswerVariantDTO> answerIdList,
                                       List<AnswerVariant> existingQuestioningAnswerVariants) {

        List<AnswerVariant> newAnswerVariant = existingQuestioningAnswerVariants.stream()
                .filter(av -> av.getId() == null)
                .peek(av -> {
                    AnswerVariantDTO answerVariantDTO = answerIdList.get(null);
                    AnswerVariant.builder()
                            .data(answerVariantDTO.getAnswer())
                            .question(question);
                }).collect(Collectors.toList());
        existingQuestioningAnswerVariants.addAll(newAnswerVariant);
        return newAnswerVariant;
    }
}
