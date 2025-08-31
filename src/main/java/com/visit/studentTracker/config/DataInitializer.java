package com.visit.studentTracker.config;

import com.visit.studentTracker.entity.*;
import com.visit.studentTracker.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev") // 개발 환경에서만 실행
public class DataInitializer implements ApplicationRunner {

    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final QuestionRepository questionRepository;
    private final TeacherClassroomRepository teacherClassroomRepository;
    private final StudentClassroomRepository studentClassroomRepository;
    private final StudentQuestionResultRepository studentQuestionResultRepository;
    private final StudentLectureResultRepository studentLectureResultRepository;
    private final ReportRepository reportRepository;
    private final ReportLectureRepository reportLectureRepository;
    private final StudentReportRepository studentReportRepository;

    public DataInitializer(TeacherRepository teacherRepository,
            ClassroomRepository classroomRepository,
            StudentRepository studentRepository,
            LectureRepository lectureRepository,
            QuestionRepository questionRepository,
            TeacherClassroomRepository teacherClassroomRepository,
            StudentClassroomRepository studentClassroomRepository,
            StudentQuestionResultRepository studentQuestionResultRepository,
            StudentLectureResultRepository studentLectureResultRepository,
            ReportRepository reportRepository,
            ReportLectureRepository reportLectureRepository,
            StudentReportRepository studentReportRepository) {
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
        this.studentRepository = studentRepository;
        this.lectureRepository = lectureRepository;
        this.questionRepository = questionRepository;
        this.teacherClassroomRepository = teacherClassroomRepository;
        this.studentClassroomRepository = studentClassroomRepository;
        this.studentQuestionResultRepository = studentQuestionResultRepository;
        this.studentLectureResultRepository = studentLectureResultRepository;
        this.reportRepository = reportRepository;
        this.reportLectureRepository = reportLectureRepository;
        this.studentReportRepository = studentReportRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            initializeDataSafely();
        } catch (Exception e) {
            System.out.println("DataInitializer error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void initializeDataSafely() {
        // 기존 데이터 모두 삭제
        clearAllData();

        // 새 데이터 초기화
        initializeData();
    }

    private void initializeData() {
        // 1. 교사 1명 생성
        Teacher teacher = Teacher.builder()
                .loginId("admin")
                .password("admin")
                .name("관리자")
                .phone("010-1234-5678")
                .build();
        teacher = teacherRepository.save(teacher);

        // 2. 학생 7명 생성
        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Student student = Student.builder()
                    .loginId("test" + i)
                    .password("test" + i)
                    .name("Test Student " + i)
                    .phone("010-1111-111" + i)
                    .build();
            students.add(studentRepository.save(student));
        }

        // 3. 수업 3개 생성
        List<Classroom> classrooms = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Classroom classroom = Classroom.builder()
                    .classroomName("수학 " + i + "반")
                    .description("수학 " + i + "반 설명")
                    .build();
            classrooms.add(classroomRepository.save(classroom));

            // 교사-수업 관계 생성
            TeacherClassroom teacherClassroom = TeacherClassroom.builder()
                    .teacher(teacher)
                    .classroom(classroom)
                    .build();
            teacherClassroomRepository.save(teacherClassroom);

            // 모든 학생을 각 수업에 등록
            for (Student student : students) {
                StudentClassroom studentClassroom = StudentClassroom.builder()
                        .student(student)
                        .classroom(classroom)
                        .status(StudentClassroomStatus.APPROVED)
                        .build();
                studentClassroomRepository.save(studentClassroom);
            }
        }

        // 4. 각 수업당 회차 8개 생성 (현재 날짜보다 이전)
        LocalDateTime baseDate = LocalDateTime.now().minusDays(60);

        for (Classroom classroom : classrooms) {
            for (int lectureNum = 1; lectureNum <= 8; lectureNum++) {
                LocalDateTime lectureDate = baseDate.plusDays(lectureNum * 7); // 일주일 간격

                Lecture lecture = Lecture.builder()
                        .lectureName(classroom.getClassroomName() + " - " + lectureNum + "회차")
                        .description(lectureNum + "회차 수업 내용")
                        .lectureDate(lectureDate)
                        .classroom(classroom)
                        .build();
                lecture = lectureRepository.save(lecture);

                // 각 회차당 문제 10개 생성
                createQuestionsForLecture(lecture, lectureNum);
            }
        }

