package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.teacher.request.CreateTeacherRequest;
import com.visit.studentTracker.dto.teacher.request.UpdateTeacherRequest;
import com.visit.studentTracker.dto.teacher.request.TeacherLoginRequest;
import com.visit.studentTracker.dto.classroom.request.CreateClassroomRequest;
import com.visit.studentTracker.dto.classroom.response.ClassroomResponse;
import com.visit.studentTracker.dto.teacher.response.TeacherResponse;
import com.visit.studentTracker.service.TeacherService;
import com.visit.studentTracker.service.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final ClassroomService classroomService;

    public TeacherController(TeacherService teacherService, ClassroomService classroomService) {
        this.teacherService = teacherService;
        this.classroomService = classroomService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@RequestBody CreateTeacherRequest dto) {
        return ResponseEntity.ok(teacherService.createTeacher(dto));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<TeacherResponse> loginTeacher(@RequestBody TeacherLoginRequest dto) {
        return ResponseEntity.ok(teacherService.loginTeacher(dto));
    }

    // READ (단건)
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacher(id));
    }

    // READ (전체)
    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable Long id,
            @RequestBody UpdateTeacherRequest dto) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // 선생님이 반 생성
    @PostMapping("/{teacherId}/classrooms")
    public ResponseEntity<ClassroomResponse> createClassroomByTeacher(
            @PathVariable Long teacherId,
            @RequestBody CreateClassroomRequest dto) {
        // teacherId를 DTO에 설정
        dto.setTeacherId(teacherId);
        return ResponseEntity.ok(classroomService.createClassroom(dto));
    }

    // 선생님의 반 목록 조회
    @GetMapping("/{teacherId}/classrooms")
    public ResponseEntity<List<ClassroomResponse>> getClassroomsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(classroomService.getClassroomsByTeacher(teacherId));
    }
}