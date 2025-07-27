package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.StudentQuestionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentQuestionResultRepository extends JpaRepository<StudentQuestionResult, Long> {
    List<StudentQuestionResult> findByStudentUid(Long studentId);

    List<StudentQuestionResult> findByQuestionUid(Long questionId);

    List<StudentQuestionResult> findByLectureUid(Long lectureId);

    List<StudentQuestionResult> findByClassroomUid(Long classroomId);

    List<StudentQuestionResult> findByStudentUidAndClassroomUid(Long studentId, Long classroomId);

    List<StudentQuestionResult> findByStudentUidAndLectureUid(Long studentId, Long lectureId);

    Optional<StudentQuestionResult> findByStudentUidAndQuestionUid(Long studentId, Long questionId);

    boolean existsByStudentUidAndQuestionUid(Long studentId, Long questionId);
}