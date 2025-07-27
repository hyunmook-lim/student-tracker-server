package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    boolean existsByClassroomName(String classroomName);

    Optional<Classroom> findByClassroomName(String classroomName);
}