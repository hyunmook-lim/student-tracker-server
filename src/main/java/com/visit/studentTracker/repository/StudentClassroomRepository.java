package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.StudentClassroom;
import com.visit.studentTracker.entity.StudentClassroomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentClassroomRepository extends JpaRepository<StudentClassroom, Long> {
    List<StudentClassroom> findByStudentUid(Long studentId);

    List<StudentClassroom> findByClassroomUid(Long classroomId);

    Optional<StudentClassroom> findByStudentUidAndClassroomUid(Long studentId, Long classroomId);

    boolean existsByStudentUidAndClassroomUid(Long studentId, Long classroomId);

    List<StudentClassroom> findByClassroomUidAndStatus(Long classroomId, StudentClassroomStatus status);

    List<StudentClassroom> findByStudentUidAndStatus(Long studentId, StudentClassroomStatus status);
}