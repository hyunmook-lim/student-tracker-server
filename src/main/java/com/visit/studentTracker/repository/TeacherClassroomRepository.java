package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.TeacherClassroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherClassroomRepository extends JpaRepository<TeacherClassroom, Long> {
    List<TeacherClassroom> findByTeacherUid(Long teacherId);

    List<TeacherClassroom> findByClassroomUid(Long classroomId);

    Optional<TeacherClassroom> findByTeacherUidAndClassroomUid(Long teacherId, Long classroomId);

    boolean existsByTeacherUidAndClassroomUid(Long teacherId, Long classroomId);
}