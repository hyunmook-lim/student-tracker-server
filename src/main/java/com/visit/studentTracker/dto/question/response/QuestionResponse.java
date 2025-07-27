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
    private String description;
    private String difficulty;
    private Integer score;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}