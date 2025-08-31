package com.visit.studentTracker.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnalyticsResponse {
    private Long studentId;
    private String studentName;

    // 학생이 듣고 있는 수업 수
    private Long totalClassrooms;

    // 학생이 수강한 모든 회차가 갖고 있는 문제 수
    private Long totalQuestions;

    // 학생의 오답수
    private Long totalWrongAnswers;

    // 오답률
    private Double wrongAnswerRate;

    // 대단원별 오답 통계
    private List<TopicStatistics> wrongAnswersByMainTopic;

    // 소단원별 오답 통계
    private List<TopicStatistics> wrongAnswersBySubTopic;

    // 난이도별 오답 통계
    private List<DifficultyStatistics> wrongAnswersByDifficulty;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicStatistics {
        private String topic;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyStatistics {
        private String difficulty;
        private Long count;
        private Double percentage;
    }
}
