package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByLoginId(String loginId);

    Optional<Teacher> findByLoginId(String loginId);
}