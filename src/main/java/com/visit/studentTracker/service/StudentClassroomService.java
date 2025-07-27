package com.visit.studentTracker.service;

import com.visit.studentTracker.entity.StudentClassroom;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.repository.StudentClassroomRepository;
import com.visit.studentTracker.repository.StudentRepository;
import com.visit.studentTracker.repository.ClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentClassroomService {

    private final StudentClassroomRepository studentClassroomRepository;
    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;

    public StudentClassroomService(StudentClassroomRepository studentClassroomRepository,
            StudentRepository studentRepository,
            ClassroomRepository classroomRepository) {
        this.studentClassroomRepository = studentClassroomRepository;
        this.studentRepository = studentRepository;
        this.classroomRepository = classroomRepository;
    }

    // 학생을 반에 추가
    @Transactional
    public StudentClassroom addStudentToClassroom(Long studentId, Long classroomId) {
        if (studentClassroomRepository.existsByStudentUidAndClassroomUid(studentId, classroomId)) {
            throw new IllegalArgumentException("이미 해당 학생이 반에 등록되어 있습니다.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

        StudentClassroom studentClassroom = StudentClassroom.builder()
                .student(student)
                .classroom(classroom)
                .build();

        return studentClassroomRepository.save(studentClassroom);
    }

    // 학생을 반에서 제거
    @Transactional
    public void removeStudentFromClassroom(Long studentId, Long classroomId) {
        StudentClassroom studentClassroom = studentClassroomRepository
                .findByStudentUidAndClassroomUid(studentId, classroomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생-반 관계를 찾을 수 없습니다."));

        studentClassroomRepository.delete(studentClassroom);
    }

    // 학생의 반 목록 조회
    @Transactional(readOnly = true)
    public List<StudentClassroom> getClassroomsByStudent(Long studentId) {
        return studentClassroomRepository.findByStudentUid(studentId);
    }

    // 반의 학생 목록 조회
    @Transactional(readOnly = true)
    public List<StudentClassroom> getStudentsByClassroom(Long classroomId) {
        return studentClassroomRepository.findByClassroomUid(classroomId);
    }
}