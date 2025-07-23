package com.visit.studentTracker.controller;

import com.visit.studentTracker.dto.lecture.request.CreateLectureRequest;
import com.visit.studentTracker.dto.lecture.request.UpdateLectureRequest;
import com.visit.studentTracker.dto.lecture.response.LectureResponse;
import com.visit.studentTracker.service.LectureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<LectureResponse> createLecture(@RequestBody CreateLectureRequest dto) {
        return ResponseEntity.ok(lectureService.createLecture(dto));
    }

    // READ (단건)
    @GetMapping("/{id}")
    public ResponseEntity<LectureResponse> getLecture(@PathVariable Long id) {
        return ResponseEntity.ok(lectureService.getLecture(id));
    }

    // READ (전체)
    @GetMapping
    public ResponseEntity<List<LectureResponse>> getAllLectures() {
        return ResponseEntity.ok(lectureService.getAllLectures());
    }

    // READ (반별 강의 목록)
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<LectureResponse>> getLecturesByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(lectureService.getLecturesByClassroom(classroomId));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<LectureResponse> updateLecture(
            @PathVariable Long id,
            @RequestBody UpdateLectureRequest dto) {
        return ResponseEntity.ok(lectureService.updateLecture(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
        lectureService.deleteLecture(id);
        return ResponseEntity.noContent().build();
    }
}