package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.question.request.CreateQuestionRequest;
import com.visit.studentTracker.dto.question.request.UpdateQuestionRequest;
import com.visit.studentTracker.dto.question.response.QuestionResponse;
import com.visit.studentTracker.entity.Question;
import com.visit.studentTracker.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // CREATE
    @Transactional
    public QuestionResponse createQuestion(CreateQuestionRequest dto) {
        if (questionRepository.existsByNumber(dto.getNumber())) {
            throw new IllegalArgumentException("이미 존재하는 문제 번호입니다.");
        }

        Question question = Question.builder()
                .number(dto.getNumber())
                .description(dto.getDescription())
                .unit(dto.getUnit())
                .types(dto.getTypes())
                .difficulty(dto.getDifficulty())
                .score(dto.getScore())
                .build();

        return toResponse(questionRepository.save(question));
    }

    // READ (단건)
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(Long id) {
        return questionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제를 찾을 수 없습니다."));
    }

    // READ (전체)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (단원별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByUnit(String unit) {
        return questionRepository.findByUnit(unit)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (난이도별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByDifficulty(String difficulty) {
        return questionRepository.findByDifficulty(difficulty)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (단원 및 난이도별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByUnitAndDifficulty(String unit, String difficulty) {
        return questionRepository.findByUnitAndDifficulty(unit, difficulty)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public QuestionResponse updateQuestion(Long id, UpdateQuestionRequest dto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제를 찾을 수 없습니다."));

        if (dto.getDescription() != null) {
            question.setDescription(dto.getDescription());
        }

        if (dto.getUnit() != null) {
            question.setUnit(dto.getUnit());
        }

        if (dto.getTypes() != null) {
            question.setTypes(dto.getTypes());
        }

        if (dto.getDifficulty() != null) {
            question.setDifficulty(dto.getDifficulty());
        }

        if (dto.getScore() != null) {
            question.setScore(dto.getScore());
        }

        question.setUpdatedAt(LocalDateTime.now());

        return toResponse(question);
    }

    // DELETE
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 문제를 찾을 수 없습니다.");
        }
        questionRepository.deleteById(id);
    }

    // DTO 변환 메서드
    private QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .uid(question.getUid())
                .number(question.getNumber())
                .description(question.getDescription())
                .unit(question.getUnit())
                .types(question.getTypes())
                .difficulty(question.getDifficulty())
                .score(question.getScore())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}