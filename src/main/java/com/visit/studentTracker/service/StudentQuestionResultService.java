package com.visit.studentTracker.service;

import com.visit.studentTracker.entity.StudentQuestionResult;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.Question;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.repository.StudentQuestionResultRepository;
import com.visit.studentTracker.repository.StudentRepository;
import com.visit.studentTracker.repository.QuestionRepository;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.ClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentQuestionResultService {

    private final StudentQuestionResultRepository studentQuestionResultRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final ClassroomRepository classroomRepository;

    public StudentQuestionResultService(StudentQuestionResultRepository studentQuestionResultRepository,
            StudentRepository studentRepository,
            QuestionRepository questionRepository,
            LectureRepository lectureRepository,
            ClassroomRepository classroomRepository) {
        this.studentQuestionResultRepository = studentQuestionResultRepository;
        this.studentRepository = studentRepository;
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.classroomRepository = classroomRepository;
    }

    // 학생 문제 결과 저장
    @Transactional
    public StudentQuestionResult saveQuestionResult(Long studentId, Long questionId,
            Long lectureId, Long classroomId, boolean isCorrect) {
        if (studentQuestionResultRepository.existsByStudentUidAndQuestionUid(studentId, questionId)) {
            throw new IllegalArgumentException("이미 해당 학생의 문제 결과가 존재합니다.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제를 찾을 수 없습니다."));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

        StudentQuestionResult result = StudentQuestionResult.builder()
                .student(student)
                .question(question)
                .lecture(lecture)
                .classroom(classroom)
                .isCorrect(isCorrect)
                .build();

        return studentQuestionResultRepository.save(result);
    }

    // 학생 문제 결과 수정
    @Transactional
    public StudentQuestionResult updateQuestionResult(Long studentId, Long questionId, boolean isCorrect) {
        StudentQuestionResult result = studentQuestionResultRepository
                .findByStudentUidAndQuestionUid(studentId, questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생의 문제 결과를 찾을 수 없습니다."));

        result.setCorrect(isCorrect);
        return studentQuestionResultRepository.save(result);
    }

    // 학생의 문제 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByStudent(Long studentId) {
        return studentQuestionResultRepository.findByStudentUid(studentId);
    }

    // 학생의 특정 반 문제 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByStudentAndClassroom(Long studentId, Long classroomId) {
        return studentQuestionResultRepository.findByStudentUidAndClassroomUid(studentId, classroomId);
    }

    // 학생의 특정 강의 문제 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByStudentAndLecture(Long studentId, Long lectureId) {
        return studentQuestionResultRepository.findByStudentUidAndLectureUid(studentId, lectureId);
    }

    // 문제별 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByQuestion(Long questionId) {
        return studentQuestionResultRepository.findByQuestionUid(questionId);
    }

    // 강의별 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByLecture(Long lectureId) {
        return studentQuestionResultRepository.findByLectureUid(lectureId);
    }

    // 반별 결과 목록 조회
    @Transactional(readOnly = true)
    public List<StudentQuestionResult> getResultsByClassroom(Long classroomId) {
        return studentQuestionResultRepository.findByClassroomUid(classroomId);
    }
}