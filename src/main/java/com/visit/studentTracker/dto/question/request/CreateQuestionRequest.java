package com.visit.studentTracker.dto.question.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionRequest {
    private Integer number;
    private String description;
    private String unit;
    private List<String> types;
    private String difficulty;
    private Integer score;
}