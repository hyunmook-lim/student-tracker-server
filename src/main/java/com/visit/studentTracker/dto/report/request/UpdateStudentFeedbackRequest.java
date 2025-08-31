package com.visit.studentTracker.dto.report.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentFeedbackRequest {
    private String feedback;
}