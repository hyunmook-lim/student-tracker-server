package com.visit.studentTracker.entity;

public enum StudentClassroomStatus {
    WAITING("대기"),
    APPROVED("승인"),
    REJECTED("거부");

    private final String description;

    StudentClassroomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
