package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.studentClassroom.response.StudentClassroomResponse;
import com.visit.studentTracker.entity.StudentClassroomStatus;
import com.visit.studentTracker.service.StudentClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-classrooms")
public class StudentClassroomController {

    private final StudentClassroomService studentClassroomService;

    public StudentClassroomController(StudentClassroomService studentClassroomService) {
        this.studentClassroomService = studentClassroomService;
    }

    // 학생을 반에 추가 (기본적으로 WAITING 상태)
    @PostMapping("/{studentId}/classrooms/{classroomId}")
    public ResponseEntity<StudentClassroomResponse> addStudentToClassroom(
            @PathVariable Long studentId,
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(studentClassroomService.addStudentToClassroom(studentId, classroomId));
    }

    // 학생의 반 신청 상태 업데이트
    @PatchMapping("/{studentId}/classrooms/{classroomId}/status")
    public ResponseEntity<StudentClassroomResponse> updateStudentClassroomStatus(
            @PathVariable Long studentId,
            @PathVariable Long classroomId,
            @RequestParam StudentClassroomStatus status) {
        return ResponseEntity.ok(studentClassroomService.updateStudentClassroomStatus(studentId, classroomId, status));
    }

    // 특정 상태의 학생-반 관계 조회
    @GetMapping("/classrooms/{classroomId}/students/status/{status}")
    public ResponseEntity<List<StudentClassroomResponse>> getStudentClassroomsByStatus(
            @PathVariable Long classroomId,
            @PathVariable StudentClassroomStatus status) {
        return ResponseEntity.ok(studentClassroomService.getStudentClassroomsByStatus(classroomId, status));
    }

    // 학생을 반에서 제거
    @DeleteMapping("/{studentId}/classrooms/{classroomId}")
    public ResponseEntity<Void> removeStudentFromClassroom(
            @PathVariable Long studentId,
            @PathVariable Long classroomId) {
        studentClassroomService.removeStudentFromClassroom(studentId, classroomId);
        return ResponseEntity.noContent().build();
    }

    // 학생의 반 목록 조회
    @GetMapping("/students/{studentId}/classrooms")
    public ResponseEntity<List<StudentClassroomResponse>> getClassroomsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentClassroomService.getClassroomsByStudent(studentId));
    }

    // 반의 학생 목록 조회
    @GetMapping("/classrooms/{classroomId}/students")
    public ResponseEntity<List<StudentClassroomResponse>> getStudentsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(studentClassroomService.getStudentsByClassroom(classroomId));
    }
}