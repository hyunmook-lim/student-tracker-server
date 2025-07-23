package com.visit.studentTracker.dto.classroom.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClassroomRequest {
    private String className;
    private Long teacherId;
    private String description;
}