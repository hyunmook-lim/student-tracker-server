package com.visit.studentTracker.dto.question.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionsRequest {
    private List<CreateQuestionRequest> questions;
}