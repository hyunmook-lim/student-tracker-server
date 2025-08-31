package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.studentReport.response.StudentReportResponse;
import com.visit.studentTracker.service.StudentReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-reports")
public class StudentReportController {

    private final StudentReportService studentReportService;

    public StudentReportController(StudentReportService studentReportService) {
        this.studentReportService = studentReportService;
    }

    // 특정 학생의 특정 성적표 상세 조회
    @GetMapping("/students/{studentId}/reports/{reportId}")
    public ResponseEntity<StudentReportResponse> getStudentReport(
            @PathVariable Long studentId, 
            @PathVariable Long reportId) {
        return ResponseEntity.ok(studentReportService.getStudentReport(studentId, reportId));
    }

    // 특정 학생의 모든 성적표 목록 조회
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<StudentReportResponse>> getStudentReports(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentReportService.getStudentReports(studentId));
    }
}