package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.StudentLectureResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentLectureResultRepository extends JpaRepository<StudentLectureResult, Long> {

        // 학생별 회차 기록 조회
        List<StudentLectureResult> findByStudentUid(Long studentId);

        // 회차별 학생 기록 조회
        List<StudentLectureResult> findByLectureUid(Long lectureId);

        // 특정 학생의 특정 회차 기록 조회
        Optional<StudentLectureResult> findByStudentUidAndLectureUid(Long studentId, Long lectureId);

        // 출석한 학생들만 조회
        List<StudentLectureResult> findByLectureUidAndIsAttendedTrue(Long lectureId);

        // 특정 회차의 출석률 계산
        @Query("SELECT COUNT(slr) FROM StudentLectureResult slr WHERE slr.lecture.uid = :lectureId AND slr.isAttended = true")
        Long countAttendedByLecture(@Param("lectureId") Long lectureId);

        @Query("SELECT COUNT(slr) FROM StudentLectureResult slr WHERE slr.lecture.uid = :lectureId")
        Long countTotalByLecture(@Param("lectureId") Long lectureId);

        // 학생의 전체 출석 기록 조회
        List<StudentLectureResult> findByStudentUidAndIsAttendedTrue(Long studentId);

        // 학생의 특정 반에서 받은 과제 점수 통계 조회
        @Query("SELECT slr.assignmentScore, COUNT(slr) FROM StudentLectureResult slr " +
                        "WHERE slr.student.uid = :studentId AND slr.lecture.classroom.uid = :classroomId " +
                        "GROUP BY slr.assignmentScore")
        List<Object[]> findAssignmentScoreStatisticsByStudentAndClassroom(@Param("studentId") Long studentId,
                        @Param("classroomId") Long classroomId);

        // 학생의 특정 반에서 받은 총 과제 수 조회
        @Query("SELECT COUNT(slr) FROM StudentLectureResult slr " +
                        "WHERE slr.student.uid = :studentId AND slr.lecture.classroom.uid = :classroomId")
        Long countTotalAssignmentsByStudentAndClassroom(@Param("studentId") Long studentId,
                        @Param("classroomId") Long classroomId);

        // 학생의 전체 출석률 계산
        @Query("SELECT COUNT(slr) FROM StudentLectureResult slr WHERE slr.student.uid = :studentId AND slr.isAttended = true")
        Long countTotalAttendedByStudent(@Param("studentId") Long studentId);

        @Query("SELECT COUNT(slr) FROM StudentLectureResult slr WHERE slr.student.uid = :studentId")
        Long countTotalLecturesByStudent(@Param("studentId") Long studentId);

        // 학생의 전체 과제 점수 통계 조회
        @Query("SELECT slr.assignmentScore, COUNT(slr) FROM StudentLectureResult slr " +
                        "WHERE slr.student.uid = :studentId GROUP BY slr.assignmentScore")
        List<Object[]> findAssignmentScoreStatisticsByStudent(@Param("studentId") Long studentId);

        // 학생의 전체 과제 수 조회
        @Query("SELECT COUNT(slr) FROM StudentLectureResult slr WHERE slr.student.uid = :studentId")
        Long countTotalAssignmentsByStudent(@Param("studentId") Long studentId);

}