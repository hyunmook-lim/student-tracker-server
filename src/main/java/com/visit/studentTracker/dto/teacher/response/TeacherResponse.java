package com.visit.studentTracker.dto.teacher.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherResponse {
    private Long uid;
    private String loginId;
    private String name;
    private String email;
    private String phone;
}