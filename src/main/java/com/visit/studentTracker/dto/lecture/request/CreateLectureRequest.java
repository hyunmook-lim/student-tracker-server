package com.visit.studentTracker.dto.lecture.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLectureRequest {
    private String lectureName;
    private String description;
    private Long classroomId;
}