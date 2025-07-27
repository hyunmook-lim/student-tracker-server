package com.visit.studentTracker.dto.lecture.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureResponse {
    private Long uid;
    private String lectureName;
    private String description;
    private Long classroomId;
    private String className;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}