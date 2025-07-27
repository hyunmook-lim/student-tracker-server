package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.classroom.request.CreateClassroomRequest;
import com.visit.studentTracker.dto.classroom.request.UpdateClassroomRequest;
import com.visit.studentTracker.dto.classroom.response.ClassroomResponse;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.Teacher;
import com.visit.studentTracker.entity.TeacherClassroom;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.TeacherRepository;
import com.visit.studentTracker.repository.TeacherClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherClassroomRepository teacherClassroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository,
            TeacherRepository teacherRepository,
            TeacherClassroomRepository teacherClassroomRepository) {
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
        this.teacherClassroomRepository = teacherClassroomRepository;
    }

    // CREATE
    @Transactional
    public ClassroomResponse createClassroom(CreateClassroomRequest dto) {
        if (classroomRepository.existsByClassroomName(dto.getClassroomName())) {
            throw new IllegalArgumentException("이미 존재하는 반 이름입니다.");
        }

        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("해당 선생님을 찾을 수 없습니다."));

        Classroom classroom = Classroom.builder()
                .classroomName(dto.getClassroomName())
                .description(dto.getDescription())
                .build();

        classroom = classroomRepository.save(classroom);

        // TeacherClassroom 관계 생성
        TeacherClassroom teacherClassroom = TeacherClassroom.builder()
                .teacher(teacher)
                .classroom(classroom)
                .build();
        teacherClassroomRepository.save(teacherClassroom);

        return toResponse(classroom);
    }

    // READ (단건)
    @Transactional(readOnly = true)
    public ClassroomResponse getClassroom(Long id) {
        return classroomRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));
    }

    // READ (전체)
    @Transactional(readOnly = true)
    public List<ClassroomResponse> getAllClassrooms() {
        return classroomRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public ClassroomResponse updateClassroom(Long id, UpdateClassroomRequest dto) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

        if (dto.getClassroomName() != null && !dto.getClassroomName().equals(classroom.getClassroomName())) {
            if (classroomRepository.existsByClassroomName(dto.getClassroomName())) {
                throw new IllegalArgumentException("이미 존재하는 반 이름입니다.");
            }
            classroom.setClassroomName(dto.getClassroomName());
        }

        if (dto.getDescription() != null) {
            classroom.setDescription(dto.getDescription());
        }

        classroom.setUpdatedAt(LocalDateTime.now());

        return toResponse(classroom);
    }

    // DELETE
    @Transactional
    public void deleteClassroom(Long id) {
        if (!classroomRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 반을 찾을 수 없습니다.");
        }
        classroomRepository.deleteById(id);
    }

    // DTO 변환 메서드
    private ClassroomResponse toResponse(Classroom classroom) {
        List<TeacherClassroom> teacherClassrooms = teacherClassroomRepository.findByClassroomUid(classroom.getUid());

        return ClassroomResponse.builder()
                .uid(classroom.getUid())
                .classroomName(classroom.getClassroomName())
                .teacherIds(teacherClassrooms.stream()
                        .map(tc -> tc.getTeacher().getUid())
                        .collect(Collectors.toList()))
                .teacherNames(teacherClassrooms.stream()
                        .map(tc -> tc.getTeacher().getName())
                        .collect(Collectors.toList()))
                .studentIds(classroom.getStudentClassroomList().stream()
                        .map(sc -> sc.getStudent().getUid())
                        .collect(Collectors.toList()))
                .studentNames(classroom.getStudentClassroomList().stream()
                        .map(sc -> sc.getStudent().getName())
                        .collect(Collectors.toList()))
                .description(classroom.getDescription())
                .createdAt(classroom.getCreatedAt())
                .updatedAt(classroom.getUpdatedAt())
                .build();
    }
}