package com.kiselev.questionnaire.controller;

import com.kiselev.questionnaire.dto.AnswerDTO;
import com.kiselev.questionnaire.dto.QuestionnaireDTO;
import com.kiselev.questionnaire.dto.QuestionnaireOfUserDTO;
import com.kiselev.questionnaire.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;


    /**
     * Метод плоучения всех опросов
     */
    @GetMapping
    public ResponseEntity<List<QuestionnaireDTO>> getAll() {
        final List<QuestionnaireDTO> all = questionnaireService.getCollect();
        if (all.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    /**
     * Отправить опросник с ответами
     */
    @PostMapping("/{id}")
    public ResponseEntity<?> saveQuestionnaireWithAnswer(@CookieValue("JSESSIONID") String userId,
                                                         @PathVariable(name = "id") Long questionnaireId,
                                                         @RequestBody AnswerDTO answer) {
        questionnaireService.saveAnswer(userId, questionnaireId, answer);
        return ResponseEntity.ok().build();
    }


    /**
     * Получить опросы с моими ответами
     */
    @GetMapping("/userQuestionnaire")
    public ResponseEntity<List<QuestionnaireOfUserDTO>> getQuestionnaireWithAnswer(@CookieValue("JSESSIONID") String userId) {
        List<QuestionnaireOfUserDTO> questionnaireDTO = questionnaireService.getQuestionnaireDTOWithAnswer(userId);
        return ResponseEntity.ok(questionnaireDTO);
    }
}
