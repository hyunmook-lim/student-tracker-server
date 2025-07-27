package com.visit.studentTracker.dto.classroom.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClassroomRequest {
    private String classroomName;
    private Long teacherId;
    private String description;
}