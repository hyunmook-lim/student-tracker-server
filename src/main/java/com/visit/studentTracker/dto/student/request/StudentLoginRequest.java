package com.visit.studentTracker.dto.student.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLoginRequest {
    private String loginId;
    private String password;
}
