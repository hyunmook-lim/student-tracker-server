package com.visit.studentTracker.controller;

import com.visit.studentTracker.entity.StudentClassroom;
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

    // 학생을 반에 추가
    @PostMapping("/{studentId}/classrooms/{classroomId}")
    public ResponseEntity<StudentClassroom> addStudentToClassroom(
            @PathVariable Long studentId,
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(studentClassroomService.addStudentToClassroom(studentId, classroomId));
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
    public ResponseEntity<List<StudentClassroom>> getClassroomsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentClassroomService.getClassroomsByStudent(studentId));
    }

    // 반의 학생 목록 조회
    @GetMapping("/classrooms/{classroomId}/students")
    public ResponseEntity<List<StudentClassroom>> getStudentsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(studentClassroomService.getStudentsByClassroom(classroomId));
    }
}