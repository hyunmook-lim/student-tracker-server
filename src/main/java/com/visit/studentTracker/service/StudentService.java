package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.student.request.CreateStudentRequest;
import com.visit.studentTracker.dto.student.request.UpdateStudentRequest;
import com.visit.studentTracker.dto.student.response.StudentResponse;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // CREATE
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest dto) {
        if (studentRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 로그인 아이디입니다.");
        }

        Student student = Student.builder()
                .loginId(dto.getLoginId())
                .password(dto.getPassword()) // 추후 BCrypt 암호화 예정
                .name(dto.getName())
                .className(dto.getClassName())
                .build();

        return toResponse(studentRepository.save(student));
    }

    // READ (단건)
    @Transactional(readOnly = true)
    public StudentResponse getStudent(Long id) {
        return studentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));
    }

    // READ (전체)
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        student.setName(dto.getName());
        student.setClassName(dto.getClassName());
        student.setUpdatedAt(LocalDateTime.now());

        return toResponse(student);
    }

    // DELETE
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 학생을 찾을 수 없습니다.");
        }
        studentRepository.deleteById(id);
    }

    // DTO 변환 메서드
    private StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .uid(student.getUid())
                .loginId(student.getLoginId())
                .name(student.getName())
                .className(student.getClassName())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .lastLoginAt(student.getLastLoginAt())
                .isActive(student.isActive())
                .build();
    }
}