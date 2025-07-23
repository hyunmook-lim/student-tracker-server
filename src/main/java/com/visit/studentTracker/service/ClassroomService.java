package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.classroom.request.CreateClassroomRequest;
import com.visit.studentTracker.dto.classroom.request.UpdateClassroomRequest;
import com.visit.studentTracker.dto.classroom.response.ClassroomResponse;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.Teacher;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;

    public ClassroomService(ClassroomRepository classroomRepository, TeacherRepository teacherRepository) {
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
    }

    // CREATE
    @Transactional
    public ClassroomResponse createClassroom(CreateClassroomRequest dto) {
        if (classroomRepository.existsByClassName(dto.getClassName())) {
            throw new IllegalArgumentException("이미 존재하는 반 이름입니다.");
        }

        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("해당 선생님을 찾을 수 없습니다."));

        Classroom classroom = Classroom.builder()
                .className(dto.getClassName())
                .teacher(teacher)
                .description(dto.getDescription())
                .build();

        return toResponse(classroomRepository.save(classroom));
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

        if (dto.getClassName() != null && !dto.getClassName().equals(classroom.getClassName())) {
            if (classroomRepository.existsByClassName(dto.getClassName())) {
                throw new IllegalArgumentException("이미 존재하는 반 이름입니다.");
            }
            classroom.setClassName(dto.getClassName());
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
        return ClassroomResponse.builder()
                .uid(classroom.getUid())
                .className(classroom.getClassName())
                .teacherId(classroom.getTeacher().getUid())
                .teacherName(classroom.getTeacher().getName())
                .studentIds(classroom.getStudentList().stream()
                        .map(student -> student.getUid())
                        .collect(Collectors.toList()))
                .description(classroom.getDescription())
                .createdAt(classroom.getCreatedAt())
                .updatedAt(classroom.getUpdatedAt())
                .build();
    }
}