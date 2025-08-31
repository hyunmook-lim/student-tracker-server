package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.student.request.CreateStudentRequest;
import com.visit.studentTracker.dto.student.request.UpdateStudentRequest;
import com.visit.studentTracker.dto.student.request.StudentLoginRequest;
import com.visit.studentTracker.dto.student.response.StudentResponse;
import com.visit.studentTracker.dto.student.response.StudentAnalyticsResponse;
import com.visit.studentTracker.dto.student.response.ClassroomStudentAnalyticsResponse;
import com.visit.studentTracker.dto.student.response.StudentDashboardResponse;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.StudentClassroom;
import com.visit.studentTracker.entity.StudentClassroomStatus;

import com.visit.studentTracker.repository.StudentRepository;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.StudentClassroomRepository;
import com.visit.studentTracker.repository.StudentQuestionResultRepository;
import com.visit.studentTracker.repository.StudentLectureResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class StudentService {

        private final StudentRepository studentRepository;
        private final ClassroomRepository classroomRepository;
        private final StudentClassroomRepository studentClassroomRepository;
        private final StudentQuestionResultRepository studentQuestionResultRepository;
        private final StudentLectureResultRepository studentLectureResultRepository;

        public StudentService(StudentRepository studentRepository,
                        ClassroomRepository classroomRepository,
                        StudentClassroomRepository studentClassroomRepository,
                        StudentQuestionResultRepository studentQuestionResultRepository,
                        StudentLectureResultRepository studentLectureResultRepository) {
                this.studentRepository = studentRepository;
                this.classroomRepository = classroomRepository;
                this.studentClassroomRepository = studentClassroomRepository;
                this.studentQuestionResultRepository = studentQuestionResultRepository;
                this.studentLectureResultRepository = studentLectureResultRepository;
        }

        // CREATE
        @Transactional
        public StudentResponse createStudent(CreateStudentRequest dto) {
                if (studentRepository.existsByLoginId(dto.getLoginId())) {
                        throw new IllegalArgumentException("이미 존재하는 로그인 아이디입니다.");
                }

                Student student = Student.builder()
                                .loginId(dto.getLoginId())
                                .password(dto.getPassword()) // 추후 BCrypt 암호화 예정
                                .name(dto.getName())
                                .phone(dto.getPhone())
                                .isActive(false)
                                .build();

                student = studentRepository.save(student);

                // 반에 학생 추가
                if (dto.getClassroomId() != null) {
                        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                                        .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

                        StudentClassroom studentClassroom = StudentClassroom.builder()
                                        .student(student)
                                        .classroom(classroom)
                                        .build();
                        studentClassroomRepository.save(studentClassroom);
                }

                return toResponse(student);
        }

        // LOGIN
        @Transactional
        public StudentResponse loginStudent(StudentLoginRequest dto) {
                Student student = studentRepository.findByLoginId(dto.getLoginId())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로그인 아이디입니다."));

                if (!student.getPassword().equals(dto.getPassword())) {
                        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                }

                // 로그인 시간 업데이트
                student.setLastLoginAt(LocalDateTime.now());
                student.setActive(true);
                student = studentRepository.save(student);

                return toResponse(student);
        }

        // READ (단건)
        @Transactional(readOnly = true)
        public StudentResponse getStudent(Long id) {
                return studentRepository.findById(id)
                                .map(this::toResponse)
                                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));
        }

        // READ (전체)
        @Transactional(readOnly = true)
        public List<StudentResponse> getAllStudents() {
                return studentRepository.findAll()
                                .stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());
        }

        // UPDATE
        @Transactional
        public StudentResponse updateStudent(Long id, UpdateStudentRequest dto) {
                Student student = studentRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

                if (dto.getName() != null) {
                        student.setName(dto.getName());
                }

                if (dto.getPhone() != null) {
                        student.setPhone(dto.getPhone());
                }

                if (dto.getClassroomId() != null) {
                        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                                        .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

                        // 기존 반 관계 제거
                        List<StudentClassroom> existingRelations = studentClassroomRepository.findByStudentUid(id);
                        studentClassroomRepository.deleteAll(existingRelations);

                        // 새로운 반 관계 생성
                        StudentClassroom studentClassroom = StudentClassroom.builder()
                                        .student(student)
                                        .classroom(classroom)
                                        .build();
                        studentClassroomRepository.save(studentClassroom);
                }

                student.setUpdatedAt(LocalDateTime.now());

                return toResponse(student);
        }

        // DELETE
        @Transactional
        public void deleteStudent(Long id) {
                if (!studentRepository.existsById(id)) {
                        throw new IllegalArgumentException("해당 학생을 찾을 수 없습니다.");
                }
                studentRepository.deleteById(id);
        }

        // 학생 분석 정보 조회
        @Transactional(readOnly = true)
        public StudentAnalyticsResponse getStudentAnalytics(Long studentId) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

                // 1. 학생이 듣고 있는 수업 수 (승인된 상태)
                Long totalClassrooms = studentClassroomRepository.countByStudentUidAndStatus(studentId,
                                StudentClassroomStatus.APPROVED);

                // 2. 학생이 실제로 푼 문제 수
                Long totalQuestions = studentQuestionResultRepository.countByStudentLectureResultStudentUid(studentId);

                // 3. 학생의 오답수
                Long totalWrongAnswers = studentQuestionResultRepository
                                .countByStudentLectureResultStudentUidAndIsCorrectFalse(studentId);

                // 4. 오답률 계산
                Double wrongAnswerRate = totalQuestions > 0 ? (double) totalWrongAnswers / totalQuestions * 100 : 0.0;

                // 5. 대단원별 오답 통계
                List<StudentAnalyticsResponse.TopicStatistics> wrongAnswersByMainTopic = studentQuestionResultRepository
                                .findWrongAnswersByMainTopic(studentId)
                                .stream()
                                .map(result -> {
                                        String topic = (String) result[0];
                                        Long count = (Long) result[1];
                                        Double percentage = totalWrongAnswers > 0
                                                        ? (double) count / totalWrongAnswers * 100
                                                        : 0.0;
                                        return StudentAnalyticsResponse.TopicStatistics.builder()
                                                        .topic(topic)
                                                        .count(count)
                                                        .percentage(percentage)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // 6. 소단원별 오답 통계
                List<StudentAnalyticsResponse.TopicStatistics> wrongAnswersBySubTopic = studentQuestionResultRepository
                                .findWrongAnswersBySubTopic(studentId)
                                .stream()
                                .map(result -> {
                                        String topic = (String) result[0];
                                        Long count = (Long) result[1];
                                        Double percentage = totalWrongAnswers > 0
                                                        ? (double) count / totalWrongAnswers * 100
                                                        : 0.0;
                                        return StudentAnalyticsResponse.TopicStatistics.builder()
                                                        .topic(topic)
                                                        .count(count)
                                                        .percentage(percentage)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // 7. 난이도별 오답 통계
                List<StudentAnalyticsResponse.DifficultyStatistics> wrongAnswersByDifficulty = studentQuestionResultRepository
                                .findWrongAnswersByDifficulty(studentId)
                                .stream()
                                .map(result -> {
                                        String difficulty = (String) result[0];
                                        Long count = (Long) result[1];
                                        Double percentage = totalWrongAnswers > 0
                                                        ? (double) count / totalWrongAnswers * 100
                                                        : 0.0;
                                        return StudentAnalyticsResponse.DifficultyStatistics.builder()
                                                        .difficulty(difficulty)
                                                        .count(count)
                                                        .percentage(percentage)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return StudentAnalyticsResponse.builder()
                                .studentId(studentId)
                                .studentName(student.getName())
                                .totalClassrooms(totalClassrooms)
                                .totalQuestions(totalQuestions)
                                .totalWrongAnswers(totalWrongAnswers)
                                .wrongAnswerRate(wrongAnswerRate)
                                .wrongAnswersByMainTopic(wrongAnswersByMainTopic)
                                .wrongAnswersBySubTopic(wrongAnswersBySubTopic)
                                .wrongAnswersByDifficulty(wrongAnswersByDifficulty)
                                .build();
        }

        // 특정 수업에 대한 학생 분석 정보 조회
        @Transactional(readOnly = true)
        public ClassroomStudentAnalyticsResponse getClassroomStudentAnalytics(Long studentId, Long classroomId) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

                Classroom classroom = classroomRepository.findById(classroomId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

                // 학생이 해당 반에 속해있는지 확인
                if (!studentClassroomRepository.existsByStudentUidAndClassroomUid(studentId, classroomId)) {
                        throw new IllegalArgumentException("해당 학생이 이 반에 속해있지 않습니다.");
                }

                // 1. 총 문제수 (학생이 실제로 푼 문제 수)
                Long totalQuestions = studentQuestionResultRepository
                                .countByStudentLectureResultStudentUidAndStudentLectureResultLectureClassroomUid(
                                                studentId, classroomId);

                // 2. 학생의 오답수
                Long totalWrongAnswers = studentQuestionResultRepository
                                .countByStudentLectureResultStudentUidAndStudentLectureResultLectureClassroomUidAndIsCorrectFalse(
                                                studentId, classroomId);

                // 3. 오답률 계산
                Double wrongAnswerRate = totalQuestions > 0 ? (double) totalWrongAnswers / totalQuestions * 100 : 0.0;

                // 4. 대단원별 난이도별 통계 조회
                List<Object[]> mainTopicDifficultyStats = studentQuestionResultRepository
                                .findMainTopicDifficultyStatisticsByStudentAndClassroom(studentId, classroomId);

                // 5. 대단원별 소단원별 통계 조회
                List<Object[]> mainTopicSubTopicStats = studentQuestionResultRepository
                                .findMainTopicSubTopicStatisticsByStudentAndClassroom(studentId, classroomId);

                // 대단원별로 그룹화하여 난이도별 통계 생성
                Map<String, List<Object[]>> groupedByMainTopicDifficulty = mainTopicDifficultyStats.stream()
                                .collect(Collectors.groupingBy(result -> (String) result[0]));

                // 대단원별로 그룹화하여 소단원별 통계 생성
                Map<String, List<Object[]>> groupedByMainTopicSubTopic = mainTopicSubTopicStats.stream()
                                .collect(Collectors.groupingBy(result -> (String) result[0]));

                List<ClassroomStudentAnalyticsResponse.MainTopicDifficultyStatistics> mainTopicDifficultyStatistics = groupedByMainTopicDifficulty
                                .entrySet().stream()
                                .map(entry -> {
                                        String mainTopic = entry.getKey();
                                        List<Object[]> difficultyStats = entry.getValue();

                                        // 난이도별 통계 생성
                                        List<ClassroomStudentAnalyticsResponse.DifficultyStatistics> difficultyStatistics = difficultyStats
                                                        .stream()
                                                        .map(stat -> {
                                                                String difficulty = (String) stat[1];
                                                                Long totalCount = (Long) stat[2];
                                                                Long wrongCount = (Long) stat[3];
                                                                Double wrongRate = totalCount > 0
                                                                                ? (double) wrongCount / totalCount * 100
                                                                                : 0.0;

                                                                return ClassroomStudentAnalyticsResponse.DifficultyStatistics
                                                                                .builder()
                                                                                .difficulty(difficulty)
                                                                                .totalQuestionCount(totalCount)
                                                                                .wrongAnswerCount(wrongCount)
                                                                                .wrongAnswerRate(wrongRate)
                                                                                .build();
                                                        })
                                                        .collect(Collectors.toList());

                                        // 소단원별 통계 생성
                                        List<ClassroomStudentAnalyticsResponse.SubTopicStatistics> subTopicStatistics = groupedByMainTopicSubTopic
                                                        .getOrDefault(mainTopic, new ArrayList<>())
                                                        .stream()
                                                        .map(stat -> {
                                                                String subTopic = (String) stat[1];
                                                                Long totalCount = (Long) stat[2];
                                                                Long wrongCount = (Long) stat[3];
                                                                Double wrongRate = totalCount > 0
                                                                                ? (double) wrongCount / totalCount * 100
                                                                                : 0.0;

                                                                return ClassroomStudentAnalyticsResponse.SubTopicStatistics
                                                                                .builder()
                                                                                .subTopic(subTopic)
                                                                                .wrongAnswerCount(wrongCount)
                                                                                .totalQuestionCount(totalCount)
                                                                                .wrongAnswerRate(wrongRate)
                                                                                .build();
                                                        })
                                                        .collect(Collectors.toList());

                                        return ClassroomStudentAnalyticsResponse.MainTopicDifficultyStatistics.builder()
                                                        .mainTopic(mainTopic)
                                                        .difficultyStatistics(difficultyStatistics)
                                                        .subTopicStatistics(subTopicStatistics)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // 7. 대단원별 통계 조회 (오답률 순으로 정렬)
                List<Object[]> mainTopicStats = studentQuestionResultRepository
                                .findMainTopicStatisticsByStudentAndClassroom(studentId, classroomId);

                // 8. 오답률 top3의 대단원 처리
                List<ClassroomStudentAnalyticsResponse.MainTopicStatistics> top3MainTopics = mainTopicStats.stream()
                                .limit(3) // top3만 선택
                                .map(result -> {
                                        String mainTopic = (String) result[0];
                                        Long totalCount = (Long) result[1];
                                        Long wrongCount = (Long) result[2];
                                        Double mainTopicWrongRate = totalCount > 0
                                                        ? (double) wrongCount / totalCount * 100
                                                        : 0.0;

                                        // 9. 각 대단원의 소단원별 통계 조회 (오답률 순으로 정렬)
                                        List<Object[]> subTopicStats = studentQuestionResultRepository
                                                        .findSubTopicStatisticsByStudentAndClassroomAndMainTopic(
                                                                        studentId, classroomId, mainTopic);

                                        // 10. 각 대단원의 소단원 top3 처리
                                        List<ClassroomStudentAnalyticsResponse.SubTopicStatistics> top3SubTopics = subTopicStats
                                                        .stream()
                                                        .limit(3) // top3만 선택
                                                        .map(subResult -> {
                                                                String subTopic = (String) subResult[0];
                                                                Long subTotalCount = (Long) subResult[1];
                                                                Long subWrongCount = (Long) subResult[2];
                                                                Double subTopicWrongRate = subTotalCount > 0
                                                                                ? (double) subWrongCount / subTotalCount
                                                                                                * 100
                                                                                : 0.0;

                                                                return ClassroomStudentAnalyticsResponse.SubTopicStatistics
                                                                                .builder()
                                                                                .subTopic(subTopic)
                                                                                .wrongAnswerCount(subWrongCount)
                                                                                .totalQuestionCount(subTotalCount)
                                                                                .wrongAnswerRate(subTopicWrongRate)
                                                                                .build();
                                                        })
                                                        .collect(Collectors.toList());

                                        return ClassroomStudentAnalyticsResponse.MainTopicStatistics.builder()
                                                        .mainTopic(mainTopic)
                                                        .wrongAnswerCount(wrongCount)
                                                        .totalQuestionCount(totalCount)
                                                        .wrongAnswerRate(mainTopicWrongRate)
                                                        .top3SubTopics(top3SubTopics)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // 11. 과제 점수 통계 조회
                List<Object[]> assignmentScoreStats = studentLectureResultRepository
                                .findAssignmentScoreStatisticsByStudentAndClassroom(studentId, classroomId);
                Long totalAssignments = studentLectureResultRepository
                                .countTotalAssignmentsByStudentAndClassroom(studentId, classroomId);

                // 과제 점수별 개수 계산
                Long aCount = 0L, bCount = 0L, cCount = 0L, dCount = 0L;
                for (Object[] stat : assignmentScoreStats) {
                        String score = (String) stat[0];
                        Long count = (Long) stat[1];
                        switch (score.toUpperCase()) {
                                case "A":
                                        aCount = count;
                                        break;
                                case "B":
                                        bCount = count;
                                        break;
                                case "C":
                                        cCount = count;
                                        break;
                                case "D":
                                        dCount = count;
                                        break;
                        }
                }

                // 과제 점수별 비율 계산
                Double aRate = totalAssignments > 0 ? (double) aCount / totalAssignments * 100 : 0.0;
                Double bRate = totalAssignments > 0 ? (double) bCount / totalAssignments * 100 : 0.0;
                Double cRate = totalAssignments > 0 ? (double) cCount / totalAssignments * 100 : 0.0;
                Double dRate = totalAssignments > 0 ? (double) dCount / totalAssignments * 100 : 0.0;

                ClassroomStudentAnalyticsResponse.AssignmentScoreStatistics assignmentScoreStatistics = ClassroomStudentAnalyticsResponse.AssignmentScoreStatistics
                                .builder()
                                .totalAssignments(totalAssignments)
                                .aCount(aCount)
                                .bCount(bCount)
                                .cCount(cCount)
                                .dCount(dCount)
                                .aRate(aRate)
                                .bRate(bRate)
                                .cRate(cRate)
                                .dRate(dRate)
                                .build();

                return ClassroomStudentAnalyticsResponse.builder()
                                .studentId(studentId)
                                .studentName(student.getName())
                                .classroomId(classroomId)
                                .classroomName(classroom.getClassroomName())
                                .totalQuestions(totalQuestions)
                                .totalWrongAnswers(totalWrongAnswers)
                                .wrongAnswerRate(wrongAnswerRate)
                                .top3MainTopics(top3MainTopics)
                                .mainTopicDifficultyStatistics(mainTopicDifficultyStatistics)
                                .assignmentScoreStatistics(assignmentScoreStatistics)
                                .build();
        }

        // 학생 대시보드 정보 조회
        @Transactional(readOnly = true)
        public StudentDashboardResponse getStudentDashboard(Long studentId) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

                // 1. 현재 수강 중인 반 수
                Long activeClassroomCount = studentClassroomRepository.countByStudentUidAndStatus(studentId,
                                StudentClassroomStatus.APPROVED);

                // 2. 전체 출석률 계산
                Long totalAttended = studentLectureResultRepository.countTotalAttendedByStudent(studentId);
                Long totalLectures = studentLectureResultRepository.countTotalLecturesByStudent(studentId);
                Double overallAttendanceRate = totalLectures > 0 ? (double) totalAttended / totalLectures * 100 : 0.0;

                // 3. 전체 평균 점수 (과제 점수를 숫자로 변환하여 계산)
                List<Object[]> assignmentScoreStats = studentLectureResultRepository
                                .findAssignmentScoreStatisticsByStudent(studentId);
                Long totalAssignments = studentLectureResultRepository.countTotalAssignmentsByStudent(studentId);

                // 과제 점수별 개수 계산
                Long aCount = 0L, bCount = 0L, cCount = 0L, dCount = 0L;
                for (Object[] stat : assignmentScoreStats) {
                        String score = (String) stat[0];
                        Long count = (Long) stat[1];
                        switch (score.toUpperCase()) {
                                case "A":
                                        aCount = count;
                                        break;
                                case "B":
                                        bCount = count;
                                        break;
                                case "C":
                                        cCount = count;
                                        break;
                                case "D":
                                        dCount = count;
                                        break;
                        }
                }

                // 전체 평균 점수 계산 (A-100점, B-75점, C-50점, D-25점)
                Double overallAverageScore = 0.0;
                if (totalAssignments > 0) {
                        double totalScore = (aCount * 100.0) + (bCount * 75.0) + (cCount * 50.0) + (dCount * 25.0);
                        overallAverageScore = totalScore / totalAssignments;
                }

                // 4. 과제 평균 완성도 (A-100점, B-75점, C-50점, D-25점 기준)
                Double assignmentCompletionRate = overallAverageScore; // 이미 위에서 계산된 값과 동일

                return StudentDashboardResponse.builder()
                                .studentId(studentId)
                                .studentName(student.getName())
                                .activeClassroomCount(activeClassroomCount)
                                .overallAttendanceRate(overallAttendanceRate)
                                .overallAverageScore(overallAverageScore)
                                .assignmentCompletionRate(assignmentCompletionRate)
                                .build();
        }

        // DTO 변환 메서드
        private StudentResponse toResponse(Student student) {
                List<StudentClassroom> studentClassrooms = studentClassroomRepository
                                .findByStudentUid(student.getUid());
                List<Long> classroomIds = studentClassrooms.stream()
                                .map(sc -> sc.getClassroom().getUid())
                                .collect(Collectors.toList());
                List<String> classroomNames = studentClassrooms.stream()
                                .map(sc -> sc.getClassroom().getClassroomName())
                                .collect(Collectors.toList());

                return StudentResponse.builder()
                                .uid(student.getUid())
                                .loginId(student.getLoginId())
                                .name(student.getName())
                                .phone(student.getPhone())
                                .classroomIds(classroomIds)
                                .classroomNames(classroomNames)
                                .isActive(student.isActive())
                                .createdAt(student.getCreatedAt())
                                .updatedAt(student.getUpdatedAt())
                                .lastLoginAt(student.getLastLoginAt())
                                .build();
        }
}