package com.visit.studentTracker.dto.classroom.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomResponse {
    private Long uid;
    private String className;
    private Long teacherId;
    private String teacherName;
    private List<Long> studentIds;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}