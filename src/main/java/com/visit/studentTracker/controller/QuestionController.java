package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.question.request.CreateQuestionRequest;
import com.visit.studentTracker.dto.question.request.UpdateQuestionRequest;
import com.visit.studentTracker.dto.question.response.QuestionResponse;
import com.visit.studentTracker.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody CreateQuestionRequest dto) {
        return ResponseEntity.ok(questionService.createQuestion(dto));
    }

    // READ (단건)
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestion(id));
    }

    // READ (전체)
    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    // READ (단원별 문제 목록)
    @GetMapping("/unit/{unit}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByUnit(@PathVariable String unit) {
        return ResponseEntity.ok(questionService.getQuestionsByUnit(unit));
    }

    // READ (난이도별 문제 목록)
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(questionService.getQuestionsByDifficulty(difficulty));
    }

    // READ (단원 및 난이도별 문제 목록)
    @GetMapping("/unit/{unit}/difficulty/{difficulty}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByUnitAndDifficulty(
            @PathVariable String unit,
            @PathVariable String difficulty) {
        return ResponseEntity.ok(questionService.getQuestionsByUnitAndDifficulty(unit, difficulty));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @RequestBody UpdateQuestionRequest dto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}