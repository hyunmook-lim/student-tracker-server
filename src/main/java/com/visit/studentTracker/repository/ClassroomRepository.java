package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    boolean existsByClassName(String className);

    Optional<Classroom> findByClassName(String className);
}