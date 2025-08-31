package com.visit.studentTracker.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomStudentAnalyticsResponse {
    private Long studentId;
    private String studentName;
    private Long classroomId;
    private String classroomName;

    // 총 문제수
    private Long totalQuestions;

    // 학생의 오답수
    private Long totalWrongAnswers;

    // 오답률
    private Double wrongAnswerRate;

    // 오답률 top3의 대단원
    private List<MainTopicStatistics> top3MainTopics;

    // 대단원별 난이도별 오답률
    private List<MainTopicDifficultyStatistics> mainTopicDifficultyStatistics;

    // 과제 점수 통계
    private AssignmentScoreStatistics assignmentScoreStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainTopicStatistics {
        private String mainTopic;
        private Long wrongAnswerCount;
        private Long totalQuestionCount;
        private Double wrongAnswerRate;
        private List<SubTopicStatistics> top3SubTopics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTopicStatistics {
        private String subTopic;
        private Long wrongAnswerCount;
        private Long totalQuestionCount;
        private Double wrongAnswerRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainTopicDifficultyStatistics {
        private String mainTopic;
        private List<DifficultyStatistics> difficultyStatistics;
        private List<SubTopicStatistics> subTopicStatistics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyStatistics {
        private String difficulty;
        private Long totalQuestionCount;
        private Long wrongAnswerCount;
        private Double wrongAnswerRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentScoreStatistics {
        private Long totalAssignments;
        private Long aCount;
        private Long bCount;
        private Long cCount;
        private Long dCount;
        private Double aRate;
        private Double bRate;
        private Double cRate;
        private Double dRate;
    }
}
