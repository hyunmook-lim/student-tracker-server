package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.classroom.request.CreateClassroomRequest;
import com.visit.studentTracker.dto.classroom.request.UpdateClassroomRequest;
import com.visit.studentTracker.dto.classroom.response.ClassroomResponse;
import com.visit.studentTracker.service.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ClassroomResponse> createClassroom(@RequestBody CreateClassroomRequest dto) {
        return ResponseEntity.ok(classroomService.createClassroom(dto));
    }

    // READ (단건)
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getClassroom(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getClassroom(id));
    }

    // READ (전체)
    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<ClassroomResponse> updateClassroom(
            @PathVariable Long id,
            @RequestBody UpdateClassroomRequest dto) {
        return ResponseEntity.ok(classroomService.updateClassroom(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }
}