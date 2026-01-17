package com.visit.studentTracker.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.Question;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.StudentClassroom;
import com.visit.studentTracker.entity.StudentLectureResult;
import com.visit.studentTracker.entity.StudentQuestionResult;
import com.visit.studentTracker.entity.StudentReport;
import com.visit.studentTracker.entity.Teacher;
import com.visit.studentTracker.entity.TeacherClassroom;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.QuestionRepository;
import com.visit.studentTracker.repository.StudentClassroomRepository;
import com.visit.studentTracker.repository.StudentLectureResultRepository;
import com.visit.studentTracker.repository.StudentQuestionResultRepository;
import com.visit.studentTracker.repository.StudentReportRepository;
import com.visit.studentTracker.repository.StudentRepository;
import com.visit.studentTracker.repository.TeacherClassroomRepository;
import com.visit.studentTracker.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataMigrationService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final LectureRepository lectureRepository;
    private final QuestionRepository questionRepository;
    private final StudentClassroomRepository studentClassroomRepository;
    private final StudentLectureResultRepository studentLectureResultRepository;
    private final StudentQuestionResultRepository studentQuestionResultRepository;
    private final StudentReportRepository studentReportRepository;
    private final TeacherClassroomRepository teacherClassroomRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void migrateData() {
        log.info("Starting data migration from H2 to PostgreSQL...");

        try {
            // 1. Migrate Teachers
            migrateTeachers();

            // 2. Migrate Students
            migrateStudents();

            // 3. Migrate Classrooms
            migrateClassrooms();

            // 4. Migrate Lectures
            migrateLectures();

            // 5. Migrate Questions
            migrateQuestions();

            // 6. Migrate Relationships
            migrateRelationships();

            log.info("Data migration completed successfully!");

        } catch (Exception e) {
            log.error("Data migration failed: ", e);
            throw new RuntimeException("Data migration failed", e);
        }
    }

    private void migrateTeachers() {
        log.info("Migrating teachers...");
        List<Teacher> teachers = teacherRepository.findAll();
        log.info("Found {} teachers to migrate", teachers.size());
    }

    private void migrateStudents() {
        log.info("Migrating students...");
        List<Student> students = studentRepository.findAll();
        log.info("Found {} students to migrate", students.size());
    }

    private void migrateClassrooms() {
        log.info("Migrating classrooms...");
        List<Classroom> classrooms = classroomRepository.findAll();
        log.info("Found {} classrooms to migrate", classrooms.size());
    }

    private void migrateLectures() {
        log.info("Migrating lectures...");
        List<Lecture> lectures = lectureRepository.findAll();
        log.info("Found {} lectures to migrate", lectures.size());
    }

    private void migrateQuestions() {
        log.info("Migrating questions...");
        List<Question> questions = questionRepository.findAll();
        log.info("Found {} questions to migrate", questions.size());
    }

    private void migrateRelationships() {
        log.info("Migrating relationships...");

        // Student-Classroom relationships
        List<StudentClassroom> studentClassrooms = studentClassroomRepository.findAll();
        log.info("Found {} student-classroom relationships to migrate", studentClassrooms.size());

        // Teacher-Classroom relationships
        List<TeacherClassroom> teacherClassrooms = teacherClassroomRepository.findAll();
        log.info("Found {} teacher-classroom relationships to migrate", teacherClassrooms.size());

        // Student results
        List<StudentLectureResult> lectureResults = studentLectureResultRepository.findAll();
        log.info("Found {} student lecture results to migrate", lectureResults.size());

        List<StudentQuestionResult> questionResults = studentQuestionResultRepository.findAll();
        log.info("Found {} student question results to migrate", questionResults.size());

        List<StudentReport> reports = studentReportRepository.findAll();
        log.info("Found {} student reports to migrate", reports.size());
    }

    @Transactional(readOnly = true)
    public void backupToJson() throws IOException {
        log.info("Starting JSON backup of H2 database...");

        String backupDir = "h2_real_backup_" + System.currentTimeMillis() + "/";
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Backup all entities to JSON files
        backupTeachersToJson(backupDir);
        backupStudentsToJson(backupDir);
        backupClassroomsToJson(backupDir);
        backupLecturesToJson(backupDir);
        backupQuestionsToJson(backupDir);
        backupTeacherClassroomsToJson(backupDir);
        backupStudentClassroomsToJson(backupDir);
        backupStudentLectureResultsToJson(backupDir);
        backupStudentQuestionResultsToJson(backupDir);
        backupStudentReportsToJson(backupDir);

        log.info("JSON backup completed in directory: {}", backupDir);
    }

    private void backupTeachersToJson(String backupDir) throws IOException {
        List<Teacher> teachers = teacherRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "teachers.json"), teachers);
        log.info("Backed up {} teachers to JSON", teachers.size());
    }

    private void backupStudentsToJson(String backupDir) throws IOException {
        List<Student> students = studentRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "students.json"), students);
        log.info("Backed up {} students to JSON", students.size());
    }

    private void backupClassroomsToJson(String backupDir) throws IOException {
        List<Classroom> classrooms = classroomRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "classrooms.json"), classrooms);
        log.info("Backed up {} classrooms to JSON", classrooms.size());
    }

    private void backupLecturesToJson(String backupDir) throws IOException {
        List<Lecture> lectures = lectureRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "lectures.json"), lectures);
        log.info("Backed up {} lectures to JSON", lectures.size());
    }

    private void backupQuestionsToJson(String backupDir) throws IOException {
        List<Question> questions = questionRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "questions.json"), questions);
        log.info("Backed up {} questions to JSON", questions.size());
    }

    private void backupTeacherClassroomsToJson(String backupDir) throws IOException {
        List<TeacherClassroom> relationships = teacherClassroomRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "teacher-classrooms.json"), relationships);
        log.info("Backed up {} teacher-classroom relationships to JSON", relationships.size());
    }

    private void backupStudentClassroomsToJson(String backupDir) throws IOException {
        List<StudentClassroom> relationships = studentClassroomRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "student-classrooms.json"), relationships);
        log.info("Backed up {} student-classroom relationships to JSON", relationships.size());
    }

    private void backupStudentLectureResultsToJson(String backupDir) throws IOException {
        List<StudentLectureResult> results = studentLectureResultRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "student-lecture-results.json"), results);
        log.info("Backed up {} student lecture results to JSON", results.size());
    }

    private void backupStudentQuestionResultsToJson(String backupDir) throws IOException {
        List<StudentQuestionResult> results = studentQuestionResultRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "student-question-results.json"), results);
        log.info("Backed up {} student question results to JSON", results.size());
    }

    private void backupStudentReportsToJson(String backupDir) throws IOException {
        List<StudentReport> reports = studentReportRepository.findAll();
        objectMapper.writeValue(new File(backupDir + "student-reports.json"), reports);
        log.info("Backed up {} student reports to JSON", reports.size());
    }
}
