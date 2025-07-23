package com.visit.studentTracker.dto.student.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentRequest {
    private String name;
    private Long classroomId;
}