package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.studentReport.response.StudentReportResponse;
import com.visit.studentTracker.entity.*;
import com.visit.studentTracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentReportService {

    private final StudentReportRepository studentReportRepository;
    private final ReportLectureRepository reportLectureRepository;
    private final StudentLectureResultRepository studentLectureResultRepository;
    private final StudentQuestionResultRepository studentQuestionResultRepository;

    public StudentReportService(StudentReportRepository studentReportRepository,
                               ReportLectureRepository reportLectureRepository,
                               StudentLectureResultRepository studentLectureResultRepository,
                               StudentQuestionResultRepository studentQuestionResultRepository) {
        this.studentReportRepository = studentReportRepository;
        this.reportLectureRepository = reportLectureRepository;
        this.studentLectureResultRepository = studentLectureResultRepository;
        this.studentQuestionResultRepository = studentQuestionResultRepository;
    }

    @Transactional(readOnly = true)
    public StudentReportResponse getStudentReport(Long studentId, Long reportId) {
        StudentReport studentReport = studentReportRepository.findByStudentUidAndReportUid(studentId, reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생의 성적표를 찾을 수 없습니다."));

        Report report = studentReport.getReport();
        Student student = studentReport.getStudent();

        // 성적표에 포함된 강의들 가져오기
        List<ReportLecture> reportLectures = reportLectureRepository.findByReportUidAndIsActive(reportId, true);

        List<StudentReportResponse.LectureResult> lectureResults = reportLectures.stream()
                .map(reportLecture -> {
                    Lecture lecture = reportLecture.getLecture();
                    
                    // 해당 학생의 강의 결과 찾기
                    StudentLectureResult studentLectureResult = studentLectureResultRepository
                            .findByStudentUidAndLectureUid(studentId, lecture.getUid())
                            .orElse(null);

                    if (studentLectureResult == null) {
                        // 결과가 없는 경우
                        return StudentReportResponse.LectureResult.builder()
                                .lectureId(lecture.getUid())
                                .lectureName(lecture.getLectureName())
                                .lectureDate(lecture.getLectureDate())
                                .isAttended(false)
                                .assignmentScore("N/A")
                                .studentScore(0.0)
                                .totalScore(0.0)
                                .classAverage(0.0)
                                .classRank(null)
                                .questionResults(List.of())
                                .build();
                    }

                    // 문제별 결과 가져오기
                    List<StudentQuestionResult> questionResults = studentQuestionResultRepository
                            .findByStudentLectureResult(studentLectureResult);

                    List<StudentReportResponse.QuestionResult> questionResultDtos = questionResults.stream()
                            .map(qr -> StudentReportResponse.QuestionResult.builder()
                                    .questionId(qr.getQuestion().getUid())
                                    .questionNumber(qr.getQuestion().getNumber())
                                    .mainTopic(qr.getQuestion().getMainTopic())
                                    .subTopic(qr.getQuestion().getSubTopic())
                                    .difficulty(qr.getQuestion().getDifficulty())
                                    .correctAnswer(qr.getQuestion().getAnswer())
                                    .studentAnswer(qr.getStudentAnswer())
                                    .isCorrect(qr.isCorrect())
                                    .score(qr.getQuestion().getScore())
                                    .build())
                            .collect(Collectors.toList());

                    // 점수 계산
                    Double studentScore = calculateStudentScore(studentLectureResult);
                    Double totalScore = calculateTotalScore(lecture.getUid());
                    Double classAverage = calculateClassAverage(lecture.getUid());
                    Integer classRank = calculateStudentRank(studentLectureResult);

                    return StudentReportResponse.LectureResult.builder()
                            .lectureId(lecture.getUid())
                            .lectureName(lecture.getLectureName())
                            .lectureDate(lecture.getLectureDate())
                            .isAttended(studentLectureResult.isAttended())
                            .assignmentScore(studentLectureResult.getAssignmentScore())
                            .studentScore(studentScore)
                            .totalScore(totalScore)
                            .classAverage(classAverage)
                            .classRank(classRank)
                            .questionResults(questionResultDtos)
                            .build();
                })
                .collect(Collectors.toList());

        // 전체 점수 계산
        Double overallScore = lectureResults.stream()
                .mapToDouble(lr -> lr.getStudentScore() != null ? lr.getStudentScore() : 0.0)
                .sum();

        Double overallTotalScore = lectureResults.stream()
                .mapToDouble(lr -> lr.getTotalScore() != null ? lr.getTotalScore() : 0.0)
                .sum();

        Double overallAverage = overallTotalScore > 0 ? (overallScore / overallTotalScore) * 100 : 0.0;

        // 전체 등수 계산 (해당 성적표에 포함된 모든 학생들과 비교)
        Integer overallRank = calculateOverallRank(studentId, reportId, overallScore);

        return StudentReportResponse.builder()
                .studentReportId(studentReport.getUid())
                .studentId(student.getUid())
                .studentName(student.getName())
                .reportId(report.getUid())
                .reportTitle(report.getReportTitle())
                .reportDescription(report.getReportDescription())
                .feedback(studentReport.getFeedback())
                .classroomName(report.getClassroom().getClassroomName())
                .lectureResults(lectureResults)
                .overallScore(overallScore)
                .overallTotalScore(overallTotalScore)
                .overallAverage(overallAverage)
                .overallRank(overallRank)
                .createdAt(studentReport.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<StudentReportResponse> getStudentReports(Long studentId) {
        return studentReportRepository.findByStudentUidAndIsActive(studentId, true)
                .stream()
                .map(studentReport -> getStudentReport(studentId, studentReport.getReport().getUid()))
                .collect(Collectors.toList());
    }

    private Double calculateStudentScore(StudentLectureResult studentLectureResult) {
        return studentQuestionResultRepository.findByStudentLectureResult(studentLectureResult)
                .stream()
                .mapToDouble(qr -> qr.isCorrect() ? qr.getQuestion().getScore() : 0)
                .sum();
    }

    private Double calculateTotalScore(Long lectureId) {
        return studentQuestionResultRepository.findByStudentLectureResultLectureUid(lectureId)
                .stream()
                .mapToDouble(qr -> qr.getQuestion().getScore())
                .distinct()
                .sum();
    }

    private Double calculateClassAverage(Long lectureId) {
        List<StudentLectureResult> allResults = studentLectureResultRepository.findByLectureUid(lectureId);
        
        List<StudentLectureResult> attendedResults = allResults.stream()
                .filter(StudentLectureResult::isAttended)
                .collect(Collectors.toList());
        
        if (attendedResults.isEmpty()) {
            return 0.0;
        }

        double totalScore = attendedResults.stream()
                .mapToDouble(this::calculateStudentScore)
                .sum();

        return totalScore / attendedResults.size();
    }

    private Integer calculateStudentRank(StudentLectureResult studentResult) {
        List<StudentLectureResult> allResults = studentLectureResultRepository
                .findByLectureUid(studentResult.getLecture().getUid());
        
        List<StudentLectureResult> attendedResults = allResults.stream()
                .filter(StudentLectureResult::isAttended)
                .collect(Collectors.toList());
        
        if (!studentResult.isAttended()) {
            return null;
        }
        
        double studentTotalScore = calculateStudentScore(studentResult);
        
        int rank = (int) attendedResults.stream()
                .mapToDouble(this::calculateStudentScore)
                .map(score -> score > studentTotalScore ? 1 : 0)
                .sum() + 1;

        return rank;
    }

    private Integer calculateOverallRank(Long studentId, Long reportId, Double studentOverallScore) {
        List<StudentReport> allStudentReports = studentReportRepository.findByReportUidAndIsActive(reportId, true);
        
        List<Double> allScores = allStudentReports.stream()
                .map(sr -> {
                    List<ReportLecture> reportLectures = reportLectureRepository.findByReportUidAndIsActive(reportId, true);
                    return reportLectures.stream()
                            .mapToDouble(rl -> {
                                return studentLectureResultRepository
                                        .findByStudentUidAndLectureUid(sr.getStudent().getUid(), rl.getLecture().getUid())
                                        .map(this::calculateStudentScore)
                                        .orElse(0.0);
                            })
                            .sum();
                })
                .collect(Collectors.toList());

        long higherScores = allScores.stream()
                .mapToLong(score -> score > studentOverallScore ? 1 : 0)
                .sum();

        return (int) higherScores + 1;
    }
}