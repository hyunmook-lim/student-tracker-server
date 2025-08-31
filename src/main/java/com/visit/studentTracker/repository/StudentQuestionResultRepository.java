package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.StudentQuestionResult;
import com.visit.studentTracker.entity.StudentLectureResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentQuestionResultRepository extends JpaRepository<StudentQuestionResult, Long> {
        List<StudentQuestionResult> findByStudentLectureResultStudentUid(Long studentId);

        List<StudentQuestionResult> findByQuestionUid(Long questionId);

        List<StudentQuestionResult> findByStudentLectureResultLectureUid(Long lectureId);

        List<StudentQuestionResult> findByStudentLectureResultLectureClassroomUid(Long classroomId);

        List<StudentQuestionResult> findByStudentLectureResultStudentUidAndStudentLectureResultLectureClassroomUid(
                        Long studentId, Long classroomId);

        List<StudentQuestionResult> findByStudentLectureResultStudentUidAndStudentLectureResultLectureUid(
                        Long studentId,
                        Long lectureId);

        Optional<StudentQuestionResult> findByStudentLectureResultStudentUidAndQuestionUid(Long studentId,
                        Long questionId);

        boolean existsByStudentLectureResultStudentUidAndQuestionUid(Long studentId, Long questionId);

        List<StudentQuestionResult> findByStudentLectureResult(StudentLectureResult studentLectureResult);

        // 학생의 오답만 조회
        List<StudentQuestionResult> findByStudentLectureResultStudentUidAndIsCorrectFalse(Long studentId);

        // 학생의 오답을 대단원별로 그룹화하여 조회
        @Query("SELECT q.mainTopic, COUNT(sqr) FROM StudentQuestionResult sqr " +
                        "JOIN sqr.question q " +
                        "WHERE sqr.studentLectureResult.student.uid = :studentId AND sqr.isCorrect = false " +
                        "GROUP BY q.mainTopic")
        List<Object[]> findWrongAnswersByMainTopic(@Param("studentId") Long studentId);

        // 학생의 오답을 소단원별로 그룹화하여 조회
        @Query("SELECT q.subTopic, COUNT(sqr) FROM StudentQuestionResult sqr " +
                        "JOIN sqr.question q " +
                        "WHERE sqr.studentLectureResult.student.uid = :studentId AND sqr.isCorrect = false " +
                        "GROUP BY q.subTopic")
        List<Object[]> findWrongAnswersBySubTopic(@Param("studentId") Long studentId);

        // 학생의 오답을 난이도별로 그룹화하여 조회
        @Query("SELECT q.difficulty, COUNT(sqr) FROM StudentQuestionResult sqr " +
                        "JOIN sqr.question q " +
                        "WHERE sqr.studentLectureResult.student.uid = :studentId AND sqr.isCorrect = false " +
                        "GROUP BY q.difficulty")
        List<Object[]> findWrongAnswersByDifficulty(@Param("studentId") Long studentId);

        // 학생의 총 오답 수 조회
        long countByStudentLectureResultStudentUidAndIsCorrectFalse(Long studentId);

        // 학생의 총 문제 수 조회
        long countByStudentLectureResultStudentUid(Long studentId);

        // 특정 학생의 특정 반 문제 수 조회
        long countByStudentLectureResultStudentUidAndStudentLectureResultLectureClassroomUid(Long studentId,
                        Long classroomId);

        // 특정 학생의 특정 반 오답 수 조회
        long countByStudentLectureResultStudentUidAndStudentLectureResultLectureClassroomUidAndIsCorrectFalse(
                        Long studentId, Long classroomId);

        // 특정 학생의 특정 반에서 대단원별 문제 수와 오답 수 조회
        @Query("SELECT q.mainTopic, COUNT(sqr), " +
                        "SUM(CASE WHEN sqr.isCorrect = false THEN 1 ELSE 0 END) " +
                        "FROM StudentQuestionResult sqr " +
                        "JOIN sqr.question q " +
                        "WHERE sqr.studentLectureResult.student.uid = :studentId " +
                        "AND sqr.studentLectureResult.lecture.classroom.uid = :classroomId " +
                        "GROUP BY q.mainTopic " +
                        "ORDER BY (SUM(CASE WHEN sqr.isCorrect = false THEN 1 ELSE 0 END) * 100.0 / COUNT(sqr)) DESC")
        List<Object[]> findMainTopicStatisticsByStudentAndClassroom(@Param("studentId") Long studentId,
                        @Param("classroomId") Long classroomId);

        // 특정 학생의 특정 반에서 특정 대단원의 소단원별 문제 수와 오답 수 조회
        @Query("SELECT q.subTopic, COUNT(sqr), " +
                        "SUM(CASE WHEN sqr.isCorrect = false THEN 1 ELSE 0 END) " +
                        "FROM StudentQuestionResult sqr " +
                        "JOIN sqr.question q " +
                        "WHERE sqr.studentLectureResult.student.uid = :studentId " +
                        "AND sqr.studentLectureResult.lecture.classroom.uid = :classroomId " +
                        "AND q.mainTopic = :mainTopic " +
                        "GROUP BY q.subTopic " +
                        "ORDER BY (SUM(CASE WHEN sqr.isCorrect = false THEN 1 ELSE 0 END) * 100.0 / COUNT(sqr)) DESC")
        List<Object[]> findSubTopicStatisticsByStudentAndClassroomAndMainTopic(@Param("studentId") Long studentId,
                        @Param("classroomId") Long classroomId,
                        @Param("mainTopic") String mainTopic);

        // 특정 학생의 특정 반에서 대단원별 난이도별 통계 조회
        @Query("SELECT q.mainTopic, q.difficulty, COUNT(sqr), " +
                        "SUM(CASE WHEN sqr.isCorrect = false THEN 1 ELSE 0 END) " +
                        "FROM StudentQuestionResult sqr " +
                        "JOIN sqr.question q " +
                        "WHERE sqr.studentLectureResult.student.uid = :studentId " +
                        "AND sqr.studentLectureResult.lecture.classroom.uid = :classroomId " +
                        "GROUP BY q.mainTopic, q.difficulty " +
                        "ORDER BY q.mainTopic, q.difficulty")
        List<Object[]> findMainTopicDifficultyStatisticsByStudentAndClassroom(@Param("studentId") Long studentId,
                        @Param("classroomId") Long classroomId);

        // 특정 학생의 특정 반에서 대단원별 소단원별 통계 조회
        @Query("SELECT q.mainTopic, q.subTopic, COUNT(sqr), " +
                        "SUM(CASE WHEN sqr.isCorrect = false THEN 1 ELSE 0 END) " +
                        "FROM StudentQuestionResult sqr " +
                        "JOIN sqr.question q " +
                        "WHERE sqr.studentLectureResult.student.uid = :studentId " +
                        "AND sqr.studentLectureResult.lecture.classroom.uid = :classroomId " +
                        "GROUP BY q.mainTopic, q.subTopic " +
                        "ORDER BY q.mainTopic, q.subTopic")
        List<Object[]> findMainTopicSubTopicStatisticsByStudentAndClassroom(@Param("studentId") Long studentId,
                        @Param("classroomId") Long classroomId);
}