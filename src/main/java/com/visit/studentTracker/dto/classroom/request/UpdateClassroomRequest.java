package com.visit.studentTracker.dto.classroom.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateClassroomRequest {
    private String className;
    private String description;
}