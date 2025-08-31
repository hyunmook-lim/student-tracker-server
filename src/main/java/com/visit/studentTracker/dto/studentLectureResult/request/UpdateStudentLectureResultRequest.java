package com.visit.studentTracker.dto.studentLectureResult.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentLectureResultRequest {
    private Boolean isAttended;
    private String assignmentScore;
    private List<String> homework;
    private List<QuestionResultDto> questionResults;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResultDto {
        private Long questionId;
        private Boolean isCorrect;
        private String studentAnswer;
    }
}