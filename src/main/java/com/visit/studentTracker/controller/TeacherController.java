package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.teacher.request.CreateTeacherRequest;
import com.visit.studentTracker.dto.teacher.request.UpdateTeacherRequest;
import com.visit.studentTracker.dto.teacher.response.TeacherResponse;
import com.visit.studentTracker.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@RequestBody CreateTeacherRequest dto) {
        return ResponseEntity.ok(teacherService.createTeacher(dto));
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
    @PutMapping("/{id}")
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
}