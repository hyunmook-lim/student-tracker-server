package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByClassroomUid(Long classroomUid);
    
    List<Report> findByClassroomUidAndIsActive(Long classroomUid, boolean isActive);
    
    List<Report> findByIsActive(boolean isActive);
}