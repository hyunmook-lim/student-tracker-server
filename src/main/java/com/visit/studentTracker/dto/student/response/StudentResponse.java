package com.visit.studentTracker.dto.student.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private Long uid;
    private String loginId;
    private String name;
    private String phone;
    private List<Long> classroomIds;
    private List<String> classroomNames;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}