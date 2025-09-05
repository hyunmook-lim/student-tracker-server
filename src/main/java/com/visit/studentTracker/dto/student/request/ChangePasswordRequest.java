package com.visit.studentTracker.dto.student.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private Long uid;
    private String loginId;
    private String password;
    private String newPassword;
}
