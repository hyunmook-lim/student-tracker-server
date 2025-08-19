package com.visit.studentTracker.dto.teacher.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherLoginRequest {
    private String loginId;
    private String password;
}
