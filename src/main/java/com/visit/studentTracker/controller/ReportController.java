package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.report.request.CreateReportRequest;
import com.visit.studentTracker.dto.report.request.UpdateReportRequest;
import com.visit.studentTracker.dto.report.request.UpdateStudentFeedbackRequest;
import com.visit.studentTracker.dto.report.response.ReportResponse;
import com.visit.studentTracker.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 성적표 생성
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@RequestBody CreateReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }

    // 성적표 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    // 전체 성적표 목록 조회
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    // 특정 반의 성적표 목록 조회
    @GetMapping("/classrooms/{classroomId}")
    public ResponseEntity<List<ReportResponse>> getReportsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(reportService.getReportsByClassroom(classroomId));
    }

    // 성적표 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ReportResponse> updateReport(
            @PathVariable Long id, 
            @RequestBody UpdateReportRequest request) {
        return ResponseEntity.ok(reportService.updateReport(id, request));
    }

    // 성적표 삭제 (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }

    // 특정 학생의 성적표 목록 조회
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<ReportResponse>> getReportsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(reportService.getReportsByStudent(studentId));
    }

    // 학생별 피드백 수정
    @PatchMapping("/{reportId}/students/{studentId}/feedback")
    public ResponseEntity<Void> updateStudentFeedback(
            @PathVariable Long reportId, 
            @PathVariable Long studentId,
            @RequestBody UpdateStudentFeedbackRequest request) {
        reportService.updateStudentFeedback(reportId, studentId, request);
        return ResponseEntity.ok().build();
    }
}