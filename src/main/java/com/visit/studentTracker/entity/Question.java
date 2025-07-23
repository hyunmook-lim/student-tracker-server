package com.visit.studentTracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(nullable = false)
    private Integer number;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String unit; // 단원

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "question_types", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "type")
    private List<String> types; // 유형 리스트

    @Column(nullable = false)
    private String difficulty; // 난이도

    @Column(nullable = false)
    private Integer score; // 배점

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