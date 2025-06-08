package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.user.NewReportDTO;
import ftn.siit.project.isspoject.dto.user.ReportDTO;
import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.interfaces.ReportService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<ReportDTO> reportUser(@RequestBody NewReportDTO userReportDTO) {
        if (userReportDTO == null || userReportDTO.getReporterId() == null || userReportDTO.getReportedId() == null) {
            throw new IllegalArgumentException("Not all arguments were given while reporting user");
        }
        Report report = new Report(userReportDTO);
        User reporter = userService.findById(userReportDTO.getReporterId());
        User reported = userService.findById(userReportDTO.getReportedId());
        report.setReporter(reporter);
        report.setReported(reported);
        Report savedReport = reportService.save(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReportDTO(savedReport));
    }

    @GetMapping("")
    public ResponseEntity<Page<ReportDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Report> reportsPage = reportService.findAllPending(pageable);
        Page<ReportDTO> dtosPage = reportsPage.map(ReportDTO::new);

        return ResponseEntity.ok(dtosPage);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, String>> approveReport(@PathVariable Integer id) {
        Report report = reportService.findById(id);
        if (report == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Report not found."));
        }

        User reportedUser = report.getReported();
        reportedUser.setSuspendedSince(LocalDateTime.now());
        userService.save(reportedUser);
        reportService.acceptReport(report.getId());

        return ResponseEntity.ok(Map.of("message", "Report approved and user suspended for 3 days."));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> rejectReport(@PathVariable Integer id) {
        Report report = reportService.findById(id);
        if (report == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Report not found."));
        }
        reportService.rejectReport(report.getId());
        return ResponseEntity.ok(Map.of("message", "Report rejected and deleted."));
    }

}
