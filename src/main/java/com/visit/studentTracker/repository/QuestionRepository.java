package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    boolean existsByNumber(Integer number);

    Optional<Question> findByNumber(Integer number);

    List<Question> findByMainTopic(String mainTopic);

    List<Question> findBySubTopic(String subTopic);

    List<Question> findByDifficulty(String difficulty);

    List<Question> findByMainTopicAndSubTopic(String mainTopic, String subTopic);

    List<Question> findByMainTopicAndSubTopicAndDifficulty(String mainTopic, String subTopic, String difficulty);
    
    @Query("SELECT q.number FROM Question q WHERE q.number IN :numbers")
    List<Integer> findNumbersByNumbers(@Param("numbers") List<Integer> numbers);
    
    List<Question> findByLectureUid(Long lectureUid);
    
    List<Question> findByLectureUidAndDifficulty(Long lectureUid, String difficulty);
    
    List<Question> findByLectureUidAndMainTopic(Long lectureUid, String mainTopic);
    
    List<Question> findByLectureUidAndSubTopic(Long lectureUid, String subTopic);
}