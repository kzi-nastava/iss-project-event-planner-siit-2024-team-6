package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.entity.Report;
import lombok.Data;

@Data
public class UserReportDTO {
    private Integer id;
    private Integer reporterId;
    private Integer reportedId;
    private String reason;

    public UserReportDTO(Report report) {
        this.id = report.getId();
        this.reporterId = report.getReporter().getId();
        this.reportedId = report.getReported().getId();
        this.reason = report.getReason();
    }
}

