package com.visit.studentTracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visit.studentTracker.dto.classroom.response.ClassroomResponse;
import com.visit.studentTracker.dto.student.response.StudentResponse;
import com.visit.studentTracker.dto.studentClassroom.response.StudentClassroomResponse;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.StudentClassroom;
import com.visit.studentTracker.entity.StudentClassroomStatus;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.StudentClassroomRepository;
import com.visit.studentTracker.repository.StudentRepository;

@Service
@SuppressWarnings("null")
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

        // 학생을 반에 추가 (기본적으로 WAITING 상태)
        @Transactional
        public StudentClassroomResponse addStudentToClassroom(Long studentId, Long classroomId) {
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
                                .status(StudentClassroomStatus.WAITING)
                                .build();

                StudentClassroom savedStudentClassroom = studentClassroomRepository.save(studentClassroom);
                return convertToResponse(savedStudentClassroom);
        }

        // 학생의 반 신청 상태 업데이트
        @Transactional
        public StudentClassroomResponse updateStudentClassroomStatus(Long studentId, Long classroomId,
                        StudentClassroomStatus status) {
                StudentClassroom studentClassroom = studentClassroomRepository
                                .findByStudentUidAndClassroomUid(studentId, classroomId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 학생-반 관계를 찾을 수 없습니다."));

                studentClassroom.setStatus(status);
                StudentClassroom savedStudentClassroom = studentClassroomRepository.save(studentClassroom);
                return convertToResponse(savedStudentClassroom);
        }

        // 특정 상태의 학생-반 관계 조회
        @Transactional(readOnly = true)
        public List<StudentClassroomResponse> getStudentClassroomsByStatus(Long classroomId,
                        StudentClassroomStatus status) {
                return studentClassroomRepository.findByClassroomUidAndStatus(classroomId, status)
                                .stream()
                                .map(this::convertToResponse)
                                .collect(Collectors.toList());
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
        public List<StudentClassroomResponse> getClassroomsByStudent(Long studentId) {
                return studentClassroomRepository.findByStudentUid(studentId)
                                .stream()
                                .map(this::convertToResponse)
                                .collect(Collectors.toList());
        }

        // 반의 학생 목록 조회
        @Transactional(readOnly = true)
        public List<StudentClassroomResponse> getStudentsByClassroom(Long classroomId) {
                return studentClassroomRepository.findByClassroomUid(classroomId)
                                .stream()
                                .map(this::convertToResponse)
                                .collect(Collectors.toList());
        }

        // StudentClassroom 엔티티를 StudentClassroomResponse DTO로 변환
        private StudentClassroomResponse convertToResponse(StudentClassroom studentClassroom) {
                StudentResponse studentResponse = StudentResponse.builder()
                                .uid(studentClassroom.getStudent().getUid())
                                .name(studentClassroom.getStudent().getName())
                                .loginId(studentClassroom.getStudent().getLoginId())
                                .phone(studentClassroom.getStudent().getPhone())
                                .build();

                ClassroomResponse classroomResponse = ClassroomResponse.builder()
                                .uid(studentClassroom.getClassroom().getUid())
                                .classroomName(studentClassroom.getClassroom().getClassroomName())
                                .description(studentClassroom.getClassroom().getDescription())
                                .build();

                return StudentClassroomResponse.builder()
                                .uid(studentClassroom.getUid())
                                .student(studentResponse)
                                .classroom(classroomResponse)
                                .status(studentClassroom.getStatus())
                                .build();
        }
}