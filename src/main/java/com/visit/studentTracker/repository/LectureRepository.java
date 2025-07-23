package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    boolean existsByLectureName(String lectureName);

    Optional<Lecture> findByLectureName(String lectureName);

    List<Lecture> findByClassroomUid(Long classroomId);
}