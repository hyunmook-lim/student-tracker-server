package com.visit.studentTracker.repository;

import com.visit.studentTracker.entity.StudentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentReportRepository extends JpaRepository<StudentReport, Long> {
    
    List<StudentReport> findByStudentUid(Long studentUid);
    
    List<StudentReport> findByReportUid(Long reportUid);
    
    List<StudentReport> findByStudentUidAndIsActive(Long studentUid, boolean isActive);
    
    List<StudentReport> findByReportUidAndIsActive(Long reportUid, boolean isActive);
    
    Optional<StudentReport> findByStudentUidAndReportUid(Long studentUid, Long reportUid);
    
    void deleteByStudentUidAndReportUid(Long studentUid, Long reportUid);
    
    boolean existsByStudentUidAndReportUid(Long studentUid, Long reportUid);
}