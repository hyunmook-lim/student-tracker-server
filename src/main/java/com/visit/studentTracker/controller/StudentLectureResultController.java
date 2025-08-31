package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.studentLectureResult.request.CreateStudentLectureResultRequest;
import com.visit.studentTracker.dto.studentLectureResult.request.UpdateStudentLectureResultRequest;
import com.visit.studentTracker.dto.studentLectureResult.response.StudentLectureResultResponse;
import com.visit.studentTracker.service.StudentLectureResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-lecture-results")
public class StudentLectureResultController {

    private final StudentLectureResultService studentLectureResultService;

    public StudentLectureResultController(StudentLectureResultService studentLectureResultService) {
        this.studentLectureResultService = studentLectureResultService;
    }

    @PostMapping
    public ResponseEntity<StudentLectureResultResponse> createStudentLectureResult(
            @RequestBody CreateStudentLectureResultRequest request) {
        try {
            StudentLectureResultResponse response = studentLectureResultService.createStudentLectureResult(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentLectureResultResponse> getStudentLectureResult(@PathVariable Long id) {
        try {
            StudentLectureResultResponse response = studentLectureResultService.getStudentLectureResult(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<StudentLectureResultResponse>> getAllStudentLectureResults() {
        List<StudentLectureResultResponse> responses = studentLectureResultService.getAllStudentLectureResults();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<StudentLectureResultResponse>> getResultsByStudent(@PathVariable Long studentId) {
        List<StudentLectureResultResponse> responses = studentLectureResultService.getResultsByStudent(studentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/lectures/{lectureId}")
    public ResponseEntity<List<StudentLectureResultResponse>> getResultsByLecture(@PathVariable Long lectureId) {
        List<StudentLectureResultResponse> responses = studentLectureResultService.getResultsByLecture(lectureId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StudentLectureResultResponse> updateStudentLectureResult(
            @PathVariable Long id,
            @RequestBody UpdateStudentLectureResultRequest request) {
        try {
            StudentLectureResultResponse response = studentLectureResultService.updateStudentLectureResult(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentLectureResult(@PathVariable Long id) {
        try {
            studentLectureResultService.deleteStudentLectureResult(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/students/{studentId}/lectures/{lectureId}")
    public ResponseEntity<StudentLectureResultResponse> getResultByStudentAndLecture(
            @PathVariable Long studentId,
            @PathVariable Long lectureId) {
        try {
            StudentLectureResultResponse response = studentLectureResultService.getResultByStudentAndLecture(studentId,
                    lectureId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}