package com.visit.studentTracker.dto.question.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuestionRequest {
    private Integer number;
    private String mainTopic;
    private String subTopic;
    private String description;
    private String difficulty;
    private Integer score;
}