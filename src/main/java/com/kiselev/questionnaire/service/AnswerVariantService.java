package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.dto.AnswerVariantDTO;
import com.kiselev.questionnaire.model.AnswerVariant;
import com.kiselev.questionnaire.model.Question;
import com.kiselev.questionnaire.repository.AnswerVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        removeAnswerVariant(existingQuestioningAnswerVariants, answerIdList);
//        existingQuestioningAnswerVariants.removeIf(av -> !answerIdList.containsKey(av.getId()));

        //обновление вариантов ответа
        updateAnswerVariant(existingQuestioningAnswerVariants, answerIdList, question);
      /*  existingQuestioningAnswerVariants.stream()
                .filter(av -> answerIdList.containsKey(av.getId()))
                .peek(av -> {
                    AnswerVariantDTO answerVariantDTO = answerIdList.get(av.getId());
                    av.setData(answerVariantDTO.getAnswer());
                    av.setQuestion(question);
                }).collect(Collectors.toList());*/


        /*List<AnswerVariant> newAnswerVariant = existingQuestioningAnswerVariants.stream()
                .filter(av -> av.getId() == null)
                .peek(av -> {
                    AnswerVariantDTO answerVariantDTO = answerIdList.get(null);
                    AnswerVariant.builder()
                            .data(answerVariantDTO.getAnswer())
                            .question(question);
                }).collect(Collectors.toList());
                */
        List<AnswerVariant> newAnswerVariant = create(question, answerIdList, existingQuestioningAnswerVariants);
        existingQuestioningAnswerVariants.addAll(newAnswerVariant);

        return answerVariantRepository.saveAll(existingQuestioningAnswerVariants);
    }

    //удаление ненужных вариантов ответов
    private void removeAnswerVariant(List<AnswerVariant> answerVariants, Map<Long, AnswerVariantDTO> answerVariantDTOMap) {
        answerVariants.removeIf(av -> !answerVariantDTOMap.containsKey(av.getId()));
    }

    //обновление вариантов ответа
    private void updateAnswerVariant(List<AnswerVariant> answerVariants, Map<Long, AnswerVariantDTO> answerVariantDTOMap, Question question) {
        answerVariants.stream()
                .filter(av -> answerVariantDTOMap.containsKey(av.getId()))
                .peek(av -> {
                    AnswerVariantDTO answerVariantDTO = answerVariantDTOMap.get(av.getId());
                    av.setData(answerVariantDTO.getAnswer());
                    av.setQuestion(question);
                });
    }

    //добавление новых вариантов ответа
    private List<AnswerVariant> create(Question question, Map<Long, AnswerVariantDTO> answerVariantDTOMap, List<AnswerVariant> answerVariants) {
        List<AnswerVariant> newAnswerVariant = answerVariants.stream()
                .filter(av -> av.getId() == null)
                .peek(av -> {
                    AnswerVariantDTO answerVariantDTO = answerVariantDTOMap.get(null);
                    AnswerVariant.builder()
                            .data(answerVariantDTO.getAnswer())
                            .question(question);
                }).collect(Collectors.toList());
        answerVariants.addAll(newAnswerVariant);
        return newAnswerVariant;
    }
}
