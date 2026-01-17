package com.visit.studentTracker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visit.studentTracker.dto.studentLectureResult.request.CreateStudentLectureResultRequest;
import com.visit.studentTracker.dto.studentLectureResult.request.UpdateStudentLectureResultRequest;
import com.visit.studentTracker.dto.studentLectureResult.response.StudentLectureResultResponse;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.Question;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.StudentLectureResult;
import com.visit.studentTracker.entity.StudentQuestionResult;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.QuestionRepository;
import com.visit.studentTracker.repository.StudentLectureResultRepository;
import com.visit.studentTracker.repository.StudentQuestionResultRepository;
import com.visit.studentTracker.repository.StudentRepository;

@Service
@SuppressWarnings("null")
public class StudentLectureResultService {

    private final StudentLectureResultRepository studentLectureResultRepository;
    private final StudentQuestionResultRepository studentQuestionResultRepository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final QuestionRepository questionRepository;

    public StudentLectureResultService(StudentLectureResultRepository studentLectureResultRepository,
            StudentQuestionResultRepository studentQuestionResultRepository,
            StudentRepository studentRepository,
            LectureRepository lectureRepository,
            QuestionRepository questionRepository) {
        this.studentLectureResultRepository = studentLectureResultRepository;
        this.studentQuestionResultRepository = studentQuestionResultRepository;
        this.studentRepository = studentRepository;
        this.lectureRepository = lectureRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public StudentLectureResultResponse createStudentLectureResult(CreateStudentLectureResultRequest dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        Lecture lecture = lectureRepository.findById(dto.getLectureId())
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        if (studentLectureResultRepository.findByStudentUidAndLectureUid(dto.getStudentId(), dto.getLectureId())
                .isPresent()) {
            throw new IllegalArgumentException("이미 해당 학생의 강의 결과가 존재합니다.");
        }

        StudentLectureResult studentLectureResult = StudentLectureResult.builder()
                .student(student)
                .lecture(lecture)
                .isAttended(dto.getIsAttended() != null ? dto.getIsAttended() : true)
                .assignmentScore(dto.getAssignmentScore() != null ? dto.getAssignmentScore() : "0")
                .homework(dto.getHomework() != null ? dto.getHomework() : new ArrayList<>())
                .build();

        StudentLectureResult savedResult = studentLectureResultRepository.save(studentLectureResult);

        if (dto.getQuestionResults() != null && !dto.getQuestionResults().isEmpty()) {
            List<StudentQuestionResult> questionResults = dto.getQuestionResults().stream()
                    .map(questionDto -> {
                        Question question = questionRepository.findById(questionDto.getQuestionId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "문제 ID " + questionDto.getQuestionId() + "를 찾을 수 없습니다."));

                        return StudentQuestionResult.builder()
                                .studentLectureResult(savedResult)
                                .question(question)
                                .studentAnswer(questionDto.getStudentAnswer())
                                .isCorrect(questionDto.getIsCorrect() != null ? questionDto.getIsCorrect() : false)
                                .build();
                    })
                    .collect(Collectors.toList());

            studentQuestionResultRepository.saveAll(questionResults);
        }

        return toResponse(savedResult);
    }

