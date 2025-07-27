package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.lecture.request.CreateLectureRequest;
import com.visit.studentTracker.dto.lecture.request.UpdateLectureRequest;
import com.visit.studentTracker.dto.lecture.response.LectureResponse;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.ClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final ClassroomRepository classroomRepository;

    public LectureService(LectureRepository lectureRepository, ClassroomRepository classroomRepository) {
        this.lectureRepository = lectureRepository;
        this.classroomRepository = classroomRepository;
    }

    // CREATE
    @Transactional
    public LectureResponse createLecture(CreateLectureRequest dto) {
        if (lectureRepository.existsByLectureName(dto.getLectureName())) {
            throw new IllegalArgumentException("이미 존재하는 강의명입니다.");
        }

        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

        Lecture lecture = Lecture.builder()
                .lectureName(dto.getLectureName())
                .description(dto.getDescription())
                .classroom(classroom)
                .build();

        return toResponse(lectureRepository.save(lecture));
    }

    // READ (단건)
    @Transactional(readOnly = true)
    public LectureResponse getLecture(Long id) {
        return lectureRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));
    }

    // READ (전체)
    @Transactional(readOnly = true)
    public List<LectureResponse> getAllLectures() {
        return lectureRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (반별 강의 목록)
    @Transactional(readOnly = true)
    public List<LectureResponse> getLecturesByClassroom(Long classroomId) {
        return lectureRepository.findByClassroomUid(classroomId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public LectureResponse updateLecture(Long id, UpdateLectureRequest dto) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        if (dto.getLectureName() != null && !dto.getLectureName().equals(lecture.getLectureName())) {
            if (lectureRepository.existsByLectureName(dto.getLectureName())) {
                throw new IllegalArgumentException("이미 존재하는 강의명입니다.");
            }
            lecture.setLectureName(dto.getLectureName());
        }

        if (dto.getDescription() != null) {
            lecture.setDescription(dto.getDescription());
        }

        lecture.setUpdatedAt(LocalDateTime.now());

        return toResponse(lecture);
    }

    // DELETE
    @Transactional
    public void deleteLecture(Long id) {
        if (!lectureRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 강의를 찾을 수 없습니다.");
        }
        lectureRepository.deleteById(id);
    }

    // DTO 변환 메서드
    private LectureResponse toResponse(Lecture lecture) {
        return LectureResponse.builder()
                .uid(lecture.getUid())
                .lectureName(lecture.getLectureName())
                .description(lecture.getDescription())
                .classroomId(lecture.getClassroom().getUid())
                .className(lecture.getClassroom().getClassroomName())
                .isActive(lecture.isActive())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .build();
    }
}