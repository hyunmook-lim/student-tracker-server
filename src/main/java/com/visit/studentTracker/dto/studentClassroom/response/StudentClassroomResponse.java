package com.visit.studentTracker.dto.studentClassroom.response;

import com.visit.studentTracker.dto.student.response.StudentResponse;
import com.visit.studentTracker.dto.classroom.response.ClassroomResponse;
import com.visit.studentTracker.entity.StudentClassroomStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentClassroomResponse {
    private Long uid;
    private StudentResponse student;
    private ClassroomResponse classroom;
    private StudentClassroomStatus status;
}