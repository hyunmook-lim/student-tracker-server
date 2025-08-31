package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.ReportLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportLectureRepository extends JpaRepository<ReportLecture, Long> {
    
    List<ReportLecture> findByReportUid(Long reportUid);
    
    List<ReportLecture> findByLectureUid(Long lectureUid);
    
    List<ReportLecture> findByReportUidAndIsActive(Long reportUid, boolean isActive);
    
    void deleteByReportUidAndLectureUid(Long reportUid, Long lectureUid);
}