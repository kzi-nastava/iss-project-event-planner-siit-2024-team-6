package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.entity.Report;
import lombok.Data;

@Data
public class ReportDTO {
    private Integer id;
    private String reporterUsername;
    private String reportedUsername;
    private String reason;

    public ReportDTO(Report report) {
        this.id = report.getId();
        this.reporterUsername = report.getReporter().getUsername();
        this.reportedUsername = report.getReported().getUsername();
        this.reason = report.getReason();
    }
}

