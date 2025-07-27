package com.visit.studentTracker.service;

import com.visit.studentTracker.entity.TeacherClassroom;
import com.visit.studentTracker.entity.Teacher;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.repository.TeacherClassroomRepository;
import com.visit.studentTracker.repository.TeacherRepository;
import com.visit.studentTracker.repository.ClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherClassroomService {

    private final TeacherClassroomRepository teacherClassroomRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;

    public TeacherClassroomService(TeacherClassroomRepository teacherClassroomRepository,
            TeacherRepository teacherRepository,
            ClassroomRepository classroomRepository) {
        this.teacherClassroomRepository = teacherClassroomRepository;
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
    }

    // 선생님을 반에 추가
    @Transactional
    public TeacherClassroom addTeacherToClassroom(Long teacherId, Long classroomId) {
        if (teacherClassroomRepository.existsByTeacherUidAndClassroomUid(teacherId, classroomId)) {
            throw new IllegalArgumentException("이미 해당 선생님이 반에 등록되어 있습니다.");
        }

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 선생님을 찾을 수 없습니다."));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

        TeacherClassroom teacherClassroom = TeacherClassroom.builder()
                .teacher(teacher)
                .classroom(classroom)
                .build();

        return teacherClassroomRepository.save(teacherClassroom);
    }

    // 선생님을 반에서 제거
    @Transactional
    public void removeTeacherFromClassroom(Long teacherId, Long classroomId) {
        TeacherClassroom teacherClassroom = teacherClassroomRepository
                .findByTeacherUidAndClassroomUid(teacherId, classroomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 선생님-반 관계를 찾을 수 없습니다."));

        teacherClassroomRepository.delete(teacherClassroom);
    }

    // 선생님의 반 목록 조회
    @Transactional(readOnly = true)
    public List<TeacherClassroom> getClassroomsByTeacher(Long teacherId) {
        return teacherClassroomRepository.findByTeacherUid(teacherId);
    }

    // 반의 선생님 목록 조회
    @Transactional(readOnly = true)
    public List<TeacherClassroom> getTeachersByClassroom(Long classroomId) {
        return teacherClassroomRepository.findByClassroomUid(classroomId);
    }
}