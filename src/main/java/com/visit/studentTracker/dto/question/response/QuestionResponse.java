package com.visit.studentTracker.dto.question.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {
    private Long uid;
    private Integer number;
    private String mainTopic;
    private String subTopic;
    private String answer;
    private String difficulty;
    private Integer score;
    private Long lectureId;
    private String lectureName;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}