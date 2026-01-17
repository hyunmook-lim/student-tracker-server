package com.visit.studentTracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visit.studentTracker.dto.question.request.CreateQuestionRequest;
import com.visit.studentTracker.dto.question.request.CreateQuestionsRequest;
import com.visit.studentTracker.dto.question.request.UpdateQuestionRequest;
import com.visit.studentTracker.dto.question.response.QuestionResponse;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.Question;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.QuestionRepository;

@Service
@SuppressWarnings("null")
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;

    public QuestionService(QuestionRepository questionRepository, LectureRepository lectureRepository) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
    }

    // CREATE
    @Transactional
    public QuestionResponse createQuestion(CreateQuestionRequest dto) {
        if (questionRepository.existsByNumber(dto.getNumber())) {
            throw new IllegalArgumentException("이미 존재하는 문제 번호입니다.");
        }

        Lecture lecture = lectureRepository.findById(dto.getLectureId())
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        Question question = Question.builder()
                .number(dto.getNumber())
                .mainTopic(dto.getMainTopic())
                .subTopic(dto.getSubTopic())
                .answer(dto.getAnswer())
                .difficulty(dto.getDifficulty())
                .score(dto.getScore())
                .lecture(lecture)
                .isActive(true)
                .build();

        return toResponse(questionRepository.save(question));
    }

    // CREATE BULK
    @Transactional
    public List<QuestionResponse> createQuestions(CreateQuestionsRequest dto) {
        List<Integer> requestNumbers = dto.getQuestions().stream()
                .map(CreateQuestionRequest::getNumber)
                .collect(Collectors.toList());
        
        List<Integer> existingNumbers = questionRepository.findNumbersByNumbers(requestNumbers);
        if (!existingNumbers.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 문제 번호가 있습니다: " + existingNumbers);
        }

        List<Question> questions = dto.getQuestions().stream()
                .map(questionDto -> {
                    Lecture lecture = lectureRepository.findById(questionDto.getLectureId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));
                    
                    return Question.builder()
                            .number(questionDto.getNumber())
                            .mainTopic(questionDto.getMainTopic())
                            .subTopic(questionDto.getSubTopic())
                            .answer(questionDto.getAnswer())
                            .difficulty(questionDto.getDifficulty())
                            .score(questionDto.getScore())
                            .lecture(lecture)
                            .isActive(true)
                            .build();
                })
                .collect(Collectors.toList());

        return questionRepository.saveAll(questions).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

    // READ (주제별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByMainTopic(String mainTopic) {
        return questionRepository.findByMainTopic(mainTopic)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (하위 주제별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsBySubTopic(String subTopic) {
        return questionRepository.findBySubTopic(subTopic)
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

    // READ (주제 및 하위 주제별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByMainTopicAndSubTopic(String mainTopic, String subTopic) {
        return questionRepository.findByMainTopicAndSubTopic(mainTopic, subTopic)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (주제, 하위 주제 및 난이도별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByMainTopicAndSubTopicAndDifficulty(String mainTopic, String subTopic,
            String difficulty) {
        return questionRepository.findByMainTopicAndSubTopicAndDifficulty(mainTopic, subTopic, difficulty)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (강의별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByLecture(Long lectureId) {
        return questionRepository.findByLectureUid(lectureId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (강의별 난이도별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByLectureAndDifficulty(Long lectureId, String difficulty) {
        return questionRepository.findByLectureUidAndDifficulty(lectureId, difficulty)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (강의별 주제별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByLectureAndMainTopic(Long lectureId, String mainTopic) {
        return questionRepository.findByLectureUidAndMainTopic(lectureId, mainTopic)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (강의별 하위 주제별 문제 목록)
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByLectureAndSubTopic(Long lectureId, String subTopic) {
        return questionRepository.findByLectureUidAndSubTopic(lectureId, subTopic)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public QuestionResponse updateQuestion(Long id, UpdateQuestionRequest dto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제를 찾을 수 없습니다."));

        if (dto.getNumber() != null && !dto.getNumber().equals(question.getNumber())) {
            if (questionRepository.existsByNumber(dto.getNumber())) {
                throw new IllegalArgumentException("이미 존재하는 문제 번호입니다.");
            }
            question.setNumber(dto.getNumber());
        }

        if (dto.getMainTopic() != null) {
            question.setMainTopic(dto.getMainTopic());
        }

        if (dto.getSubTopic() != null) {
            question.setSubTopic(dto.getSubTopic());
        }

        if (dto.getAnswer() != null) {
            question.setAnswer(dto.getAnswer());
        }

        if (dto.getDifficulty() != null) {
            question.setDifficulty(dto.getDifficulty());
        }

        if (dto.getScore() != null) {
            question.setScore(dto.getScore());
        }

        if (dto.getLectureId() != null) {
            Lecture lecture = lectureRepository.findById(dto.getLectureId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));
            question.setLecture(lecture);
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
                .mainTopic(question.getMainTopic())
                .subTopic(question.getSubTopic())
                .answer(question.getAnswer())
                .difficulty(question.getDifficulty())
                .score(question.getScore())
                .lectureId(question.getLecture().getUid())
                .lectureName(question.getLecture().getLectureName())
                .isActive(question.isActive())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}