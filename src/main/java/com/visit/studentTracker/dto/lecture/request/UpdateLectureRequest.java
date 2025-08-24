package com.visit.studentTracker.dto.lecture.request;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLectureRequest {
    private String lectureName;
    private String description;
    private LocalDateTime lectureDate;
}