    @Transactional(readOnly = true)
    public StudentLectureResultResponse getStudentLectureResult(Long id) {
        return studentLectureResultRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생 강의 결과를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<StudentLectureResultResponse> getAllStudentLectureResults() {
        return studentLectureResultRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentLectureResultResponse> getResultsByStudent(Long studentId) {
        return studentLectureResultRepository.findByStudentUid(studentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentLectureResultResponse> getResultsByLecture(Long lectureId) {
        return studentLectureResultRepository.findByLectureUid(lectureId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentLectureResultResponse getResultByStudentAndLecture(Long studentId, Long lectureId) {
        return studentLectureResultRepository.findByStudentUidAndLectureUid(studentId, lectureId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생의 강의 결과를 찾을 수 없습니다."));
    }

    @Transactional
    public StudentLectureResultResponse updateStudentLectureResult(Long id, UpdateStudentLectureResultRequest dto) {
        StudentLectureResult result = studentLectureResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생 강의 결과를 찾을 수 없습니다."));

        if (dto.getIsAttended() != null) {
            result.setAttended(dto.getIsAttended());
        }

        if (dto.getAssignmentScore() != null) {
            result.setAssignmentScore(dto.getAssignmentScore());
        }

        if (dto.getHomework() != null) {
            result.setHomework(dto.getHomework());
        }

        result.setUpdatedAt(LocalDateTime.now());

        return toResponse(result);
    }

    @Transactional
    public void deleteStudentLectureResult(Long id) {
        if (!studentLectureResultRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 학생 강의 결과를 찾을 수 없습니다.");
        }
        studentLectureResultRepository.deleteById(id);
    }

    private StudentLectureResultResponse toResponse(StudentLectureResult result) {
        List<StudentLectureResultResponse.QuestionResultResponse> questionResponses = studentQuestionResultRepository
                .findByStudentLectureResult(result)
                .stream()
                .map(qr -> StudentLectureResultResponse.QuestionResultResponse.builder()
                        .uid(qr.getUid())
                        .questionId(qr.getQuestion().getUid())
                        .questionNumber(qr.getQuestion().getNumber())
                        .isCorrect(qr.isCorrect())
                        .studentAnswer(qr.getStudentAnswer())
                        .maxScore(qr.getQuestion().getScore())
                        .mainTopic(qr.getQuestion().getMainTopic())
                        .subTopic(qr.getQuestion().getSubTopic())
                        .difficulty(qr.getQuestion().getDifficulty())
                        .score(qr.getQuestion().getScore())
                        .build())
                .collect(Collectors.toList());

        // 해당 회차의 반 평균과 학생 등수 계산
        Double classAverage = calculateClassAverage(result.getLecture().getUid());
        Integer classRank = calculateStudentRank(result);

        // 학생 점수와 총점 계산
        Double studentScore = calculateStudentTotalScore(result);
        Double totalScore = calculateTotalPossibleScore(result.getLecture().getUid());

        return StudentLectureResultResponse.builder()
                .uid(result.getUid())
                .studentId(result.getStudent().getUid())
                .studentName(result.getStudent().getName())
                .lectureId(result.getLecture().getUid())
                .lectureName(result.getLecture().getLectureName())
                .isAttended(result.isAttended())
                .assignmentScore(result.getAssignmentScore())
                .homework(result.getHomework() != null ? result.getHomework() : new ArrayList<>())
                .questionResults(questionResponses)
                .classAverage(classAverage)
                .classRank(classRank)
                .studentScore(studentScore)
                .totalScore(totalScore)
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }

    private Double calculateClassAverage(Long lectureId) {
        List<StudentLectureResult> allResults = studentLectureResultRepository.findByLectureUid(lectureId);

        // 출석한 학생들만 필터링
        List<StudentLectureResult> attendedResults = allResults.stream()
                .filter(StudentLectureResult::isAttended)
                .collect(Collectors.toList());

        if (attendedResults.isEmpty()) {
            return 0.0;
        }

        double totalScore = attendedResults.stream()
                .mapToDouble(this::calculateStudentTotalScore)
                .sum();

        return totalScore / attendedResults.size();
    }

    private Integer calculateStudentRank(StudentLectureResult studentResult) {
        List<StudentLectureResult> allResults = studentLectureResultRepository
                .findByLectureUid(studentResult.getLecture().getUid());

        // 출석한 학생들만 필터링
        List<StudentLectureResult> attendedResults = allResults.stream()
                .filter(StudentLectureResult::isAttended)
                .collect(Collectors.toList());

        // 해당 학생이 결석했다면 등수 없음
        if (!studentResult.isAttended()) {
            return null;
        }

        double studentTotalScore = calculateStudentTotalScore(studentResult);

        // 출석한 학생들 중에서 해당 학생보다 높은 점수를 받은 학생 수 + 1 = 등수
        int rank = (int) attendedResults.stream()
                .mapToDouble(this::calculateStudentTotalScore)
                .map(score -> score > studentTotalScore ? 1 : 0)
                .sum() + 1;

        return rank;
    }

    private double calculateStudentTotalScore(StudentLectureResult result) {
        return studentQuestionResultRepository.findByStudentLectureResult(result)
                .stream()
                .mapToDouble(qr -> qr.isCorrect() ? qr.getQuestion().getScore() : 0)
                .sum();
    }

    private Double calculateTotalPossibleScore(Long lectureId) {
        return questionRepository.findByLectureUid(lectureId)
                .stream()
                .mapToDouble(Question::getScore)
                .sum();
    }
}