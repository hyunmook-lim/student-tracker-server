package com.visit.studentTracker.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {
    private Long studentId;
    private String studentName;

    // 현재 수강 중인 반 수
    private Long activeClassroomCount;

    // 전체 출석률
    private Double overallAttendanceRate;

    // 전체 평균 점수
    private Double overallAverageScore;

    // 과제 평균 완성도 (A-100점, B-75점, C-50점, D-25점 기준)
    private Double assignmentCompletionRate;
}
