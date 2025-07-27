package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.student.request.CreateStudentRequest;
import com.visit.studentTracker.dto.student.request.UpdateStudentRequest;
import com.visit.studentTracker.dto.student.response.StudentResponse;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.StudentClassroom;
import com.visit.studentTracker.repository.StudentRepository;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.StudentClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentClassroomRepository studentClassroomRepository;

    public StudentService(StudentRepository studentRepository,
            ClassroomRepository classroomRepository,
            StudentClassroomRepository studentClassroomRepository) {
        this.studentRepository = studentRepository;
        this.classroomRepository = classroomRepository;
        this.studentClassroomRepository = studentClassroomRepository;
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
                .isActive(false)
                .build();

        student = studentRepository.save(student);

        // 반에 학생 추가
        if (dto.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

            StudentClassroom studentClassroom = StudentClassroom.builder()
                    .student(student)
                    .classroom(classroom)
                    .build();
            studentClassroomRepository.save(studentClassroom);
        }

        return toResponse(student);
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

        if (dto.getName() != null) {
            student.setName(dto.getName());
        }

        if (dto.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

            // 기존 반 관계 제거
            List<StudentClassroom> existingRelations = studentClassroomRepository.findByStudentUid(id);
            studentClassroomRepository.deleteAll(existingRelations);

            // 새로운 반 관계 생성
            StudentClassroom studentClassroom = StudentClassroom.builder()
                    .student(student)
                    .classroom(classroom)
                    .build();
            studentClassroomRepository.save(studentClassroom);
        }

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
        List<StudentClassroom> studentClassrooms = studentClassroomRepository.findByStudentUid(student.getUid());
        List<Long> classroomIds = studentClassrooms.stream()
                .map(sc -> sc.getClassroom().getUid())
                .collect(Collectors.toList());
        List<String> classroomNames = studentClassrooms.stream()
                .map(sc -> sc.getClassroom().getClassroomName())
                .collect(Collectors.toList());

        return StudentResponse.builder()
                .uid(student.getUid())
                .loginId(student.getLoginId())
                .name(student.getName())
                .classroomIds(classroomIds)
                .classroomNames(classroomNames)
                .isActive(student.isActive())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .lastLoginAt(student.getLastLoginAt())
                .build();
    }
}