        // 5. 수학 1반에 대한 StudentLectureResult 및 StudentQuestionResult 생성
        createSampleResults(classrooms.get(0), students);
    }

    private void createQuestionsForLecture(Lecture lecture, int lectureNum) {
        String[] mainTopics = { "대수", "기하", "확률과 통계", "미적분", "함수", "수열", "벡터", "행렬", "정수론", "조합" };
        String[] subTopics = { "기초", "응용", "심화", "실전", "종합", "연습", "문제해결", "응용문제", "고급", "특수" };
        String[] difficulties = { "중", "중상", "상", "최상", "중", "중상", "상", "최상", "중", "중상" };
        String[] answers = { "1", "2", "3", "4", "5", "1", "2", "3", "4", "5" };
        int[] scores = { 10, 12, 15, 18, 20, 22, 25, 28, 30, 35 };

        for (int i = 1; i <= 10; i++) {
            Question question = Question.builder()
                    .number(i)
                    .mainTopic(mainTopics[i - 1])
                    .subTopic(subTopics[i - 1])
                    .answer(answers[i - 1])
                    .difficulty(difficulties[i - 1])
                    .score(scores[i - 1])
                    .lecture(lecture)
                    .build();
            questionRepository.save(question);
        }
    }

    private void createSampleResults(Classroom classroom, List<Student> students) {
        Random random = new Random();
        List<Lecture> lectures = lectureRepository.findByClassroomUid(classroom.getUid());

        for (Lecture lecture : lectures) {
            List<Question> questions = questionRepository.findByLectureUid(lecture.getUid());

            for (Student student : students) {
                // 90% 확률로 출석
                boolean isAttended = random.nextDouble() < 0.9;

                // 과제 점수를 A, B, C, D 중에서 학생 능력에 따라 설정
                String assignmentScore = getAssignmentScore(student.getLoginId(), random);

                // 과제 목록 생성 (회차별로 다른 과제)
                List<String> homework = generateHomeworkList(lecture.getLectureName(), student.getLoginId());

                StudentLectureResult studentLectureResult = StudentLectureResult.builder()
                        .student(student)
                        .lecture(lecture)
                        .isAttended(isAttended)
                        .assignmentScore(assignmentScore)
                        .homework(homework)
                        .build();
                studentLectureResult = studentLectureResultRepository.save(studentLectureResult);

                // 출석한 경우에만 문제 결과 생성
                if (isAttended) {
                    for (Question question : questions) {
                        // 학생별 실력 차이 반영 (test1이 가장 우수, test7이 가장 낮음)
                        double correctProbability = getStudentAbility(student.getLoginId(), question.getDifficulty());
                        boolean isCorrect = random.nextDouble() < correctProbability;

                        StudentQuestionResult questionResult = StudentQuestionResult.builder()
                                .studentLectureResult(studentLectureResult)
                                .question(question)
                                .studentAnswer(
                                        isCorrect ? question.getAnswer() : getRandomWrongAnswer(question.getAnswer()))
                                .isCorrect(isCorrect)
                                .build();
                        studentQuestionResultRepository.save(questionResult);
                    }
                }
            }

            // 해당 lecture의 결과 입력 완료 표시
            lecture.setResultEntered(true);
            lectureRepository.save(lecture);
        }
    }

    private double getStudentAbility(String loginId, String difficulty) {
        // 학생별 기본 능력치
        double baseAbility = switch (loginId) {
            case "test1" -> 0.95; // 최우수
            case "test2" -> 0.88;
            case "test3" -> 0.82;
            case "test4" -> 0.75;
            case "test5" -> 0.68;
            case "test6" -> 0.60;
            case "test7" -> 0.50; // 최하위
            default -> 0.70;
        };

        // 난이도별 조정
        double difficultyMultiplier = switch (difficulty) {
            case "중" -> 1.0;
            case "중상" -> 0.85;
            case "상" -> 0.70;
            case "최상" -> 0.55;
            default -> 0.80;
        };

        return Math.min(0.98, baseAbility * difficultyMultiplier); // 최대 98%로 제한
    }

    private String getRandomWrongAnswer(String correctAnswer) {
        String[] answers = { "1", "2", "3", "4", "5" };
        Random random = new Random();
        String wrongAnswer;
        do {
            wrongAnswer = answers[random.nextInt(answers.length)];
        } while (wrongAnswer.equals(correctAnswer));
        return wrongAnswer;
    }

    private String getAssignmentScore(String loginId, Random random) {
        // 학생별 A 등급 받을 확률
        double aGradeProbability = switch (loginId) {
            case "test1" -> 0.8; // 최우수
            case "test2" -> 0.65;
            case "test3" -> 0.5;
            case "test4" -> 0.35;
            case "test5" -> 0.25;
            case "test6" -> 0.15;
            case "test7" -> 0.1; // 최하위
            default -> 0.4;
        };

        double randomValue = random.nextDouble();

        if (randomValue < aGradeProbability) {
            return "A";
        } else if (randomValue < aGradeProbability + 0.3) {
            return "B";
        } else if (randomValue < aGradeProbability + 0.5) {
            return "C";
        } else {
            return "D";
        }
    }

    private List<String> generateHomeworkList(String lectureName, String studentLoginId) {
        List<String> homework = new ArrayList<>();
        Random random = new Random();

        // 회차 번호 추출
        int lectureNumber = extractLectureNumber(lectureName);

        // 기본 과제 목록 (회차별로 다른 과제)
        String[] baseHomework = {
                "수학 문제집 1-10번 풀기",
                "개념 정리 노트 작성",
                "오답 노트 정리",
                "추가 문제 5개 풀기",
                "단원 마무리 테스트 준비"
        };

        // 회차별 특화 과제
        String[] lectureSpecificHomework = {
                "기초 개념 복습",
                "중간 난이도 문제 연습",
                "고급 문제 도전",
                "실전 모의고사 연습",
                "종합 문제 해결",
                "심화 문제 풀이",
                "최종 점검 테스트",
                "전체 단원 복습"
        };

        // 학생별 추가 과제 (능력에 따라)
        String[] studentSpecificHomework = {
                "기본 문제 추가 연습",
                "개념 이해도 점검",
                "오답 분석 및 개선",
                "심화 문제 도전",
                "실전 문제 연습",
                "고급 문제 풀이",
                "최고 난이도 도전"
        };

        // 기본 과제 1개 선택
        homework.add(baseHomework[random.nextInt(baseHomework.length)]);

        // 회차별 특화 과제 1개 선택
        if (lectureNumber <= lectureSpecificHomework.length) {
            homework.add(lectureSpecificHomework[lectureNumber - 1]);
        }

        // 학생별 추가 과제 (학생 능력에 따라 개수 조정, 총 3개가 되도록)
        int additionalCount = Math.min(1, 3 - homework.size());
        for (int i = 0; i < additionalCount; i++) {
            homework.add(studentSpecificHomework[random.nextInt(studentSpecificHomework.length)]);
        }

        return homework;
    }

    private int extractLectureNumber(String lectureName) {
        // "수학 1반 - 3회차" 형태에서 3을 추출
        try {
            String[] parts = lectureName.split(" - ");
            if (parts.length > 1) {
                String lecturePart = parts[1];
                String numberPart = lecturePart.replaceAll("[^0-9]", "");
                return Integer.parseInt(numberPart);
            }
        } catch (Exception e) {
            // 파싱 실패 시 기본값 1 반환
        }
        return 1;
    }

    private int getStudentHomeworkCount(String studentLoginId) {
        // 학생별 추가 과제 개수 (총 3개가 되도록 조정)
        return switch (studentLoginId) {
            case "test1" -> 1; // 최우수 학생은 1개 추가 (총 3개)
            case "test2" -> 1;
            case "test3" -> 1;
            case "test4" -> 1;
            case "test5" -> 1;
            case "test6" -> 1;
            case "test7" -> 1; // 최하위 학생도 1개 추가 (총 3개)
            default -> 1;
        };
    }

    private void clearAllData() {
        // 순서대로 삭제 (외래키 제약조건 고려)
        studentQuestionResultRepository.deleteAll();
        studentLectureResultRepository.deleteAll();
        studentReportRepository.deleteAll();
        reportLectureRepository.deleteAll();
        reportRepository.deleteAll();
        questionRepository.deleteAll();
        lectureRepository.deleteAll();
        teacherClassroomRepository.deleteAll();
        studentClassroomRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        classroomRepository.deleteAll();
    }
}