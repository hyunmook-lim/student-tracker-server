package com.visit.studentTracker.controller;

import com.visit.studentTracker.entity.TeacherClassroom;
import com.visit.studentTracker.service.TeacherClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-classrooms")
public class TeacherClassroomController {

    private final TeacherClassroomService teacherClassroomService;

    public TeacherClassroomController(TeacherClassroomService teacherClassroomService) {
        this.teacherClassroomService = teacherClassroomService;
    }

    // 선생님을 반에 추가
    @PostMapping("/{teacherId}/classrooms/{classroomId}")
    public ResponseEntity<TeacherClassroom> addTeacherToClassroom(
            @PathVariable Long teacherId,
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(teacherClassroomService.addTeacherToClassroom(teacherId, classroomId));
    }

    // 선생님을 반에서 제거
    @DeleteMapping("/{teacherId}/classrooms/{classroomId}")
    public ResponseEntity<Void> removeTeacherFromClassroom(
            @PathVariable Long teacherId,
            @PathVariable Long classroomId) {
        teacherClassroomService.removeTeacherFromClassroom(teacherId, classroomId);
        return ResponseEntity.noContent().build();
    }

    // 선생님의 반 목록 조회
    @GetMapping("/teachers/{teacherId}/classrooms")
    public ResponseEntity<List<TeacherClassroom>> getClassroomsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherClassroomService.getClassroomsByTeacher(teacherId));
    }

    // 반의 선생님 목록 조회
    @GetMapping("/classrooms/{classroomId}/teachers")
    public ResponseEntity<List<TeacherClassroom>> getTeachersByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(teacherClassroomService.getTeachersByClassroom(classroomId));
    }
}