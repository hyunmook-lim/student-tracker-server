package com.visit.studentTracker.dto.teacher.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTeacherRequest {
    private String name;
    private String email;
    private String phone;
}