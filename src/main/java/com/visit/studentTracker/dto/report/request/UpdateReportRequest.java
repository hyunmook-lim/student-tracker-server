package com.visit.studentTracker.dto.report.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReportRequest {
    private String reportTitle;
    private String reportDescription;
    private List<Long> lectureIds;
    private List<Long> studentIds;
}