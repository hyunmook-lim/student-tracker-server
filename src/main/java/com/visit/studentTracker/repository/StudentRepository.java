package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByLoginId(String loginId);

    Optional<Student> findByLoginId(String loginId);
}