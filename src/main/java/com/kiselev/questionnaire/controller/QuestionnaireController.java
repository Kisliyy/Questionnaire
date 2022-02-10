package com.kiselev.questionnaire.controller;

import com.kiselev.questionnaire.dto.QuestionnaireDTO;
import com.kiselev.questionnaire.dto.creation.QuestionnaireCreationDTO;
import com.kiselev.questionnaire.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administrator")
@PreAuthorize("hasAuthority('admin')")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;

    /*
     * Создание нового опросника
     */
    @PostMapping("/new")
    public ResponseEntity<QuestionnaireDTO> create(@RequestBody QuestionnaireCreationDTO questionnaireDTO) {
        QuestionnaireDTO response = questionnaireService.create(questionnaireDTO);
        return ResponseEntity.ok(response);
    }

    /*
     * Изменение опросника
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long questionnaireId,
                                    @RequestBody QuestionnaireDTO updateQuestionnaireDTO) {
        QuestionnaireDTO response = questionnaireService.update(questionnaireId, updateQuestionnaireDTO);
        return ResponseEntity.ok(response);
    }

    /*
     * Удаление опросника
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long questionnaireId) {
        questionnaireService.delete(questionnaireId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
