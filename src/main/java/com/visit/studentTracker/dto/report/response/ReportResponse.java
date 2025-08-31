package com.visit.studentTracker.dto.report.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {
    private Long uid;
    private String reportTitle;
    private String reportDescription;
    private Long classroomId;
    private String classroomName;
    private List<LectureInfo> lectures;
    private List<StudentInfo> students;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LectureInfo {
        private Long lectureId;
        private String lectureName;
        private LocalDateTime lectureDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentInfo {
        private Long studentId;
        private String studentName;
        private String feedback;
    }
}