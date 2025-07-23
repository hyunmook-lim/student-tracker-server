package com.visit.studentTracker.dto.question.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {
    private Long uid;
    private Integer number;
    private String description;
    private String unit;
    private List<String> types;
    private String difficulty;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}