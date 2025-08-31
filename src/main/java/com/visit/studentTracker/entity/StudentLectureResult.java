package com.visit.studentTracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "student_lecture_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLectureResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(nullable = false)
    private boolean isAttended;

    @Column(nullable = false)
    private String assignmentScore;

    // 숙제 목록 (String 리스트)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "student_lecture_homework", joinColumns = @JoinColumn(name = "student_lecture_result_id"))
    @Column(name = "homework", nullable = false)
    private List<String> homework;

    @OneToMany(mappedBy = "studentLectureResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentQuestionResult> questionResults;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}