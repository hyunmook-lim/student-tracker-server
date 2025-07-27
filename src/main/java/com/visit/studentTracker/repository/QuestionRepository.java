package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

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
}