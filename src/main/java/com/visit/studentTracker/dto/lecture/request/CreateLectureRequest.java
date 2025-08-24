package com.visit.studentTracker.dto.lecture.request;

import com.visit.studentTracker.dto.question.request.CreateQuestionRequest;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLectureRequest {
    private String lectureName;
    private String description;
    private LocalDateTime lectureDate;
    private Long classroomId;
    private List<CreateQuestionRequest> questions;
}