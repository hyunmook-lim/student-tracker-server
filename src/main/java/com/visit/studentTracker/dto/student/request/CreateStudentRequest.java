package com.visit.studentTracker.dto.student.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentRequest {
    private String loginId;
    private String password;
    private String name;
    private Long classroomId;
}