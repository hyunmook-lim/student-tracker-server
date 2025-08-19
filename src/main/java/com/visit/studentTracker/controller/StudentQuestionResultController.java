package com.visit.studentTracker.controller;

import com.visit.studentTracker.entity.StudentQuestionResult;
import com.visit.studentTracker.service.StudentQuestionResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-question-results")
public class StudentQuestionResultController {

    private final StudentQuestionResultService studentQuestionResultService;

    public StudentQuestionResultController(StudentQuestionResultService studentQuestionResultService) {
        this.studentQuestionResultService = studentQuestionResultService;
    }

    // 학생 문제 결과 저장
    @PostMapping
    public ResponseEntity<StudentQuestionResult> saveQuestionResult(
            @RequestParam Long studentId,
            @RequestParam Long questionId,
            @RequestParam Long lectureId,
            @RequestParam Long classroomId,
            @RequestParam boolean isCorrect) {
        return ResponseEntity.ok(studentQuestionResultService.saveQuestionResult(
                studentId, questionId, lectureId, classroomId, isCorrect));
    }

    // 학생 문제 결과 수정
    @PatchMapping("/{studentId}/questions/{questionId}")
    public ResponseEntity<StudentQuestionResult> updateQuestionResult(
            @PathVariable Long studentId,
            @PathVariable Long questionId,
            @RequestParam boolean isCorrect) {
        return ResponseEntity.ok(studentQuestionResultService.updateQuestionResult(studentId, questionId, isCorrect));
    }

    // 학생의 문제 결과 목록 조회
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<StudentQuestionResult>> getResultsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentQuestionResultService.getResultsByStudent(studentId));
    }

    // 학생의 특정 반 문제 결과 목록 조회
    @GetMapping("/students/{studentId}/classrooms/{classroomId}")
    public ResponseEntity<List<StudentQuestionResult>> getResultsByStudentAndClassroom(
            @PathVariable Long studentId,
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(studentQuestionResultService.getResultsByStudentAndClassroom(studentId, classroomId));
    }

    // 학생의 특정 강의 문제 결과 목록 조회
    @GetMapping("/students/{studentId}/lectures/{lectureId}")
    public ResponseEntity<List<StudentQuestionResult>> getResultsByStudentAndLecture(
            @PathVariable Long studentId,
            @PathVariable Long lectureId) {
        return ResponseEntity.ok(studentQuestionResultService.getResultsByStudentAndLecture(studentId, lectureId));
    }

    // 문제별 결과 목록 조회
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<List<StudentQuestionResult>> getResultsByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(studentQuestionResultService.getResultsByQuestion(questionId));
    }

    // 강의별 결과 목록 조회
    @GetMapping("/lectures/{lectureId}")
    public ResponseEntity<List<StudentQuestionResult>> getResultsByLecture(@PathVariable Long lectureId) {
        return ResponseEntity.ok(studentQuestionResultService.getResultsByLecture(lectureId));
    }

    // 반별 결과 목록 조회
    @GetMapping("/classrooms/{classroomId}")
    public ResponseEntity<List<StudentQuestionResult>> getResultsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(studentQuestionResultService.getResultsByClassroom(classroomId));
    }
}