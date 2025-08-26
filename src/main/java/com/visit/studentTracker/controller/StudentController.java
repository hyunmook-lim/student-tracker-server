package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.student.request.CreateStudentRequest;
import com.visit.studentTracker.dto.student.request.UpdateStudentRequest;
import com.visit.studentTracker.dto.student.request.StudentLoginRequest;
import com.visit.studentTracker.dto.student.response.StudentResponse;
import com.visit.studentTracker.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody CreateStudentRequest dto) {
        return ResponseEntity.ok(studentService.createStudent(dto));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<StudentResponse> loginStudent(@RequestBody StudentLoginRequest dto) {
        return ResponseEntity.ok(studentService.loginStudent(dto));
    }

    // READ (단건)
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    // READ (전체)
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @RequestBody UpdateStudentRequest dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}