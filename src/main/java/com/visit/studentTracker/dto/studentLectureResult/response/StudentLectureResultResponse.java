package com.visit.studentTracker.dto.studentLectureResult.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLectureResultResponse {
    private Long uid;
    private Long studentId;
    private String studentName;
    private Long lectureId;
    private String lectureName;
    private Boolean isAttended;
    private String assignmentScore;
    private List<String> homework;
    private List<QuestionResultResponse> questionResults;
    private Double classAverage;
    private Integer classRank;
    private Double studentScore; // 학생이 받은 점수
    private Double totalScore; // 총점
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResultResponse {
        private Long uid;
        private Long questionId;
        private Integer questionNumber;
        private Boolean isCorrect;
        private String studentAnswer;
        private Integer maxScore;
        private String mainTopic;
        private String subTopic;
        private String difficulty;
        private Integer score;
    }
}