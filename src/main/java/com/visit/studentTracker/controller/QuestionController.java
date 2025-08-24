package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.question.request.CreateQuestionRequest;
import com.visit.studentTracker.dto.question.request.CreateQuestionsRequest;
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

    // CREATE BULK
    @PostMapping("/bulk")
    public ResponseEntity<List<QuestionResponse>> createQuestions(@RequestBody CreateQuestionsRequest dto) {
        return ResponseEntity.ok(questionService.createQuestions(dto));
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

    // READ (주제별 문제 목록)
    @GetMapping("/main-topic/{mainTopic}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByMainTopic(@PathVariable String mainTopic) {
        return ResponseEntity.ok(questionService.getQuestionsByMainTopic(mainTopic));
    }

    // READ (하위 주제별 문제 목록)
    @GetMapping("/sub-topic/{subTopic}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsBySubTopic(@PathVariable String subTopic) {
        return ResponseEntity.ok(questionService.getQuestionsBySubTopic(subTopic));
    }

    // READ (난이도별 문제 목록)
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(questionService.getQuestionsByDifficulty(difficulty));
    }

    // READ (주제 및 하위 주제별 문제 목록)
    @GetMapping("/main-topic/{mainTopic}/sub-topic/{subTopic}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByMainTopicAndSubTopic(
            @PathVariable String mainTopic,
            @PathVariable String subTopic) {
        return ResponseEntity.ok(questionService.getQuestionsByMainTopicAndSubTopic(mainTopic, subTopic));
    }

    // READ (주제, 하위 주제 및 난이도별 문제 목록)
    @GetMapping("/main-topic/{mainTopic}/sub-topic/{subTopic}/difficulty/{difficulty}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByMainTopicAndSubTopicAndDifficulty(
            @PathVariable String mainTopic,
            @PathVariable String subTopic,
            @PathVariable String difficulty) {
        return ResponseEntity
                .ok(questionService.getQuestionsByMainTopicAndSubTopicAndDifficulty(mainTopic, subTopic, difficulty));
    }

    // UPDATE
    @PatchMapping("/{id}")
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