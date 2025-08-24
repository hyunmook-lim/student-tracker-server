package com.visit.studentTracker.dto.question.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionRequest {
    private Integer number;
    private String mainTopic;
    private String subTopic;
    private String answer;
    private String difficulty;
    private Integer score;
}