package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.teacher.request.CreateTeacherRequest;
import com.visit.studentTracker.dto.teacher.request.UpdateTeacherRequest;
import com.visit.studentTracker.dto.teacher.request.TeacherLoginRequest;
import com.visit.studentTracker.dto.teacher.response.TeacherResponse;
import com.visit.studentTracker.entity.Teacher;
import com.visit.studentTracker.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    // CREATE
    @Transactional
    public TeacherResponse createTeacher(CreateTeacherRequest dto) {
        if (teacherRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 로그인 아이디입니다.");
        }

        Teacher teacher = Teacher.builder()
                .loginId(dto.getLoginId())
                .password(dto.getPassword()) // 추후 BCrypt 암호화 예정
                .name(dto.getName())
                .phone(dto.getPhone())
                .isActive(true)
                .build();

        return toResponse(teacherRepository.save(teacher));
    }

    // READ (단건)
    @Transactional(readOnly = true)
    public TeacherResponse getTeacher(Long id) {
        return teacherRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 선생님을 찾을 수 없습니다."));
    }

    // READ (전체)
    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public TeacherResponse updateTeacher(Long id, UpdateTeacherRequest dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 선생님을 찾을 수 없습니다."));

        if (dto.getName() != null) {
            teacher.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            teacher.setPhone(dto.getPhone());
        }
        teacher.setUpdatedAt(LocalDateTime.now());

        return toResponse(teacher);
    }

    // DELETE
    @Transactional
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 선생님을 찾을 수 없습니다.");
        }
        teacherRepository.deleteById(id);
    }

    // LOGIN
    @Transactional
    public TeacherResponse loginTeacher(TeacherLoginRequest dto) {
        Teacher teacher = teacherRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("로그인 아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!teacher.isActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        if (!teacher.getPassword().equals(dto.getPassword())) {
            throw new IllegalArgumentException("로그인 아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 마지막 로그인 시간 업데이트
        teacher.setLastLoginAt(LocalDateTime.now());
        teacherRepository.save(teacher);

        return toResponse(teacher);
    }

    // DTO 변환 메서드
    private TeacherResponse toResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .uid(teacher.getUid())
                .loginId(teacher.getLoginId())
                .name(teacher.getName())
                .phone(teacher.getPhone())
                .isActive(teacher.isActive())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .lastLoginAt(teacher.getLastLoginAt())
                .build();
    }
}