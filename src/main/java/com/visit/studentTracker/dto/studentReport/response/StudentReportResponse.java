package com.visit.studentTracker.dto.studentReport.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentReportResponse {
    private Long studentReportId;
    private Long studentId;
    private String studentName;
    private Long reportId;
    private String reportTitle;
    private String reportDescription;
    private String feedback;
    private String classroomName;
    private List<LectureResult> lectureResults;
    private Double overallScore;
    private Double overallTotalScore;
    private Double overallAverage;
    private Integer overallRank;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LectureResult {
        private Long lectureId;
        private String lectureName;
        private LocalDateTime lectureDate;
        private Boolean isAttended;
        private String assignmentScore;
        private Double studentScore;
        private Double totalScore;
        private Double classAverage;
        private Integer classRank;
        private List<QuestionResult> questionResults;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResult {
        private Long questionId;
        private Integer questionNumber;
        private String mainTopic;
        private String subTopic;
        private String difficulty;
        private String correctAnswer;
        private String studentAnswer;
        private Boolean isCorrect;
        private Integer score;
    }
}