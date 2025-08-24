package com.visit.studentTracker.service;

import com.visit.studentTracker.dto.lecture.request.CreateLectureRequest;
import com.visit.studentTracker.dto.lecture.request.UpdateLectureRequest;
import com.visit.studentTracker.dto.lecture.response.LectureResponse;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.Question;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final ClassroomRepository classroomRepository;
    private final QuestionRepository questionRepository;

    public LectureService(LectureRepository lectureRepository, ClassroomRepository classroomRepository, QuestionRepository questionRepository) {
        this.lectureRepository = lectureRepository;
        this.classroomRepository = classroomRepository;
        this.questionRepository = questionRepository;
    }

    // CREATE
    @Transactional
    public LectureResponse createLecture(CreateLectureRequest dto) {
        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

        Lecture lecture = Lecture.builder()
                .lectureName(dto.getLectureName())
                .description(dto.getDescription())
                .lectureDate(dto.getLectureDate())
                .classroom(classroom)
                .build();

        Lecture savedLecture = lectureRepository.save(lecture);

        try {
            // 문제 벌크 생성
            if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
                List<Question> questions = dto.getQuestions().stream()
                        .map(questionDto -> Question.builder()
                                .number(questionDto.getNumber())
                                .mainTopic(questionDto.getMainTopic())
                                .subTopic(questionDto.getSubTopic())
                                .answer(questionDto.getAnswer())
                                .difficulty(questionDto.getDifficulty())
                                .score(questionDto.getScore())
                                .isActive(true)
                                .build())
                        .collect(Collectors.toList());

                questionRepository.saveAll(questions);
            }
        } catch (Exception e) {
            // 문제 생성 실패 시 트랜잭션 롤백을 위해 런타임 예외로 변환
            throw new RuntimeException("문제 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

        return toResponse(savedLecture);
    }


    // READ (단건)
    @Transactional(readOnly = true)
    public LectureResponse getLecture(Long id) {
        return lectureRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));
    }

    // READ (전체)
    @Transactional(readOnly = true)
    public List<LectureResponse> getAllLectures() {
        return lectureRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (반별 강의 목록)
    @Transactional(readOnly = true)
    public List<LectureResponse> getLecturesByClassroom(Long classroomId) {
        return lectureRepository.findByClassroomUid(classroomId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public LectureResponse updateLecture(Long id, UpdateLectureRequest dto) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        if (dto.getLectureName() != null) {
            lecture.setLectureName(dto.getLectureName());
        }

        if (dto.getDescription() != null) {
            lecture.setDescription(dto.getDescription());
        }

        if (dto.getLectureDate() != null) {
            lecture.setLectureDate(dto.getLectureDate());
        }

        lecture.setUpdatedAt(LocalDateTime.now());

        return toResponse(lecture);
    }

    // DELETE
    @Transactional
    public void deleteLecture(Long id) {
        if (!lectureRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 강의를 찾을 수 없습니다.");
        }
        lectureRepository.deleteById(id);
    }

    // DTO 변환 메서드
    private LectureResponse toResponse(Lecture lecture) {
        return LectureResponse.builder()
                .uid(lecture.getUid())
                .lectureName(lecture.getLectureName())
                .description(lecture.getDescription())
                .lectureDate(lecture.getLectureDate())
                .classroomId(lecture.getClassroom().getUid())
                .className(lecture.getClassroom().getClassroomName())
                .isActive(lecture.isActive())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .build();
    }
}