package com.visit.studentTracker.dto.report.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReportRequest {
    private String reportTitle;
    private String reportDescription;
    private Long classroomId;
    private List<Long> lectureIds;
    private List<Long> studentIds;
}