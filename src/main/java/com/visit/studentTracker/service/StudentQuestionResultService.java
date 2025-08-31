package com.visit.studentTracker.service;

import com.visit.studentTracker.entity.StudentQuestionResult;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.Question;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.StudentLectureResult;
import com.visit.studentTracker.repository.StudentQuestionResultRepository;
import com.visit.studentTracker.repository.StudentRepository;
import com.visit.studentTracker.repository.QuestionRepository;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.StudentLectureResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class StudentQuestionResultService {

    private final StudentQuestionResultRepository studentQuestionResultRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final StudentLectureResultRepository studentLectureResultRepository;

    public StudentQuestionResultService(StudentQuestionResultRepository studentQuestionResultRepository,
            StudentRepository studentRepository,
            QuestionRepository questionRepository,
            LectureRepository lectureRepository,
            StudentLectureResultRepository studentLectureResultRepository) {
        this.studentQuestionResultRepository = studentQuestionResultRepository;
        this.studentRepository = studentRepository;
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.studentLectureResultRepository = studentLectureResultRepository;
    }

    // 학생 문제 결과 저장
    @Transactional
    public StudentQuestionResult saveQuestionResult(Long studentId, Long questionId,
            Long lectureId, boolean isCorrect, String studentAnswer) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제를 찾을 수 없습니다."));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        // StudentLectureResult 찾기 또는 생성
        StudentLectureResult studentLectureResult = studentLectureResultRepository
                .findByStudentUidAndLectureUid(studentId, lectureId)
                .orElseGet(() -> {
                    StudentLectureResult newResult = StudentLectureResult.builder()
                            .student(student)
                            .lecture(lecture)
                            .isAttended(true)
                            .assignmentScore("0")
                            .build();
                    return studentLectureResultRepository.save(newResult);
                });

        StudentQuestionResult result = StudentQuestionResult.builder()
                .studentLectureResult(studentLectureResult)
                .question(question)
                .studentAnswer(studentAnswer)
                .isCorrect(isCorrect)
                .build();

        return studentQuestionResultRepository.save(result);
    }

    // 학생 문제 결과 수정
    @Transactional
    public StudentQuestionResult updateQuestionResult(Long studentId, Long questionId, boolean isCorrect) {
        StudentQuestionResult result = studentQuestionResultRepository
                .findByStudentLectureResultStudentUidAndQuestionUid(studentId, questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생의 문제 결과를 찾을 수 없습니다."));

        result.setCorrect(isCorrect);
        return studentQuestionResultRepository.save(result);
    }

    // 학생의 문제 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByStudent(Long studentId) {
        return studentQuestionResultRepository.findByStudentLectureResultStudentUid(studentId);
    }

    // 학생의 특정 반 문제 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByStudentAndClassroom(Long studentId, Long classroomId) {
        return studentQuestionResultRepository.findByStudentLectureResultStudentUidAndStudentLectureResultLectureClassroomUid(studentId, classroomId);
    }

    // 학생의 특정 강의 문제 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByStudentAndLecture(Long studentId, Long lectureId) {
        return studentQuestionResultRepository.findByStudentLectureResultStudentUidAndStudentLectureResultLectureUid(studentId, lectureId);
    }

    // 문제별 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByQuestion(Long questionId) {
        return studentQuestionResultRepository.findByQuestionUid(questionId);
    }

    // 강의별 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByLecture(Long lectureId) {
        return studentQuestionResultRepository.findByStudentLectureResultLectureUid(lectureId);
    }

    // 반별 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByClassroom(Long classroomId) {
        return studentQuestionResultRepository.findByStudentLectureResultLectureClassroomUid(classroomId);
    }

    // 학생의 특정 강의 총점 계산
    @Transactional(readOnly = true)
    public Integer calculateStudentTotalScore(Long studentId, Long lectureId) {
        List<StudentQuestionResult> results = getResultsByStudentAndLecture(studentId, lectureId);
        return results.stream()
                .filter(StudentQuestionResult::isCorrect)
                .mapToInt(result -> result.getQuestion().getScore())
                .sum();
    }

    // 강의의 반평균 점수 계산
    @Transactional(readOnly = true)
    public Double calculateClassAverageScore(Long lectureId) {
        List<StudentQuestionResult> allResults = getResultsByLecture(lectureId);
        
        Map<Long, Integer> studentScores = allResults.stream()
                .collect(Collectors.groupingBy(
                    result -> result.getStudentLectureResult().getStudent().getUid(),
                    Collectors.summingInt(result -> result.isCorrect() ? result.getQuestion().getScore() : 0)
                ));

        return studentScores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    // 특정 강의에서 학생의 등수 계산
    @Transactional(readOnly = true)
    public Integer calculateStudentRank(Long studentId, Long lectureId) {
        List<StudentQuestionResult> allResults = getResultsByLecture(lectureId);
        
        Map<Long, Integer> studentScores = allResults.stream()
                .collect(Collectors.groupingBy(
                    result -> result.getStudentLectureResult().getStudent().getUid(),
                    Collectors.summingInt(result -> result.isCorrect() ? result.getQuestion().getScore() : 0)
                ));

        Integer targetScore = studentScores.get(studentId);
        if (targetScore == null) return null;

        List<Integer> sortedScores = studentScores.values().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        return sortedScores.indexOf(targetScore) + 1;
    }

    // 강의별 학생 점수 및 등수 리스트 조회
    @Transactional(readOnly = true)
    public List<StudentScoreInfo> getStudentScoresWithRanking(Long lectureId) {
        List<StudentQuestionResult> allResults = getResultsByLecture(lectureId);
        
        Map<Long, Integer> studentScores = allResults.stream()
                .collect(Collectors.groupingBy(
                    result -> result.getStudentLectureResult().getStudent().getUid(),
                    Collectors.summingInt(result -> result.isCorrect() ? result.getQuestion().getScore() : 0)
                ));

        Map<Long, String> studentNames = allResults.stream()
                .collect(Collectors.toMap(
                    result -> result.getStudentLectureResult().getStudent().getUid(),
                    result -> result.getStudentLectureResult().getStudent().getName(),
                    (existing, replacement) -> existing
                ));

        List<Integer> sortedScores = studentScores.values().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        return studentScores.entrySet().stream()
                .map(entry -> {
                    Long studentId = entry.getKey();
                    Integer score = entry.getValue();
                    Integer rank = sortedScores.indexOf(score) + 1;
                    String name = studentNames.get(studentId);
                    return new StudentScoreInfo(studentId, name, score, rank);
                })
                .sorted(Comparator.comparing(StudentScoreInfo::getRank))
                .collect(Collectors.toList());
    }

    // 학생 점수 정보를 담는 내부 클래스
    public static class StudentScoreInfo {
        private Long studentId;
        private String studentName;
        private Integer totalScore;
        private Integer rank;

        public StudentScoreInfo(Long studentId, String studentName, Integer totalScore, Integer rank) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.totalScore = totalScore;
            this.rank = rank;
        }

        // Getters
        public Long getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public Integer getTotalScore() { return totalScore; }
        public Integer getRank() { return rank; }
    }
}