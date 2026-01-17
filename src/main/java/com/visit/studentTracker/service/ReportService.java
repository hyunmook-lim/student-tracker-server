package com.visit.studentTracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visit.studentTracker.dto.report.request.CreateReportRequest;
import com.visit.studentTracker.dto.report.request.UpdateReportRequest;
import com.visit.studentTracker.dto.report.request.UpdateStudentFeedbackRequest;
import com.visit.studentTracker.dto.report.response.ReportResponse;
import com.visit.studentTracker.entity.Classroom;
import com.visit.studentTracker.entity.Lecture;
import com.visit.studentTracker.entity.Report;
import com.visit.studentTracker.entity.ReportLecture;
import com.visit.studentTracker.entity.Student;
import com.visit.studentTracker.entity.StudentReport;
import com.visit.studentTracker.repository.ClassroomRepository;
import com.visit.studentTracker.repository.LectureRepository;
import com.visit.studentTracker.repository.ReportLectureRepository;
import com.visit.studentTracker.repository.ReportRepository;
import com.visit.studentTracker.repository.StudentReportRepository;
import com.visit.studentTracker.repository.StudentRepository;

@Service
@SuppressWarnings("null")
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportLectureRepository reportLectureRepository;
    private final StudentReportRepository studentReportRepository;
    private final ClassroomRepository classroomRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;

    public ReportService(ReportRepository reportRepository,
                        ReportLectureRepository reportLectureRepository,
                        StudentReportRepository studentReportRepository,
                        ClassroomRepository classroomRepository,
                        LectureRepository lectureRepository,
                        StudentRepository studentRepository) {
        this.reportRepository = reportRepository;
        this.reportLectureRepository = reportLectureRepository;
        this.studentReportRepository = studentReportRepository;
        this.classroomRepository = classroomRepository;
        this.lectureRepository = lectureRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public ReportResponse createReport(CreateReportRequest dto) {
        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 반을 찾을 수 없습니다."));

        Report report = Report.builder()
                .reportTitle(dto.getReportTitle())
                .reportDescription(dto.getReportDescription())
                .classroom(classroom)
                .build();

        Report savedReport = reportRepository.save(report);

        if (dto.getLectureIds() != null && !dto.getLectureIds().isEmpty()) {
            List<ReportLecture> reportLectures = dto.getLectureIds().stream()
                    .map(lectureId -> {
                        Lecture lecture = lectureRepository.findById(lectureId)
                                .orElseThrow(() -> new IllegalArgumentException("강의 ID " + lectureId + "를 찾을 수 없습니다."));
                        
                        return ReportLecture.builder()
                                .report(savedReport)
                                .lecture(lecture)
                                .build();
                    })
                    .collect(Collectors.toList());

            reportLectureRepository.saveAll(reportLectures);
        }

        // 학생-성적표 관계 생성
        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            List<StudentReport> studentReports = dto.getStudentIds().stream()
                    .map(studentId -> {
                        Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("학생 ID " + studentId + "를 찾을 수 없습니다."));
                        
                        return StudentReport.builder()
                                .student(student)
                                .report(savedReport)
                                .build();
                    })
                    .collect(Collectors.toList());

            studentReportRepository.saveAll(studentReports);
        }

        return toResponse(savedReport);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long id) {
        return reportRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("해당 성적표를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findByIsActive(true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByClassroom(Long classroomId) {
        return reportRepository.findByClassroomUidAndIsActive(classroomId, true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByStudent(Long studentId) {
        return studentReportRepository.findByStudentUidAndIsActive(studentId, true)
                .stream()
                .map(studentReport -> toResponse(studentReport.getReport()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse updateReport(Long id, UpdateReportRequest dto) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 성적표를 찾을 수 없습니다."));

        if (dto.getReportTitle() != null) {
            report.setReportTitle(dto.getReportTitle());
        }

        if (dto.getReportDescription() != null) {
            report.setReportDescription(dto.getReportDescription());
        }

        if (dto.getLectureIds() != null) {
            // 기존 ReportLecture 관계 삭제
            List<ReportLecture> existingReportLectures = reportLectureRepository.findByReportUid(id);
            reportLectureRepository.deleteAll(existingReportLectures);

            // 새로운 ReportLecture 관계 생성
            List<ReportLecture> newReportLectures = dto.getLectureIds().stream()
                    .map(lectureId -> {
                        Lecture lecture = lectureRepository.findById(lectureId)
                                .orElseThrow(() -> new IllegalArgumentException("강의 ID " + lectureId + "를 찾을 수 없습니다."));
                        
                        return ReportLecture.builder()
                                .report(report)
                                .lecture(lecture)
                                .build();
                    })
                    .collect(Collectors.toList());

            reportLectureRepository.saveAll(newReportLectures);
        }

        if (dto.getStudentIds() != null) {
            // 기존 StudentReport 관계 삭제
            List<StudentReport> existingStudentReports = studentReportRepository.findByReportUid(id);
            studentReportRepository.deleteAll(existingStudentReports);

            // 새로운 StudentReport 관계 생성
            List<StudentReport> newStudentReports = dto.getStudentIds().stream()
                    .map(studentId -> {
                        Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("학생 ID " + studentId + "를 찾을 수 없습니다."));
                        
                        return StudentReport.builder()
                                .student(student)
                                .report(report)
                                .build();
                    })
                    .collect(Collectors.toList());

            studentReportRepository.saveAll(newStudentReports);
        }

        report.setUpdatedAt(LocalDateTime.now());
        return toResponse(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 성적표를 찾을 수 없습니다.");
        }

        Report report = reportRepository.findById(id).get();
        report.setActive(false);
        report.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void updateStudentFeedback(Long reportId, Long studentId, UpdateStudentFeedbackRequest dto) {
        StudentReport studentReport = studentReportRepository.findByStudentUidAndReportUid(studentId, reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생의 성적표 관계를 찾을 수 없습니다."));

        studentReport.setFeedback(dto.getFeedback());
        studentReport.setUpdatedAt(LocalDateTime.now());
    }

    private ReportResponse toResponse(Report report) {
        List<ReportResponse.LectureInfo> lectureInfos = reportLectureRepository.findByReportUidAndIsActive(report.getUid(), true)
                .stream()
                .map(reportLecture -> ReportResponse.LectureInfo.builder()
                        .lectureId(reportLecture.getLecture().getUid())
                        .lectureName(reportLecture.getLecture().getLectureName())
                        .lectureDate(reportLecture.getLecture().getLectureDate())
                        .build())
                .collect(Collectors.toList());

        List<ReportResponse.StudentInfo> studentInfos = studentReportRepository.findByReportUidAndIsActive(report.getUid(), true)
                .stream()
                .map(studentReport -> ReportResponse.StudentInfo.builder()
                        .studentId(studentReport.getStudent().getUid())
                        .studentName(studentReport.getStudent().getName())
                        .feedback(studentReport.getFeedback())
                        .build())
                .collect(Collectors.toList());

        return ReportResponse.builder()
                .uid(report.getUid())
                .reportTitle(report.getReportTitle())
                .reportDescription(report.getReportDescription())
                .classroomId(report.getClassroom().getUid())
                .classroomName(report.getClassroom().getClassroomName())
                .lectures(lectureInfos)
                .students(studentInfos)
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}