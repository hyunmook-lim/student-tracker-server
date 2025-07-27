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
    private String classroomName;
    private List<Long> teacherIds;
    private List<String> teacherNames;
    private List<Long> studentIds;
    private List<String> studentNames;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}