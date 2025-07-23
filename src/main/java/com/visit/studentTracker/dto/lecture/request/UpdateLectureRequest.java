package com.visit.studentTracker.dto.lecture.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLectureRequest {
    private String lectureName;
    private String description;
}